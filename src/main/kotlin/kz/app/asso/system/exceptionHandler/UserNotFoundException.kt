package kz.app.asso.system.exceptionHandler

class UserNotFoundException(message: String?, errorCode: String?) : ApiException(message, errorCode!!)
