package kz.app.asso.auth.controller


import kz.app.asso.auth.dto.UserDto
import kz.app.asso.auth.model.payload.UserPhoneUpdateRequest
import kz.app.asso.auth.service.SignService
import kz.app.asso.system.dto.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/sms")
class UserSmsController {

    @Autowired
    lateinit var signService: SignService

    @PostMapping("/sign-up/generate")
    fun signInSms(@Valid @RequestBody userRequest: UserDto): Mono<Status> {
        return signService.signInSms(userRequest)
    }

    @PostMapping("/phone/change/generate")
    @PreAuthorize("isAuthenticated()")
    fun phoneUpdateGenerateCode(@Valid @RequestBody userRequest: UserDto): Mono<Status> {
        return signService.phoneUpdateGenerateCode(userRequest)
    }

    @PostMapping("/phone/change")
    @PreAuthorize("isAuthenticated()")
    fun phoneUpdateConfirm(@Valid @RequestBody confirmCode: UserPhoneUpdateRequest): Mono<Status> {
        return signService.phoneUpdateConfirm(confirmCode)
    }
}
