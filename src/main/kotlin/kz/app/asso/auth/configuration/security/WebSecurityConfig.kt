package kz.app.asso.auth.configuration.security

import kz.app.asso.auth.configuration.security.support.JwtVerifyHandler
import kz.app.asso.auth.configuration.security.support.ServerHttpBearerAuthenticationConverter
import kz.app.asso.auth.configuration.security.support.ServerHttpCookieAuthenticationConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class WebSecurityConfig {
    private val logger = org.slf4j.LoggerFactory.getLogger(WebSecurityConfig::class.java)

    @Value("\${jwt.secret}")
    private val jwtSecret: String = "asso.kz"

    @Value("\${app.public_routes}")
    val publicRoutes: Array<String> = arrayOf()

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity, authManager: ReactiveAuthenticationManager): SecurityWebFilterChain {
        return http
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()
            .pathMatchers(*publicRoutes).permitAll()
            .pathMatchers("/favicon.ico").permitAll()
            .anyExchange().authenticated().and()
            .csrf().disable()
            .formLogin().disable()
            .exceptionHandling()
            .authenticationEntryPoint(ServerAuthenticationEntryPoint { swe: ServerWebExchange, e: AuthenticationException ->
                logger.info("[1] Authentication error: Unauthorized[401]: " + e.message)
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.UNAUTHORIZED }
            })
            .accessDeniedHandler(ServerAccessDeniedHandler { swe: ServerWebExchange, e: AccessDeniedException ->
                logger.info("[2] Authentication error: Access Denied[401]: " + e.message)
                Mono.fromRunnable { swe.response.statusCode = HttpStatus.FORBIDDEN }
            })
            .and()
            .addFilterBefore(bearerAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterBefore(cookieAuthenticationFilter(authManager), SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }

    /**
     * Spring security works by filter chaining.
     * We need to add a JWT CUSTOM FILTER to the chain.
     *
     * what is AuthenticationWebFilter:
     *
     * A WebFilter that performs authentication of a particular request. An outline of the logic:
     * A request comes in and if it does not match setRequiresAuthenticationMatcher(ServerWebExchangeMatcher),
     * then this filter does nothing and the WebFilterChain is continued.
     * If it does match then... An attempt to convert the ServerWebExchange into an Authentication is made.
     * If the result is empty, then the filter does nothing more and the WebFilterChain is continued.
     * If it does create an Authentication...
     * The ReactiveAuthenticationManager specified in AuthenticationWebFilter(ReactiveAuthenticationManager) is used to perform authentication.
     * If authentication is successful, ServerAuthenticationSuccessHandler is invoked and the authentication is set on ReactiveSecurityContextHolder,
     * else ServerAuthenticationFailureHandler is invoked
     *
     */
    fun bearerAuthenticationFilter(authManager: ReactiveAuthenticationManager): AuthenticationWebFilter {
        val bearerAuthenticationFilter = AuthenticationWebFilter(authManager)
        bearerAuthenticationFilter.setAuthenticationConverter(
            ServerHttpBearerAuthenticationConverter(
                JwtVerifyHandler(
                    jwtSecret
                )
            )
        )
        bearerAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"))
        return bearerAuthenticationFilter
    }

    fun cookieAuthenticationFilter(authManager: ReactiveAuthenticationManager): AuthenticationWebFilter? {
        val cookieAuthenticationFilter = AuthenticationWebFilter(authManager)
        cookieAuthenticationFilter.setAuthenticationConverter(
            ServerHttpCookieAuthenticationConverter(
                JwtVerifyHandler(
                    jwtSecret
                )
            )
        )
        cookieAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/**"))
        return cookieAuthenticationFilter
    }

}
