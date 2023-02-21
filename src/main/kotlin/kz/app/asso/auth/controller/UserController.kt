package kz.app.asso.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kz.app.asso.auth.model.payload.UserRequest
import kz.app.asso.auth.service.UserService
import kz.app.asso.system.dto.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserList(
        @RequestParam(value = "page") page: Int? = 1,
        @RequestParam(value = "size") size: Int? = 20,
        @RequestParam params: MutableMap<String, String> = mutableMapOf()
    ): Page<UserRequest> {
        val pageR: PageRequest = PageRequest.of((page ?: 1) - 1, (size ?: 20), Sort.by(Sort.Direction.DESC, "created_at"))
        return userService.getList(pageR, params)
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getAccountById(@PathVariable(value = "id") id: UUID): Mono<UserRequest> {
        return userService.getAccountById(id)
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    fun getCurrentAccount(authentication: Authentication): Mono<UserRequest> {
        return userService.findByName(authentication.name)
    }

    @PutMapping("/update")
    @PreAuthorize("isAuthenticated()")
    fun saveCurrentAccount(@Valid @RequestBody user: UserRequest, authentication: Authentication): Mono<Status> {
        return userService.saveChanges(authentication.name, user)
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    fun createNewAccount(@Valid @RequestBody user: UserRequest): Mono<Status> {
        return userService.create(user)
    }

    @DeleteMapping("/remove/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeAccount(@PathVariable(value = "id") id: UUID): Mono<Status> {
        return userService.moveToTrash(id)
    }

    @DeleteMapping("/remove-account")
    @PreAuthorize("isAuthenticated()")
    fun removeMyAccount(authentication: Authentication): Mono<Status> {
        return userService.moveToTrash(authentication.name)
    }

    @GetMapping("/pre")
    fun getEx(principal: Authentication): Any? {
        val oMapper = ObjectMapper()
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getDetails)
            .map { r -> oMapper.convertValue(r, MutableMap::class.java) }
            .map { r -> r["tokenValue"] }
    }

}
