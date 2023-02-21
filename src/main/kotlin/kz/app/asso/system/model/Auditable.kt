package kz.app.asso.system.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import lombok.AccessLevel
import lombok.Getter
import lombok.Setter
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import java.io.Serializable
import java.sql.Timestamp
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
@EnableR2dbcAuditing(auditorAwareRef = "auditorAware")
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@JsonIgnoreProperties(value = ["deletedAt", "eventLog"], allowGetters = false)
abstract class Auditable<U> : Serializable {

    @CreatedDate
    var createdAt: Timestamp = Timestamp(System.currentTimeMillis())
        private set

    @CreatedBy
    protected var creator: U? = null

    @LastModifiedDate
    var updatedAt: Timestamp? = null

    @LastModifiedBy
    protected var editor: U? = null

    var deletedAt: Timestamp? = null
}
