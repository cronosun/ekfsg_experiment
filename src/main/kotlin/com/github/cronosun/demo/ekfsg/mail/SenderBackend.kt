package com.github.cronosun.demo.ekfsg.mail

/**
 * Mail sender backend. A real implementation would for example use `javax.mail`.
 */
interface SenderBackend {
    fun sendMail(from: String, to: String, subject: String, body: String)
}