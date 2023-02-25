package kz.app.asso.sms.dto.response

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "response")
class ResponseDto {
    var action: String? = null
    var data: DataDto? = null
}
