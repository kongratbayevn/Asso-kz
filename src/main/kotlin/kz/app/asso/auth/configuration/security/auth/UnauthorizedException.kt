package kz.app.asso.auth.configuration.security.auth

import kz.app.asso.system.exceptionHandler.ApiException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException(message: String?) : ApiException(message, "UNAUTHORIZED")
