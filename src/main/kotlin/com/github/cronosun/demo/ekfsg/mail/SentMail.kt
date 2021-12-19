package com.github.cronosun.demo.ekfsg.mail

import java.time.Instant

data class SentMail(
    val from: String,
    val subject: String,
    val to: String,
    val body: String,
    val sentAt: Instant,
)
