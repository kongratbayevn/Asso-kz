package kz.app.asso.system.service

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*


internal class AuditorAwareImpl: AuditorAware<String?> {
    override fun getCurrentAuditor(): Optional<String?> {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map { r -> r.toString() }
    }
}
