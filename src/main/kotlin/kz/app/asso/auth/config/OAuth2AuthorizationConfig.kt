package kz.app.asso.auth.config

import kz.app.asso.auth.service.UserService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory



@Configuration
@EnableAuthorizationServer
class OAuth2AuthorizationConfig : AuthorizationServerConfigurerAdapter() {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var env: Environment

    @Autowired
    private lateinit var userService: UserService

    private var logger = LogManager.getLogger(OAuth2AuthorizationConfig::class.java)

    @Bean
    fun tokenEnhancer(): JwtAccessTokenConverter {
        val keyStoreKeyFactory = KeyStoreKeyFactory(ClassPathResource("mytest.jks"), "mypass".toCharArray())
        val converter = JwtAccessTokenConverter()
        converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"))
        return converter
    }

    @Bean
    fun tokenStore(): JwtTokenStore {
        return JwtTokenStore(tokenEnhancer())
    }

    @Throws(Exception::class)
    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        val tokenEnhancerChain = TokenEnhancerChain()
        tokenEnhancerChain.setTokenEnhancers(listOf(CustomTokenEnhancer(), tokenEnhancer()))
        endpoints
            .authenticationManager(authenticationManager)
            .tokenStore(tokenStore())
            .tokenEnhancer(tokenEnhancerChain)
            .accessTokenConverter(tokenEnhancer())
            .userDetailsService(userDetailsService)
    }

    @Throws(Exception::class)
    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security
            .tokenKeyAccess("permitAll()")
            .checkTokenAccess("isAuthenticated()")
            .passwordEncoder(NoOpPasswordEncoder.getInstance())
    }

    @Throws(java.lang.Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
            .withClient("browser")
            .authorizedGrantTypes("refresh_token", "password")
            .scopes("ui")
            .and()
            .withClient("kaspi-payment-service")
            .secret(env.getProperty("KASPI_PAYMENT_PASSWORD"))
            .authorizedGrantTypes("client_credentials", "refresh_token")
            .scopes("kaspi_payment")
            .accessTokenValiditySeconds(20000)
            .refreshTokenValiditySeconds(20000)
    }

    @EventListener
    fun authSuccessEventListener(authorizedEvent: AuthenticationSuccessEvent) {
        if (authorizedEvent.authentication.principal is User) {
            userService.resetPassword((authorizedEvent.authentication.principal as User).username)
        }
    }

    @EventListener
    fun authFailedEventListener(oAuth2AuthenticationFailureEvent: AbstractAuthenticationFailureEvent) {
        // write custom code here login failed audit.
        if (oAuth2AuthenticationFailureEvent.authentication.principal is String) {
            userService.failedAuth(oAuth2AuthenticationFailureEvent.authentication.principal.toString())
            logger.warn("${oAuth2AuthenticationFailureEvent.authentication.principal} authentication is failed")
        }
    }
}
