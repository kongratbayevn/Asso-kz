package kz.app.asso.auth.model.payload

import kz.app.asso.auth.dto.UserDto
import kz.app.asso.auth.model.User
import java.util.*

class UserRequest {
    constructor()

    constructor(userDto: UserDto) {
        this.id = userDto.id
        this.phone = userDto.phone
        this.firstName = userDto.firstName
        this.lastName = userDto.lastName
        this.email = userDto.email
        this.avatar = userDto.avatar
    }

    constructor(userDto: User?) {
        this.id = userDto?.id
        this.phone = userDto?.phone?:""
        this.firstName = userDto?.firstName
        this.lastName = userDto?.lastName
        this.email = userDto?.email
        this.avatar = userDto?.avatar
    }

    var id: UUID? = null
    var phone: String = ""
        set(value) {
            field = if (!value.contains("+")) {
                "+${value.replace(" ", "")}"
            } else {
                value.replace(" ", "")
            }
        }
    var roles: ArrayList<String> = arrayListOf()
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null
    var avatar: UUID? = null

    fun getUser(): User {
        val user = User()
        user.phone = this.phone
        user.firstName = this.firstName
        user.lastName = this.lastName
        user.email = this.email
        user.roles = this.roles
        user.avatar = this.avatar
        return user
    }
}
