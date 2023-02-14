package kz.app.asso.auth.service

import io.jsonwebtoken.lang.Assert
import kz.app.asso.system.dto.Status
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.UserPhoneUpdate
import kz.app.asso.auth.model.UsersRoles
import kz.app.asso.auth.model.payload.NewUserRequest
import kz.app.asso.auth.model.payload.UserPhoneUpdateRequest
import kz.app.asso.auth.repository.RoleRepository
import kz.app.asso.auth.repository.UserPhoneUpdateRepository
import kz.app.asso.auth.repository.UserRepository
import kz.app.asso.auth.repository.UsersRolesRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.*

@Service
class UserServiceImpl: UserService {

    private val log = LoggerFactory.getLogger(javaClass)

    private val encoder = BCryptPasswordEncoder()

    @Autowired
    lateinit var repository: UserRepository

    @Autowired
    lateinit var phoneRepository: UserPhoneUpdateRepository

    @Autowired
    lateinit var usersRoles: UsersRolesRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    override fun create(user: NewUserRequest): UUID? {
        if (user.phone.isNotBlank()) {
            val existing = repository.findByPhoneIgnoreCase(user.phone)
            existing.ifPresent { throw IllegalArgumentException("user already exists: " + it.phone) }

            val newUser = User()
            newUser.phone = user.phone

            repository.save(newUser)
            if (newUser.id != null) {
                if (user.roles.isNotEmpty()) {
                    for (role in user.roles) {
                        val roleCandidate = roleRepository.findByNameIgnoreCaseAndDeletedAtIsNull(role.uppercase())
                        if (roleCandidate.isPresent) {
                            val newRole = UsersRoles()
                            newRole.userId = newUser.id
                            newRole.roleId = roleCandidate.get().id
                            usersRoles.save(newRole)
                        }
                    }
                }
                log.info("new user has been created: {}", user.phone)
                return newUser.id!!
            }
        }
        return null
    }

    override fun getUserById(id: UUID): Optional<User> {
        return repository.findById(id)
    }

    override fun getUserByPhone(phone: String): Optional<User> {
        return repository.findByPhoneIgnoreCase(phone)
    }

    override fun getUserListByIds(ids: List<UUID>): ArrayList<User> {
        return repository.findAllByIdInAndDeletedAtIsNull(ids)
    }

    override fun moveToTrash(id: UUID): Status {
        val status = Status()
        val user = repository.findById(id)
        if (user.isPresent) {
            user.get().phone = "${user.get().phone}_deleted_${System.currentTimeMillis()}"
            user.get().deletedAt = Timestamp(System.currentTimeMillis())
            repository.save(user.get())

            status.status = 1
        }
        return status
    }

    override fun delete(id: UUID): Status {
        val status = Status()
        return status
    }

    override fun randomCodeUser(userRequest: NewUserRequest): Status {
        val status = Status()
        if (userRequest.phone.isBlank()) {
            return status
        }

        val randomCode = String.format("%04d", Random().nextInt(10000))
        getUserByPhone(userRequest.phone).ifPresentOrElse({ user ->
            if (
                (user.isBlocked == null || user.isBlocked!!.before(Timestamp(System.currentTimeMillis())))
                && (user.blockCodeSend == null || user.blockCodeSend!!.before(Timestamp(System.currentTimeMillis())))
            ) {
                user.code = encoder.encode(randomCode)
                // 900000 -> 15 minutes;    600000 -> 10 minutes;   300000 -> 5 minutes
                user.expiredCode = Timestamp(System.currentTimeMillis() + 300000)
                user.receivingFailedCountCode = user.receivingFailedCountCode!! + 1
                if (user.receivingFailedCountCode!! > 3) {
                    user.blockCodeSend = Timestamp(System.currentTimeMillis() + 1800000)
                }
                repository.save(user)

                status.value = user.id
                status.status = 1
                status.message = "Code generated"
                // TODO алып тастау
                status.message = randomCode
            }
        }, {
            val newUser = User()
            newUser.phone = userRequest.phone
            newUser.code = encoder.encode(randomCode)
            // 900000 -> 15 minutes;    600000 -> 10 minutes;   300000 -> 5 minutes
            newUser.expiredCode = Timestamp(System.currentTimeMillis() + 300000)
            repository.save(newUser)

            status.value = newUser.id
            status.status = 1
            status.message = "Code generated"
            // TODO алып тастау
            status.message = randomCode

            if (userRequest.roles.isNotEmpty()) {
                for (role in userRequest.roles) {
                    val roleCandidate = roleRepository.findByNameIgnoreCaseAndDeletedAtIsNull(role.uppercase())
                    if (roleCandidate.isPresent) {
                        val newRole = UsersRoles()
                        newRole.userId = newUser.id
                        newRole.roleId = roleCandidate.get().id
                        usersRoles.save(newRole)
                    }
                }
            }
        })

        println("random code = $randomCode")
        status.value = randomCode
        return status
    }

