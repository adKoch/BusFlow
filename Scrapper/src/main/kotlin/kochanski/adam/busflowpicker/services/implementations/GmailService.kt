package kochanski.adam.busflowpicker.services.implementations

import java.time.Instant
import kochanski.adam.busflowpicker.services.MessageService
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.stereotype.Component

@Component
class GmailService : MessageService {

    private final val emailSender: JavaMailSenderImpl = JavaMailSenderImpl()
    private final val mailUsername: String = System.getenv("gmail.username")
    private final val mailPassword: String = System.getenv("gmail.password")
    private final val mailReceiver: String = System.getenv("gmail.receiver")

    final val SMTP_TRANSPORT_PROTOCOL_NAME = "smtp"
    final val SMTP_NAME = "smtp.gmail.com"
    final val SMTP_PORT = 587
    final val SMTP_START_TLS_NAME = "mail.smtp.starttls.enable"

    init {
        emailSender.host = SMTP_NAME
        emailSender.port = SMTP_PORT
        emailSender.username = mailUsername
        emailSender.password = mailPassword
        emailSender.protocol = SMTP_TRANSPORT_PROTOCOL_NAME
        emailSender.javaMailProperties[SMTP_START_TLS_NAME] = "true"
    }

    override fun sendInfo(title: String, message: String) {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(mailReceiver)
        simpleMailMessage.setSubject("Info: $title")
        simpleMailMessage.setText("Sent on: ${Instant.now()}\n\n$message\n\n Sent via BusFlow email messaging")
        emailSender.send(simpleMailMessage)
    }

    override fun sendError(title: String, message: String, error: String) {
        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(mailReceiver)
        simpleMailMessage.setSubject("Error: $title")
        simpleMailMessage.setText("Sent on: ${Instant.now()}\nError: $error\n\n$message\n\n Sent via BusFlow email messaging")
        emailSender.send(simpleMailMessage)
    }
}
