package kz.app.asso.auth.model.payload

import java.util.*

class NewUserRequest {
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
}
