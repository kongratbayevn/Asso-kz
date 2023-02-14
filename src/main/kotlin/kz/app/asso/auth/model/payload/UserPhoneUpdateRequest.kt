package kz.app.asso.auth.model.payload

import java.sql.Timestamp
import java.util.*
import javax.validation.constraints.Pattern

class UserPhoneUpdateRequest {
    var id: UUID? = null
    var userId: UUID? = null
    @Pattern(regexp = "\\d{4}")
    var code: String = ""
}
