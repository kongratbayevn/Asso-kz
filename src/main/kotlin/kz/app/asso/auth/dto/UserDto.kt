package kz.app.asso.auth.dto

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.*

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class UserDto(
    var id: UUID? = null,
    var phone: String = "",
    var firstName: String? = null,
    var lastName: String? = null,
    var email: String? = null,
    var avatar: UUID? = null,
//    val enabled: Boolean = false
)
