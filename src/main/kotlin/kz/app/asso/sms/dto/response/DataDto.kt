package kz.app.asso.sms.dto.response

import kz.app.asso.sms.dto.response.AcceptReportDto
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "data")
class DataDto {
    var acceptreport: AcceptReportDto? = null
}
