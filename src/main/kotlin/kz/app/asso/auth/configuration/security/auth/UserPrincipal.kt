package kz.app.asso.auth.configuration.security.auth

import java.security.Principal
import java.util.UUID


class UserPrincipal(val id: UUID, private val name: String) : Principal {

    override fun getName(): String {
        return name
    }
}
