package kz.app.asso.auth.configuration.security.auth

import com.fasterxml.jackson.annotation.JsonIgnore
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.UUID
import java.util.stream.Collectors

@Data
@AllArgsConstructor
@NoArgsConstructor
class LocalUserDetails : UserDetails {
    private val id: UUID? = null
    private val username: String? = null

    @JsonIgnore
    private val password: String? = null
    private val roles: List<String>? = null
    private val enabled: Boolean? = null

    override fun isAccountNonExpired(): Boolean {
        return false
    }

    override fun isAccountNonLocked(): Boolean {
        return false
    }

    override fun isCredentialsNonExpired(): Boolean {
        return false
    }

    override fun isEnabled(): Boolean {
        return enabled!!
    }

    override fun getPassword(): String {
        return username!!
    }

    override fun getUsername(): String {
        return password!!
    }

    @JsonIgnore
    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return roles!!.stream()
            .map { role: String? ->
                SimpleGrantedAuthority(
                    role
                )
            }
            .collect(Collectors.toList())
    }
}
