package kz.app.asso.auth.configuration.security.support

import kz.app.asso.auth.configuration.security.auth.CurrentUserAuthenticationBearer
import org.springframework.security.core.Authentication
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.function.Predicate

class ServerHttpCookieAuthenticationConverter(private val jwtVerifier: JwtVerifyHandler) :
    Function<ServerWebExchange?, Mono<Authentication>> {
    override fun apply(serverWebExchange: ServerWebExchange?): Mono<Authentication> {
        return Mono.justOrEmpty<ServerWebExchange>(serverWebExchange)
            .flatMap<String?> { serverWebExchange: ServerWebExchange ->
                extract(
                    serverWebExchange
                )
            }
            .flatMap<JwtVerifyHandler.VerificationResult> { accessToken: String? ->
                jwtVerifier.check(
                    accessToken!!
                )
            }
            .flatMap<Authentication>(CurrentUserAuthenticationBearer::create)
    }

    companion object {
        private const val BEARER = "Bearer "
        private val matchBearerLength =
            Predicate { authValue: String -> authValue.length > BEARER.length }
        private val isolateBearerValue =
            Function { authValue: String ->
                Mono.justOrEmpty(
                    authValue.substring(BEARER.length)
                )
            }

        fun extract(serverWebExchange: ServerWebExchange): Mono<String?> {
            val cookieSes = serverWebExchange.request
                .cookies
                .getFirst("X-Session-Id")
            return if (cookieSes != null) Mono.justOrEmpty(cookieSes.value) else Mono.empty()
        }
    }
}
