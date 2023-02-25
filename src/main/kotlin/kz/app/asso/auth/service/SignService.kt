package kz.app.asso.auth.service

import kz.app.asso.auth.dto.UserDto
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.payload.UserRequest
import kz.app.asso.auth.repository.UserRepository
import kz.app.asso.sms.dto.SmsRequest
import kz.app.asso.sms.service.SmsService
import kz.app.asso.system.dto.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.util.*


@Service
class SignService {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var smsService: SmsService

    @Value("\${spring.profiles.active}")
    val statusApp: String? = null

    fun signInSms(userDto: UserDto): Mono<Status> {
        val userRequest = UserRequest(userDto)
        userRequest.roles = arrayListOf("ROLE_USER")
        val status = randomCodeUser(userRequest)

        if (statusApp == "production") {
            try {
                status.flatMap { sts ->
                    val smsBody = SmsRequest()
                    smsBody.messageData = "ASSO-KZ. Никому не сообщайте! Код подтверждения: ${sts.message}"
                    smsBody.recipient = userDto.phone.replace("+", "")

                    smsService.send(smsBody).map { resultSms ->
                        sts.message = sts.value.toString()
                        sts.value = resultSms
                    }
                }
            } catch (e: Exception) {
                status.map {
                    it.status = 0
                    it.value = e.message
                    it.message = ""
                }
            }
        }

        return status
    }

    fun phoneUpdateGenerateCode(userRequest: Any): Mono<Status> {
        return Mono.just(Status())
    }

    fun phoneUpdateConfirm(confirmCode: Any): Mono<Status> {
        return Mono.just(Status())
    }

    fun randomCodeUser(userRequest: UserRequest): Mono<Status> {
        var status = Mono.just(Status())
        if (userRequest.phone.isNotBlank()) {
            val random = Random()
            val randomCode = String.format("%04d", random.nextInt(10000))
            println("random code = $randomCode")

            try {
                status = userService.createOrUpdateUser(userRequest, randomCode).map {
                    Status(1, randomCode, it.id)
                }
            } catch (ex: Exception) {
                status.map {
                    it.message = ex.message.toString()
                }
            }
        }

        return status
    }

}
