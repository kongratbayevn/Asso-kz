package kz.app.asso.auth.model

import kz.app.asso.system.model.Auditable
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.sql.Timestamp
import java.util.*

@Table(name = "USERS")
class User : Auditable<String?>() {

    @Id
    var id: UUID? = null

    //Primary Auth
    var email: String? = null
    var phone: String = ""
        set(value) {
            field = if (!value.contains("+")) {
                "+${value.replace(" ", "")}"
            } else {
                value.replace(" ", "")
            }
        }

    var code: String? = null
    var password: String? = null

    //claims
    var roles: List<String> = listOf()

    // User Info
    var firstName: String? = null
    var lastName: String? = null
    var avatar: UUID? = null

    //Authority
    var expiredCode: Timestamp? = null
    var blockCodeSend: Timestamp? = null
    var loginAttempts: Int? = 0
        get() { return field ?: 0 }
    var isBlocked: Timestamp? = null
    var receivingFailedCountCode: Int? = 0
        get() { return field ?: 0 }

}
