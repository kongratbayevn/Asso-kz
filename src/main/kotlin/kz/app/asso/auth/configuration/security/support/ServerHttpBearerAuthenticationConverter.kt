package kz.app.asso.auth.configuration.security.support

import kz.app.asso.auth.configuration.security.auth.CurrentUserAuthenticationBearer
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.function.Predicate

class ServerHttpBearerAuthenticationConverter(private val jwtVerifier: JwtVerifyHandler) :
    Function<ServerWebExchange?, Mono<Authentication>> {
    override fun apply(serverWebExchange: ServerWebExchange?): Mono<Authentication> {
        return Mono.justOrEmpty<ServerWebExchange>(serverWebExchange)
            .flatMap { serverWebExchange: ServerWebExchange ->
                extract(
                    serverWebExchange
                )
            }
            .filter(matchBearerLength)
            .flatMap(isolateBearerValue)
            .flatMap { accessToken: String? ->
                println(accessToken)
                jwtVerifier.check(
                    accessToken!!
                )
            }
            .flatMap(CurrentUserAuthenticationBearer::create)
    }

    companion object {
        private const val BEARER = "Bearer "
        private val matchBearerLength =
            Predicate { authValue: String -> authValue.length > BEARER.length }
        private val isolateBearerValue =
            Function { authValue: String ->
                Mono.just(
                    authValue.substring(BEARER.length)
                )
            }

        fun extract(serverWebExchange: ServerWebExchange): Mono<String> {
            return Mono.justOrEmpty(
                serverWebExchange.request
                    .headers
                    .getFirst(HttpHeaders.AUTHORIZATION)
            )
        }
    }
}
