package com.github.cronosun.demo.ekfsg.mail

import org.springframework.stereotype.Service

@Service
class DefaultSenderBackend : SenderBackend {
    override fun sendMail(from: String, to: String, subject: String, body: String) {
        // does nothing. For unit-tests there's a simulator.
        // A real implementation would use 'javax.mail'. Something like this:

        // val prop = Properties()
        // prop.put("mail.smtp.auth", true)
        // prop.put("mail.smtp.starttls.enable", "true")
        // prop.put("mail.smtp.host", "smtp.mailtrap.io")
        // prop.put("mail.smtp.port", "25")
        // prop.put("mail.smtp.ssl.trust", "smtp.mailtrap.io")
        // <...>
    }
}