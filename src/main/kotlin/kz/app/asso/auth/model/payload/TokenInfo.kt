package kz.app.asso.auth.model.payload

import lombok.Builder
import java.util.*

@Builder(toBuilder = true)
data class TokenInfo (
    var userId: UUID? = null,
    var token: String? = null,
    var issuedAt: Date? = null,
    var expiresAt: Date? = null
)
