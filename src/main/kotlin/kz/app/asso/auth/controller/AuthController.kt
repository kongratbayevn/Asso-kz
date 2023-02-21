package kz.app.asso.auth.controller

import kz.app.asso.auth.dto.AuthResultDto
import kz.app.asso.auth.dto.UserLoginDto
import kz.app.asso.auth.service.security.SecurityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class AuthController {

    @Autowired
    lateinit var securityService: SecurityService

    @PostMapping("/oauth/token", consumes = ["application/x-www-form-urlencoded"])
    fun login(dto: UserLoginDto): Mono<ResponseEntity<AuthResultDto>> {
        return securityService.authenticate(dto.username, dto.password)
            .flatMap { tokenInfo ->
                Mono.just(
                    ResponseEntity.ok(
                        AuthResultDto(
                            tokenInfo.token,
                            "bearer",
                            null,
                            tokenInfo.expiresAt
                        )
                    )
                )
            }
    }
}
