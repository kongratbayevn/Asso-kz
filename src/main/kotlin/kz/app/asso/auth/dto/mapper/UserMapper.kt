package kz.app.asso.auth.dto.mapper

import kz.app.asso.auth.dto.UserDto
import kz.app.asso.auth.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun map(user: User): UserDto {
        return UserDto(
            phone = user.phone
        )
    }

    fun map(userDto: UserDto): User {
        val user = User()
        user.phone = userDto.phone
        return user
    }
}
