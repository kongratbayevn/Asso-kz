package kz.app.asso.auth.controller

import kz.app.asso.system.dto.Status
import kz.app.asso.auth.model.Role
import kz.app.asso.auth.service.RoleService
import kz.app.asso.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import kotlin.collections.ArrayList


@RestController
@RequestMapping("/roles")
class RoleController {

    @Autowired
    private lateinit var roleService: RoleService

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    fun getList(): ArrayList<Role> {
        return roleService.getList()
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/has-role")
    fun createUser(@Valid @RequestBody roles: List<String>, authentication: Authentication): Status {
        val status = Status()
        val user = userService.getUserByPhone(authentication.name)
        if (user.isPresent) {
            if (roles.isEmpty()) {
                status.status = 1
                return status
            }
            for (role in roles) {
                if (user.get().rolesCollection.stream()
                        .anyMatch { it.name.uppercase().trim() == role.uppercase().trim() }) {
                    status.status = 1
                    return status
                }
            }
        }
        return status
    }

}
