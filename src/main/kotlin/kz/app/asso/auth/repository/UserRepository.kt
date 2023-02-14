package kz.app.asso.auth.repository

import kz.app.asso.auth.model.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import java.util.*
import kotlin.collections.ArrayList

interface UserRepository: JpaRepository<User, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): Optional<User>

    fun findAllByDeletedAtIsNull(): ArrayList<User>
    fun findAllByDeletedAtIsNull(page: Pageable): Page<User>
    fun findAllByIdInAndDeletedAtIsNull(@Param("id") ids: List<UUID>, page: Pageable): Page<User>
    fun findAllByIdInAndDeletedAtIsNull(@Param("id") ids: List<UUID>): ArrayList<User>

    fun findByPhone(@Param("phone") phone: String): Optional<User>
    fun findByPhoneAndDeletedAtIsNull(@Param("phone") phone: String): Optional<User>
    fun findByPhoneIgnoreCaseAndDeletedAtIsNull(@Param("phone") phone: String): Optional<User>
    fun findByPhoneIgnoreCase(@Param("phone") phone: String): Optional<User>
}
