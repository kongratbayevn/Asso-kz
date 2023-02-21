package kz.app.asso.auth.configuration.security.auth

import kz.app.asso.auth.configuration.security.support.JwtVerifyHandler
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import reactor.core.publisher.Mono
import java.util.*


object CurrentUserAuthenticationBearer {
    fun create(verificationResult: JwtVerifyHandler.VerificationResult): Mono<Authentication> {
        val claims = verificationResult.claims
        val subject = claims.subject
        val roles: List<*> = claims.get("role", List::class.java)
        val authorities = roles.map { role ->
                SimpleGrantedAuthority(
                    role.toString()
                )
            }

        var principalId: UUID? = null
        try {
            principalId = UUID.fromString(subject.toString())
        } catch (ignore: NumberFormatException) {
        }
        if (principalId == null) return Mono.empty() // invalid value for any of jwt auth parts
        val principal = UserPrincipal(principalId, claims.issuer)
        return Mono.justOrEmpty(UsernamePasswordAuthenticationToken(principal, null, authorities))
    }
}
