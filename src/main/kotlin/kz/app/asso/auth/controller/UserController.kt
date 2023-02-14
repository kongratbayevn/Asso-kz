package kz.app.asso.auth.controller

import kz.app.asso.system.dto.Status
import kz.app.asso.auth.model.User
import kz.app.asso.auth.model.payload.NewUserRequest
import kz.app.asso.auth.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.util.*
import javax.validation.Valid



@RestController
@RequestMapping("/users")
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @GetMapping("/current")
    fun getUser(principal: Principal): Principal {
        return principal
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("#oauth2.hasScope('server')")
    fun getUserById(@PathVariable(value = "id") id: UUID): Optional<User> {
        return userService.getUserById(id)
    }

    @GetMapping("/phone-name/{phone}")
    @PreAuthorize("#oauth2.hasScope('server')")
    fun getUserByPhone(@PathVariable(value = "phone") phone: String): Optional<User> {
        return userService.getUserByPhone(phone)
    }

    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ADMIN')")
    @PostMapping("/list")
    fun getUserListByIds(@Valid @RequestBody ids: List<UUID>): ArrayList<User> {
        return userService.getUserListByIds(ids)
    }

    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ADMIN')")
    @PostMapping("/create")
    fun createUser(@Valid @RequestBody user: NewUserRequest): UUID? {
        return userService.create(user)
    }

    @PutMapping("/update")
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ADMIN')")
    fun updateUser(@Valid @RequestBody user: NewUserRequest): UUID? {
        return null
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("#oauth2.hasScope('server') or hasRole('ADMIN')")
    fun removeToTrashUser(@PathVariable id: UUID): Status {
        return userService.moveToTrash(id)
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/has-role")
    fun checkRole(@Valid @RequestBody roles: List<String>, authentication: Authentication): Status {
        val status = Status()
        println(authentication.name)
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

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/has-role/{userId}")
    fun checkRoleByUserId(
        @Valid @RequestBody roles: List<String>,
        @PathVariable("userId") userId: UUID
    ): Status {
        val status = Status()
        val user = userService.getUserById(userId)
        if (user.isPresent) {
            if (roles.isEmpty()) {
                status.status = 1
                status.message = "Have acces!"
                return status
            }
            for (role in roles) {
                if (user.get().rolesCollection.stream()
                        .anyMatch { it.name.uppercase().trim() == role.uppercase().trim() }) {
                    status.status = 1
                    status.message = "Have acces!"
                    return status
                }
            }
        }
        return status
    }
}
