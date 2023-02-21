package kz.app.asso.auth.configuration.security.support

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import kz.app.asso.auth.configuration.security.auth.UnauthorizedException
import reactor.core.publisher.Mono
import java.util.*

class JwtVerifyHandler(private val secret: String) {
    fun check(accessToken: String): Mono<VerificationResult> {
        return Mono.just(verify(accessToken))
            .onErrorResume { e: Throwable ->
                Mono.error(
                    UnauthorizedException(e.message)
                )
            }
    }

    private fun verify(token: String): VerificationResult {
        val claims = getAllClaimsFromToken(token)
        val expiration = claims.expiration
        if (expiration.before(Date())) throw RuntimeException("Token expired")
        return VerificationResult(claims, token)
    }

    private fun getAllClaimsFromToken(token: String?): Claims {
        return Jwts.parser()
            .setSigningKey(Base64.getEncoder().encodeToString(secret.toByteArray()))
            .parseClaimsJws(token)
            .body
    }

    inner class VerificationResult(var claims: Claims, var token: String)
}
