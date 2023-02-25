package kz.app.asso.sms.dto

import com.fasterxml.jackson.annotation.JsonProperty
import kz.app.asso.sms.dto.NotifyUrl
import javax.validation.constraints.NotNull

class RequestSmsBody {

    @NotNull
    var from: String? = null

    @NotNull
    var to: String? = null

    @NotNull
    var text: String? = null

    var flash: Boolean? = false

    @JsonProperty("sent_at")
    var sentAt: String? = null

    @JsonProperty("notify_url")
    var notifyUrl: NotifyUrl? = null

    @JsonProperty("extra_id")
    var extraId: String? = null

    @JsonProperty("callback_data")
    var callbackData: String? = null

}
