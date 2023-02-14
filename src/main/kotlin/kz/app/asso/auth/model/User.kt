package kz.app.asso.auth.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kz.app.asso.system.model.Auditable
import org.hibernate.annotations.*
import java.sql.Timestamp
import java.util.*
import javax.persistence.*
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.Table
import kotlin.jvm.Transient


@Entity(name = "USERS")
@Table(name = "USERS")
@JsonIgnoreProperties(value = ["rolesCollection"], allowGetters = false, allowSetters = true)
class User(): Auditable<String?>() {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    var id: UUID? = null

    @Column(name = "phone", unique = true, nullable = false)
    var phone: String = ""
        set(value) {
            field = if (!value.contains("+")) {
                "+${value.replace(" ", "")}"
            } else {
                value.replace(" ", "")
            }
        }

    @Column(name = "CODE", columnDefinition = "character varying")
    var code: String? = null

    @Column(name = "EXPIRED_CODE")
    var expiredCode: Timestamp? = null

    @Column(name = "RECEIVING_FAILED_COUNT_CODE")
    var receivingFailedCountCode: Int? = 0
        get() {
            return field?:0
        }

    @Column(name = "BLOCK_CODE_SEND")
    var blockCodeSend: Timestamp? = null

    @Column(name = "LANGUAGE", columnDefinition = "character varying")
    var language: String? = "RU"

    @Column(name = "LOGIN_ATTEMPTS")
    var loginAttempts: Int? = 0
        get() {
            return if (field != null)
                field
            else 0
        }

    @Column(name = "IS_BLOCKED")
    var isBlocked: Timestamp? = null

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinTable(name = "USERS_ROLES",
        joinColumns = [JoinColumn(name = "USER_ID", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "ROLE_ID", referencedColumnName = "id")])
    var rolesCollection: Collection<Role> = listOf()

    @Transient
    var roles: List<String> = arrayListOf()
        get() {
            field = arrayListOf()
            for (item in rolesCollection) {
                field = field.plus(item.name)
            }
            return field
        }
        private set

}
