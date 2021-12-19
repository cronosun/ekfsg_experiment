package com.github.cronosun.demo.ekfsg.invoice

import com.github.cronosun.demo.ekfsg.file.FileId
import com.github.cronosun.demo.ekfsg.mail.MailFrom
import com.github.cronosun.demo.ekfsg.mail.MailService
import com.github.cronosun.demo.ekfsg.mail.MailToBeSent
import com.github.cronosun.demo.ekfsg.shared.currency.MaybeCurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.MaybeIban
import com.github.cronosun.demo.ekfsg.shared.ssn.Ssn
import com.github.cronosun.demo.ekfsg.user.UserId
import com.github.cronosun.demo.ekfsg.user.UserInfo
import com.github.cronosun.demo.ekfsg.user.UserService
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
class InvoiceService(
    @Autowired val clock: Clock,
    @Autowired val userService: UserService,
    @Autowired val database: Database,
    @Autowired val mailService: MailService
) {
    /**
     * Die Funktionen in den Services sollten einigermassen die Use-Cases wiederspiegeln: Beispielsweise
     * ist das die Funktion einen neue Rechnung hinhzuzufügen (im Externen Zugang). Auch in den Daten
     * sollte sich das Widerspiegeln: Beispielsweise gibt es im `NewExternalInvoice` folgendes nicht:
     *
     *  - id / uuid
     *  - status (freigabe, gesendet, ...)
     *  - createdAt timestamp
     *  - user ID
     *
     *  ... wieso? Diese Daten kommen nicht vom User, das sind interne Details (die werden hier in der
     *  Service-Methode hinhzugefügt). Das bedeutet auch, dass eine Rechung je nach Use-Case anders
     *  aussieht (weniger oder mehr Daten hat).
     */
    fun addInvoice(invoice: NewExternalInvoice) {
        val id = UUID.randomUUID()
        val createdAt = clock.instant()
        val userInfo = userService.requireCurrentUser()
        val userId = userInfo.id
        val entity = InvoiceEntity()
        entity.id = id
        entity.userComment = invoice.comment
        entity.internalComment = ""
        entity.fileId = invoice.document.id
        entity.amountCents = invoice.amount.toMaybeCents()
        entity.iban = invoice.iban.toString()
        entity.ssn = invoice.ssn.toString()
        entity.state = InvoiceState.NEW
        entity.createdAt = createdAt
        entity.userId = userId.id
        entity.userMail = userInfo.mail

        database.invoices.add(entity)

        sendMailForNewInvoice(userInfo)
    }

    fun listInvoicesSortedByDateDescendingInState(state: InvoiceState, limit: Int): Iterable<SavedInvoice> {
        return database.from(InvoiceEntities).select()
            .where { InvoiceEntities.state eq state }
            .orderBy(InvoiceEntities.createdAt.desc()).limit(limit)
            .map { row -> InvoiceEntities.createEntity(row) }.map { entityToSavedInvoice(it) }
    }

    private fun entityToSavedInvoice(entity: InvoiceEntity): SavedInvoice {
        val iban = MaybeIban.tryFromString(entity.iban)
            ?: throw RuntimeException("Got invalid IBAN in database: ${entity.iban}")
        return SavedInvoice(
            amount = MaybeCurrencyAmount.fromMaybeCents(entity.amountCents),
            comment = entity.userComment,
            createdAt = entity.createdAt,
            createdBy = UserId(entity.userId),
            document = FileId(entity.fileId),
            iban = iban,
            id = InvoiceId(entity.id),
            internalComment = entity.internalComment,
            ssn = Ssn(entity.ssn),
            state = entity.state
        )
    }

    /**
     * Use-case: Ablehnen einer Rechnung.
     */
    fun rejectInvoice(id: InvoiceId): RejectInvoiceResult {
        val rowsAffected = database.update(InvoiceEntities) {
            set(it.state, InvoiceState.REJECTED)
            where {
                it.id eq id.id
            }
        }
        return when (rowsAffected) {
            0 -> RejectInvoiceResult.NOTHING_AFFECTED
            else -> {
                sendMailForRejectedInvoice(id)
                RejectInvoiceResult.OK
            }
        }
    }

    /**
     * Use-case: Genehmigen einer Rechnung.
     */
    fun approveInvoice(approval: InvoiceApproval): ApproveInvoiceResult {
        val rowsAffected = database.update(InvoiceEntities) {
            set(it.state, InvoiceState.APPROVED)
            set(it.iban, approval.iban.iban)
            set(it.amountCents, approval.amount.cents)
            where {
                it.id eq approval.id.id
            }
        }
        return when (rowsAffected) {
            0 -> ApproveInvoiceResult.NOTHING_AFFECTED
            else -> {
                sendMailForAcceptedInvoice(approval.id)
                ApproveInvoiceResult.OK
            }
        }
    }

    private fun getMailFromUserThatCreatedInvoice(id: InvoiceId): String? {
        val list = database.from(InvoiceEntities).select()
            .where { InvoiceEntities.id eq id.id }
            .map { row -> InvoiceEntities.createEntity(row) }.map { it.userMail }
        return if (list.size == 1) {
            list.first()
        } else {
            null
        }
    }

    private fun sendMailForNewInvoice(userInfo: UserInfo) {
        val mail = MailToBeSent(
            subject = "Rechnung erfasst",
            body = "Bonjour\n\nSie haben soeben eine neue Rechnung erfasst. Die Rechnung wird verarbeitet.",
            from = MailFrom.DefaultSystemSender,
            to = userInfo.mail
        )
        mailService.sendMail(mail)
    }

    private fun sendMailForRejectedInvoice(id: InvoiceId) {
        val toAddress = getMailFromUserThatCreatedInvoice(id)
        if (toAddress == null) {
            // TODO: Log here or handle this situation (should never happen)
        } else {
            val mail = MailToBeSent(
                subject = "Rechung Abgelehnt",
                body = "Leider wurde Ihre Rechnung abgelehnt.",
                from = MailFrom.DefaultSystemSender,
                to = toAddress
            )
            mailService.sendMail(mail)
        }
    }

    private fun sendMailForAcceptedInvoice(id: InvoiceId) {
        val toAddress = getMailFromUserThatCreatedInvoice(id)
        if (toAddress == null) {
            // TODO: Log here or handle this situation (should never happen)
        } else {
            val mail = MailToBeSent(
                subject = "Rechung Genehmigt",
                body = "Toptop, Ihre Rechnung wurde genehmigt.",
                from = MailFrom.DefaultSystemSender,
                to = toAddress
            )
            mailService.sendMail(mail)
        }
    }

    private interface InvoiceEntity : Entity<InvoiceEntity> {
        companion object : Entity.Factory<InvoiceEntity>()

        var id: UUID
        var amountCents: Int?
        var userComment: String
        var internalComment: String
        var fileId: UUID
        var ssn: String
        var iban: String?
        var createdAt: Instant
        var userId: String
        var userMail: String
        var state: InvoiceState
    }

    private object InvoiceEntities : Table<InvoiceEntity>("invoice") {
        val id = uuid("id").primaryKey().bindTo { it.id }
        val amountCents = int("amount_cents").bindTo { it.amountCents }
        val userComment = varchar("user_comment").bindTo { it.userComment }
        val internalComment = varchar("internal_comment").bindTo { it.internalComment }
        val fileId = uuid("file_id").bindTo { it.fileId }
        val ssn = varchar("ssn").bindTo { it.ssn }
        val iban = varchar("iban").bindTo { it.iban }
        val createdAt = timestamp("created_at").bindTo { it.createdAt }
        val userId = varchar("user_id").bindTo { it.userId }
        val userMail = varchar("user_mail").bindTo { it.userMail }
        val state = enum<InvoiceState>("state").bindTo { it.state }
    }

    private val Database.invoices get() = this.sequenceOf(InvoiceEntities)
}

enum class RejectInvoiceResult {
    OK,
    NOTHING_AFFECTED
}

enum class ApproveInvoiceResult {
    OK,
    NOTHING_AFFECTED
}