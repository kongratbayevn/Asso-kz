package kz.app.asso.auth.config

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.TokenEnhancer
import kotlin.collections.HashMap



class CustomTokenEnhancer : TokenEnhancer {
    override fun enhance(accessToken: OAuth2AccessToken, authentication: OAuth2Authentication): OAuth2AccessToken {
        val additionalInfo: MutableMap<String, Any> = HashMap()
        (accessToken as DefaultOAuth2AccessToken).additionalInformation = additionalInfo
        return accessToken
    }
}
