package com.github.cronosun.demo.ekfsg.mail

class SenderBackendSimulator : SenderBackend {

    private val mutableSentMails = mutableListOf<SentTestMail>()
    val sentMails: List<SentTestMail> get() = mutableSentMails

    override fun sendMail(from: String, to: String, subject: String, body: String) {
        mutableSentMails.add(SentTestMail(from = from, to = to, subject = subject, body = body))
    }

    fun numberOfMatches(example: MailExample): Int {
        return mutableSentMails.count { example.matches(it) }
    }

    fun has(example: MailExample): Boolean {
        return numberOfMatches(example) > 0
    }

    fun clearAll() {
        mutableSentMails.clear()
    }
}

data class MailExample(
    val from: String? = null,
    val to: String? = null,
    val subject: String? = null,
    val body: String? = null
) {
    fun matches(mail: SentTestMail): Boolean {
        if (from != null && mail.from != from) {
            return false
        }
        if (to != null && mail.to != to) {
            return false
        }
        if (subject != null && mail.subject != subject) {
            return false
        }
        if (body != null && mail.body != body) {
            return false
        }
        return true
    }
}

data class SentTestMail(val from: String, val to: String, val subject: String, val body: String)