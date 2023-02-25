package kz.app.asso.sms.dto

import com.fasterxml.jackson.annotation.JsonProperty

class NotifyUrl {
    @JsonProperty("bulk_id")
    var bulkId: String? = null

    @JsonProperty("message_id")
    var messageId: String? = null

    @JsonProperty("extra_id")
    var extraId: String? = null

    var to: String? = null

    var sender: String? = null

    var text: String? = null

    @JsonProperty("sent_at")
    var sentAt: String? = null

    @JsonProperty("done_at")
    var doneAt: String? = null

    @JsonProperty("sms_count")
    var smsCount: String? = null

    @JsonProperty("callback_data")
    var callbackData: String? = null

    var status: String? = null
    var mnc: String? = null
    var err: String? = null
}
