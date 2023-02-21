package kz.app.asso.system.config

import kz.app.asso.system.service.AuditorAwareImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware

@Configuration
class AuditorConfig {
    @Bean
    fun auditorAware(): AuditorAware<String?> {
        return AuditorAwareImpl()
    }
}
