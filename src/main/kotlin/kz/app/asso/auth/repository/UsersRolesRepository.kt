package kz.app.asso.auth.repository

import kz.app.asso.auth.model.UsersRoles
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UsersRolesRepository: JpaRepository<UsersRoles, UUID> {

    @Transactional
    fun deleteAllByUserId(userId: UUID): Long
}
