package kz.app.asso.sms.service

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import kz.app.asso.sms.dto.SmsRequest
import kz.app.asso.sms.dto.response.AcceptReportDto
import kz.app.asso.sms.dto.response.ResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono


/**
 * @project microservice-template
 * @author Bekzat Sailaubayev on 27.05.2022
 */
@Service
class SmsService {

    @Value("\${sms.user.login}")
    private val username: String? = null

    @Value("\${sms.user.password}")
    private val password: String? = null

    @Value("\${sms.user.url}")
    private val url: String? = null

    fun send(
        sms: SmsRequest
    ): Mono<AcceptReportDto> {
        return send(
            sms.recipient!!,
            sms.messageData!!,
            sms.action,
            sms.messageType,
            sms.originator
        )
    }

    fun send(
        recipient: String,
        messageData: String,
        action: String,
        messageType: String,
        originator: String
    ): Mono<AcceptReportDto> {
        val restTemplate = RestTemplate()

        val urlTemplate = UriComponentsBuilder.fromHttpUrl(url!!)
            .queryParam("action", action)
            .queryParam("username", username!!)
            .queryParam("password", password!!)
            .queryParam("recipient", recipient)
            .queryParam("messagetype", messageType)
            .queryParam("originator", originator)
            .queryParam("messagedata", messageData)
            .encode()
            .toUriString()

        val xmlData: String = restTemplate.postForObject(urlTemplate, null, String::class)
        val mapper = XmlMapper()
        val result = mapper.readValue(xmlData, ResponseDto::class.java)
        if (result?.data != null && result.data!!.acceptreport != null) {
            return Mono.just(result.data!!.acceptreport!!)
        }

        return Mono.error(Exception("sms not send"))
    }
}
