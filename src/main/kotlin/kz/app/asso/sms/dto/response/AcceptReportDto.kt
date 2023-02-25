package kz.app.asso.sms.dto.response

import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "acceptreport")
class AcceptReportDto {
    var statuscode: String? = null
    var statusmessage: String? = null
    var messageid: String? = null
    var recipient: String? = null
    var originator: String? = null
    var messagetype: String? = null
    var messagedata: String? = null
}
