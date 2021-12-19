package com.github.cronosun.demo.ekfsg.mail

sealed class MailFrom {
    object DefaultSystemSender : MailFrom()
    data class CustomSender(val from: String) : MailFrom()
}