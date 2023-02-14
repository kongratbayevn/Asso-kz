package kz.app.asso.auth.service.security

import kz.app.asso.auth.model.UserPrincipal
import kz.app.asso.auth.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.sql.Timestamp
import java.util.stream.Collectors

@Service
class UserDetailsServiceImpl: UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    private val encoder = BCryptPasswordEncoder()

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val userCandidate = userRepository.findByPhoneIgnoreCaseAndDeletedAtIsNull(username)
//        if (userCandidate.isEmpty || userCandidate.get().code == null) {
        if (userCandidate.isEmpty) {
            throw UsernameNotFoundException("User $username not found")
        }
        val user = userCandidate.get()

        val isExpired = user.expiredCode != null && user.expiredCode!!.before(Timestamp(System.currentTimeMillis()))

        val isLocked = user.isBlocked != null && user.isBlocked!!.after(Timestamp(System.currentTimeMillis()))

        val authorities: List<GrantedAuthority> = user.rolesCollection.stream().map { role -> SimpleGrantedAuthority("ROLE_${role.name}") }
            .collect(Collectors.toList<GrantedAuthority>())

        val userPrincipal = org.springframework.security.core.userdetails.User
            .withUsername(username)
            .password(user.code)
            .authorities(authorities)
            .accountExpired(isExpired)
            .accountLocked(isLocked)
            .credentialsExpired(false)
            .disabled(false)
            .build()

        return UserPrincipal(userPrincipal, user.id!!)
    }
}
