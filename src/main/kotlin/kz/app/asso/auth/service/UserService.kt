package kz.app.asso.auth.service

import kz.app.asso.auth.dto.UserDto
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.payload.UserRequest
import kz.app.asso.auth.repository.UserRepository
import kz.app.asso.auth.service.security.SecurityService
import kz.app.asso.system.dto.Status
import kz.app.asso.system.exceptionHandler.ApiException
import kz.app.asso.system.exceptionHandler.UserNotFoundException
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.util.UUID

@Slf4j
@Service
class UserService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    fun getUserByPhone(phone: String): Mono<User> {
        return userRepository.findByPhoneIgnoreCaseAndDeletedAtIsNull(phone)
    }

    fun createUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    fun createOrUpdateUser(userRequest: UserRequest, randomCode: String): Mono<User> {
        val user = userRequest.getUser()
        user.id = UUID.randomUUID()
        user.code = passwordEncoder.encode(randomCode)
        // 900000 -> 15 minutes;    600000 -> 10 minutes;   300000 -> 5 minutes
        user.expiredCode = Timestamp(System.currentTimeMillis() + 300000)
        return createOrUpdateUser(user)
    }

    fun createOrUpdateUser(user: User): Mono<User> {
        return getUserByPhone(user.phone).flatMap { u ->
            val currentTime = Timestamp(System.currentTimeMillis())
            if ((u.isBlocked != null && u.isBlocked!!.after(currentTime))
                || (u.blockCodeSend != null && u.blockCodeSend!!.after(currentTime))
            ) {
                return@flatMap Mono.error(ApiException("Your account blocked", "403"))
            }
            u.expiredCode = user.expiredCode
            u.receivingFailedCountCode = u.receivingFailedCountCode!! + 1
            if (u.receivingFailedCountCode!! > 3) {
                u.isBlocked = Timestamp(System.currentTimeMillis() + 1800000)
                u.blockCodeSend = Timestamp(System.currentTimeMillis() + 1800000)
            } else if (user.code == null) {
                u.receivingFailedCountCode = 0
            }
            u.code = user.code

            userRepository.save(u)
        }.switchIfEmpty(userRepository.save(user))
    }

    fun getList(pageR: PageRequest, params: MutableMap<String, String>): Page<UserRequest> {
        return Page.empty()
    }

    fun getAccountById(id: UUID): Mono<UserRequest> {
        return userRepository.findByIdAndDeletedAtIsNull(id)
            .switchIfEmpty(Mono.error(UserNotFoundException("User Not Found", "404")))
            .map { user -> UserRequest(user) }
    }

    fun findByName(name: String): Mono<UserRequest> {
        return getUserByPhone(name)
            .switchIfEmpty(Mono.error(UserNotFoundException("User Not Found", "404")))
            .map { user -> UserRequest(user) }
    }

    fun saveChanges(username: String, user: UserRequest): Mono<Status> {
        return getUserByPhone(username).map { u ->

            u.firstName = user.firstName ?: u.firstName
            u.lastName = user.lastName ?: u.lastName
            u.email = user.email ?: u.email
            u.avatar = user.avatar ?: u.avatar

            userRepository.save(u)

            Status(1, "Success")
        }.switchIfEmpty(Mono.error(UserNotFoundException("User Not Found", "404")))
    }

    fun create(user: UserRequest): Mono<Status> {
        return Mono.just(Status())
    }

    fun moveToTrash(id: UUID): Mono<Status> {
        return userRepository.findById(id)
            .map { u ->
                u.deletedAt = Timestamp(System.currentTimeMillis())
                userRepository.save(u)

                Status(1, "Success")
            }.switchIfEmpty(Mono.error(UserNotFoundException("User Not Found", "404")))
    }

    fun moveToTrash(username: String): Mono<Status> {
        return getUserByPhone(username).map { u ->
            u.deletedAt = Timestamp(System.currentTimeMillis())
            userRepository.save(u)

            Status(1, "Success")
        }.switchIfEmpty(Mono.error(UserNotFoundException("User Not Found", "404")))
    }
}
