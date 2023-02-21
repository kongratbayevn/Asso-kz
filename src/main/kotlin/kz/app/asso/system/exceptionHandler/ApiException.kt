package kz.app.asso.system.exceptionHandler

import lombok.Getter

open class ApiException(message: String?, @field:Getter var errorCode: String) : RuntimeException(message)
