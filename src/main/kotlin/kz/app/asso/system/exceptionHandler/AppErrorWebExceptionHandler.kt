package kz.app.asso.system.exceptionHandler

import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*

@Component
@Order(-2)
class AppErrorWebExceptionHandler(
    g: AppErrorAttributes?,
    applicationContext: ApplicationContext?,
    serverCodecConfigurer: ServerCodecConfigurer
) :
    AbstractErrorWebExceptionHandler(g, WebProperties.Resources(), applicationContext) {
    init {
        super.setMessageWriters(serverCodecConfigurer.writers)
        super.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { request: ServerRequest? ->
            val props =
                getErrorAttributes(request, ErrorAttributeOptions.defaults())
            ServerResponse.status(props.getOrDefault("status", 500).toString().toInt())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(props["errors"] as Any))
        }
    }
}
