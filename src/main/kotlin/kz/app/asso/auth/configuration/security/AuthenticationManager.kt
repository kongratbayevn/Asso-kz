package kz.app.asso.auth.configuration.security

import kz.app.asso.auth.configuration.security.auth.UnauthorizedException
import kz.app.asso.auth.configuration.security.auth.UserPrincipal
import kz.app.asso.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class AuthenticationManager : ReactiveAuthenticationManager {

    @Autowired
    lateinit var userService: UserService


    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val principal: UserPrincipal = authentication.principal as UserPrincipal

        //TODO add more user validation logic here.
        return userService.getUserByPhone(principal.name)
            .filter { user -> (user.isBlocked == null) }
            .switchIfEmpty(Mono.error(UnauthorizedException("User account is disabled.")))
            .map { authentication }
    }
}

