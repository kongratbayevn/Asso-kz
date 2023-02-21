package kz.app.asso.system.exceptionHandler

class AuthException(message: String?, errorCode: String?) : ApiException(message, errorCode!!)
