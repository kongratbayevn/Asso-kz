package kz.app.asso.system.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import kz.app.asso.system.dto.EventLogs
import lombok.AccessLevel
import lombok.Getter
import lombok.Setter
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.io.Serializable
import java.sql.Timestamp
import java.util.ArrayList
import javax.persistence.Column
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@TypeDefs(
    TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType::class
    )
)
@JsonIgnoreProperties(value = ["deletedAt", "eventLog"], allowGetters = false)
abstract class Auditable<U> : Serializable {

    @CreatedDate
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis())
        private set

    @CreatedBy
    protected var creator: U? = null

    @LastModifiedDate
    @Column(name="updated_at")
    var updatedAt: Timestamp? = null

    @LastModifiedBy
    protected var editor: U? = null

    @Column(name="deleted_at")
    var deletedAt: Timestamp? = null

    @Type(type = "jsonb")
    @Column(name = "event_log", columnDefinition = "jsonb")
    var eventLog: ArrayList<EventLogs>? = arrayListOf()
        get() {
            return field?: arrayListOf()
        }
        private set

    fun addLog(value: EventLogs) {
        if (eventLog != null) {
            eventLog!!.add(value)
        } else {
            eventLog = arrayListOf(value)
        }
    }

}
