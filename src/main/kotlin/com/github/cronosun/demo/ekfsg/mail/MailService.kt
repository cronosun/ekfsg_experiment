package com.github.cronosun.demo.ekfsg.mail

import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.Entity
import org.ktorm.entity.add
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.util.*

@Service
class MailService(
    @Autowired
    private val senderBackend: SenderBackend,
    @Autowired
    private val clock: Clock,
    @Autowired
    private val database: Database
) {
    /**
     * This stores the mail in the database and then uses the `SenderBackend` to send the mail. Since it's stored
     * in the database, the user can see what mails have been sent (and it's also possible to implement a retry
     * mechanism if the mail-sender server is down; not implemented in this demo).
     */
    fun sendMail(mail: MailToBeSent) {
        val from = getFromAddress(mail.from)
        addToDatabase(from, mail)
        sendMailUsingBackend(from, mail)
    }

    fun listSentMailsOrderedBySentDateDescending(limit: Int): List<SentMail> {
        return database.from(MailEntities).select()
            .orderBy(MailEntities.sentAt.desc()).limit(limit)
            .map { row -> MailEntities.createEntity(row) }.map { entityToSentMail(it) }
    }

    private fun getFromAddress(from: MailFrom): String {
        return when (from) {
            is MailFrom.CustomSender -> from.from
            MailFrom.DefaultSystemSender -> {
                // we'd take that value from a property (for this demo it's just hardcoded)
                "no-reply-kja@be.ch"
            }
        }
    }

    private fun entityToSentMail(entity: MailEntity): SentMail {
        return SentMail(
            to = entity.to, from = entity.from, subject = entity.subject,
            body = entity.body, sentAt = entity.sentAt
        )
    }

    private fun sendMailUsingBackend(from: String, mail: MailToBeSent) {
        // NOTE: Auch schon zu sehen: Argumente lassen sich per Name übergeben; besonders hilfreich wenn man -
        // wie hier - eine Menge von Argumenten hat (alle mit demselben Typ).
        senderBackend.sendMail(from = from, to = mail.to, subject = mail.subject, body = mail.body)
    }

    private fun addToDatabase(from: String, mail: MailToBeSent) {
        val id = UUID.randomUUID()
        val sentAt = clock.instant()
        val entity = MailEntity()
        entity.id = id
        entity.from = from
        entity.to = mail.to
        entity.subject = mail.subject
        entity.body = mail.body
        entity.sentAt = sentAt

        database.mails.add(entity)
    }

    private interface MailEntity : Entity<MailEntity> {
        companion object : Entity.Factory<MailEntity>()

        var id: UUID
        var from: String
        var to: String
        var subject: String
        var body: String
        var sentAt: Instant
    }

    private object MailEntities : Table<MailEntity>("mail") {
        val id = uuid("id").primaryKey().bindTo { it.id }
        val from = varchar("from").bindTo { it.from }
        val to = varchar("to").bindTo { it.to }
        val subject = varchar("subject").bindTo { it.subject }
        val body = varchar("body").bindTo { it.body }
        val sentAt = timestamp("sent_at").bindTo { it.sentAt }
    }

    private val Database.mails get() = this.sequenceOf(MailEntities)
}

