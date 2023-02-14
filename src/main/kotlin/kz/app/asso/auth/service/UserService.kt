package kz.app.asso.auth.service

import kz.app.asso.system.dto.Status
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.payload.NewUserRequest
import kz.app.asso.auth.model.payload.UserPhoneUpdateRequest
import java.util.*

interface UserService {

    fun create(user: NewUserRequest): UUID?
    fun getUserById(id: UUID): Optional<User>
    fun getUserByPhone(phone: String): Optional<User>
    fun getUserListByIds(ids:  List<UUID>): ArrayList<User>
    fun moveToTrash(id: UUID): Status
    fun delete(id: UUID): Status
    fun randomCodeUser(userRequest: NewUserRequest): Status
    fun resetPassword(username: String)
    fun failedAuth(username: String)
    fun phoneUpdateGenerateCode(userRequest: NewUserRequest): Status
    fun phoneUpdateConfirm(confirmCode: UserPhoneUpdateRequest): Status

}
