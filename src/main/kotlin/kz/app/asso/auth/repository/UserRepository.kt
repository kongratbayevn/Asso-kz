package kz.app.asso.auth.repository

import kz.app.asso.auth.model.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono
import java.util.*

interface UserRepository: R2dbcRepository<User, UUID> {
    fun findByPhoneIgnoreCase(phone: String): Mono<User>
    fun findByPhoneIgnoreCaseAndDeletedAtIsNull(phone: String): Mono<User>
    fun findByPhoneAndDeletedAtIsNull(phone: String): Mono<User>
    fun findByIdAndDeletedAtIsNull(id: UUID): Mono<User>
}
