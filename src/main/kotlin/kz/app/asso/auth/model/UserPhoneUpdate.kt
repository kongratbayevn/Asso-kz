package kz.app.asso.auth.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kz.app.asso.system.model.Auditable
import org.hibernate.annotations.GenericGenerator
import java.util.*
import javax.persistence.*

@Entity(name = "USER_PHONE_UPDATE")
@Table(name = "USER_PHONE_UPDATE")
@JsonIgnoreProperties(value = ["rolesCollection"], allowGetters = false, allowSetters = true)
class UserPhoneUpdate: Auditable<String?>() {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID? = null

    @Column(name = "CODE", columnDefinition = "character varying")
    var code: String? = null

    var userId: UUID? = null

    @Column(name = "old_phone", nullable = false)
    var oldPhone: String = ""
        set(value) {
            field = value.lowercase().trim()
        }

    @Column(name = "new_phone", nullable = false)
    var newPhone: String = ""
        set(value) {
            field = value.lowercase().trim()
        }

    var confirmed = false
}
