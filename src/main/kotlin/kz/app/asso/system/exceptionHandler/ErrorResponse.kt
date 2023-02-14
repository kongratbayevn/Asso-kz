package kz.app.asso.system.exceptionHandler

import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus


@RequiredArgsConstructor
class ErrorResponse {
    var status: HttpStatus? = null
    var errorMsg: String? = null
    var developerMsg: String? = null
}