    override fun resetPassword(username: String) {
        val user = repository.findByPhoneIgnoreCase(username)
        user.ifPresent {
            it.code = encoder.encode("kD5PBY3p4twn5Z7mrJeAdWTbLYFa57wXmhspx2Cjv6Mcq8y8zNS2qBD9tXu2e9f")
            it.expiredCode = null
            it.isBlocked = null
            it.loginAttempts = 0
            it.receivingFailedCountCode = 0
            repository.save(it)
        }
    }

    override fun failedAuth(username: String) {
        val user = repository.findByPhoneIgnoreCase(username)
        user.ifPresent {
            it.loginAttempts = (it.loginAttempts?:0) + 1
            if (it.loginAttempts!! > 3) {
                //10800000 -> 3 hours
                it.isBlocked = Timestamp(System.currentTimeMillis() + 10800000)
            }
            repository.save(it)
        }
    }

    override fun phoneUpdateGenerateCode(userRequest: NewUserRequest): Status {
        val status = Status()
        Assert.notNull(userRequest.id, "User is missing")

        if (userRequest.phone.isNotBlank()) {
            val user = getUserById(userRequest.id!!)

            val random = Random()
            val randomCode = String.format("%04d", random.nextInt(10000))

            println("random code = $randomCode")

            user.ifPresentOrElse(
                {
                    val newPhoneReq = UserPhoneUpdate()
                    newPhoneReq.userId = it.id
                    newPhoneReq.oldPhone = it.phone
                    newPhoneReq.code = encoder.encode(randomCode)
                    newPhoneReq.newPhone = userRequest.phone
                    // TODO expiredCode
                    phoneRepository.save(newPhoneReq)

                    status.value = newPhoneReq.id
                    status.status = 1
                    status.message = "Code generated"
                    // TODO алып тастау
                    status.message = randomCode
                },
                {
                    status.message = "Code Not Generated, Can not find user"
                }
            )
        }
        return status
    }


    override fun phoneUpdateConfirm(confirmCode: UserPhoneUpdateRequest): Status {
        val status = Status()
        Assert.notNull(confirmCode.id, "Request is missing ")
        Assert.notNull(confirmCode.userId, "Request is missing ")

        phoneRepository.findById(confirmCode.id!!).ifPresentOrElse(
            {
                if(it.userId != confirmCode.userId) {
                    status.message = "You don't have access"
                    return@ifPresentOrElse
                }

                if(!encoder.matches(confirmCode.code, it.code)) {
                    status.message = "Code incorrect"
                    return@ifPresentOrElse
                }

                repository.findByPhone(it.oldPhone).ifPresent {user ->
                    user.code = encoder.encode(confirmCode.code)
                    user.expiredCode = Timestamp(System.currentTimeMillis() + 300000)
                    user.phone = it.newPhone
                    repository.save(user)
                }

                it.confirmed = true
                it.code = null
                phoneRepository.save(it)

                // TODO value
                status.value = it.newPhone
                status.status = 1
                status.message = "Successfully change"
            },
            {
                status.message = "Request Not Found"
            }
        )
        return status
    }

}
