package kz.app.asso.system.exceptionHandler

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import kz.app.asso.auth.configuration.security.auth.UnauthorizedException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

@Component
class AppErrorAttributes : DefaultErrorAttributes() {
    var status = HttpStatus.INTERNAL_SERVER_ERROR

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): Map<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, ErrorAttributeOptions.defaults())
        val error = getError(request)
        val errorList = ArrayList<Map<String, Any?>>()
        when (error) {
            is AuthException, is UnauthorizedException -> {
                status = HttpStatus.UNAUTHORIZED
                val errorMap = LinkedHashMap<String, Any?>()
                errorMap["code"] = (error as ApiException).errorCode
                errorMap["message"] = error.message
                errorList.add(errorMap)
            }

            is ApiException -> {
                status = HttpStatus.BAD_REQUEST
                val errorMap = LinkedHashMap<String, Any?>()
                errorMap["code"] = error.errorCode
                errorMap["message"] = error.message
                errorList.add(errorMap)
            }

            is ExpiredJwtException, is SignatureException, is MalformedJwtException -> {
                status = HttpStatus.UNAUTHORIZED
                val errorMap = LinkedHashMap<String, Any?>()
                errorMap["code"] = "UNAUTHORIZED"
                errorMap["message"] = error.message
                errorList.add(errorMap)
            }

            else -> {
                status = HttpStatus.INTERNAL_SERVER_ERROR
                var message = error.message
                if (message == null) message = error.javaClass.name
                val errorMap = LinkedHashMap<String, Any?>()
                errorMap["code"] = "INTERNAL_ERROR"
                errorMap["message"] = message
                errorList.add(errorMap)
            }
        }
        val errors = HashMap<String, Any>()
        errors["errors"] = errorList
        errorAttributes["status"] = status.value()
        errorAttributes["errors"] = errors
        return errorAttributes
    }
}
