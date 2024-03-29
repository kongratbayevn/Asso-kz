package kz.app.asso.system.exceptionHandler

import org.springframework.http.HttpStatus

class UnauthorisedException(message: String?, developerMessage: String?) : RuntimeException(message) {
    private val errorResponse: ErrorResponse = ErrorResponse()

    init {
        errorResponse.developerMsg = developerMessage
        errorResponse.errorMsg = message
        errorResponse.status = HttpStatus.UNAUTHORIZED
    }
}
