package kz.app.asso.auth.dto

import java.util.*

data class AuthResultDto (
    var access_token: String? = null,
    var token_type: String? = "bearer",
    var refresh_token: String? = null,
    var expires_in: Date? = null,
    var scope: String = "ui",
    var jti: String = "",
)
