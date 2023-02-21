package kz.app.asso.auth.configuration.security.support

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

@Component
class PBKDF2Encoder : PasswordEncoder {
    @Value("\${jwt.password.encoder.secret}")
    private val secret: String? = null

    @Value("\${jwt.password.encoder.iteration}")
    private val iteration: Int? = null

    @Value("\${jwt.password.encoder.key_length}")
    private val keyLength: Int? = null

    override fun encode(cs: CharSequence): String {
        return try {
            val result = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
                .generateSecret(
                    PBEKeySpec(
                        cs.toString().toCharArray(), secret!!.toByteArray(), iteration!!,
                        keyLength!!
                    )
                )
                .encoded
            Base64.getEncoder()
                .encodeToString(result)
        } catch (ex: NoSuchAlgorithmException) {
            throw RuntimeException(ex)
        } catch (ex: InvalidKeySpecException) {
            throw RuntimeException(ex)
        }
    }

    override fun matches(cs: CharSequence, string: String): Boolean {
        return encode(cs) == string
    }
}
