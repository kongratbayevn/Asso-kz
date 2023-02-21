package kz.app.asso.auth.service.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.payload.TokenInfo
import kz.app.asso.auth.repository.UserRepository
import kz.app.asso.auth.service.UserService
import kz.app.asso.system.exceptionHandler.AuthException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Component
class SecurityService {

    @Autowired
    lateinit var userService: UserService
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Value("\${jwt.secret}")
    private val secret: String? = null

    @Value("\${jwt.expiration}")
    private val defaultExpirationTimeInSecondsConf: String? = null


    fun generateAccessToken(user: User): TokenInfo {
        val claims: HashMap<String, Any> = hashMapOf("role" to user.roles)
        return doGenerateToken(claims, user.phone, user.id.toString())
    }

    private fun doGenerateToken(claims: Map<String, Any>, issuer: String, subject: String): TokenInfo {
        val expirationTimeInMilliseconds = defaultExpirationTimeInSecondsConf!!.toLong() * 1000
        val expirationDate = Date(Date().time + expirationTimeInMilliseconds)
        return doGenerateToken(expirationDate, claims, issuer, subject)
    }

    private fun doGenerateToken(
        expirationDate: Date,
        claims: Map<String, Any>,
        issuer: String,
        subject: String
    ): TokenInfo {
        val createdDate = Date()
        val token = Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setSubject(subject)
            .setIssuedAt(createdDate)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, Base64.getEncoder().encodeToString(secret!!.toByteArray()))
            .compact()
        return TokenInfo(
            userId = null,
            token,
            createdDate,
            expirationDate
        )
    }

    fun authenticate(username: String, password: String?): Mono<TokenInfo> {
        return userService.getUserByPhone(username)
            .flatMap { user ->
                if (
                    user.isBlocked != null
                    || user.deletedAt != null
                ) return@flatMap Mono.error(
                    AuthException(
                        "Account disabled.",
                        "USER_ACCOUNT_DISABLED"
                    )
                )
                if (passwordEncoder.encode(password) != user.code) return@flatMap Mono.error(
                    AuthException(
                        "Invalid user password!",
                        "INVALID_USER_PASSWORD"
                    )
                )
                user.code = null
                userService.createOrUpdateUser(user)

            }
            .flatMap {  user ->
                val tokenInfo = generateAccessToken(user)
                tokenInfo.userId = user.id
                Mono.just(tokenInfo)
            }
            .switchIfEmpty(Mono.error(AuthException("Invalid user, $username is not registered.", "INVALID_USERNAME")))
    }
}
