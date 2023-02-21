package kz.app.asso.auth.dto

data class UserLoginDto(
    var username: String,
    var password: String,
    var grant_type: String
)
