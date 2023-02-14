package kz.app.asso.auth.controller

import kz.app.asso.system.dto.Status
import kz.app.asso.auth.model.payload.NewUserRequest
import kz.app.asso.auth.model.payload.UserPhoneUpdateRequest
import kz.app.asso.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid



@RestController
@RequestMapping("/sms-in")
class SignController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping("/generate-code")
    @PreAuthorize("#oauth2.hasScope('server')")
    fun generateCode(@Valid @RequestBody userRequest: NewUserRequest): Status {
        return userService.randomCodeUser(userRequest)
    }

    @PostMapping("/phone/change/generate")
    @PreAuthorize("#oauth2.hasScope('server')")
    fun phoneUpdateGenerateCode(@Valid @RequestBody userRequest: NewUserRequest): Status {
        return userService.phoneUpdateGenerateCode(userRequest)
    }

    @PostMapping("/phone/change")
    @PreAuthorize("#oauth2.hasScope('server')")
    fun phoneUpdateConfirm(@Valid @RequestBody confirmCode: UserPhoneUpdateRequest): Status {
        return userService.phoneUpdateConfirm(confirmCode)
    }
}
