package com.github.cronosun.demo.ekfsg.mail

data class MailToBeSent(
    val from: MailFrom,
    val subject: String,
    val to: String,
    val body: String
)