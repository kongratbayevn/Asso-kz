package kz.app.asso.sms.dto

import javax.validation.constraints.NotNull

class SmsRequest {
    @NotNull
    var recipient: String? = null

    @NotNull
    var messageData: String? = null

    var action: String = "sendmessage"
    var messageType: String = "SMS:TEXT"
    var originator: String = "INFO_KAZ"
}
