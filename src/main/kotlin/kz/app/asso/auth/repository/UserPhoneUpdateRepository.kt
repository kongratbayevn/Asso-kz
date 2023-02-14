package kz.app.asso.auth.repository

import kz.app.asso.auth.model.UserPhoneUpdate
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserPhoneUpdateRepository: JpaRepository<UserPhoneUpdate, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): Optional<UserPhoneUpdate>
    fun findFirstByOldPhoneAndNewPhoneAndConfirmedFalseOrderByCreatedAt(oldPhone: String, newPhone: String): Optional<UserPhoneUpdate>
}
