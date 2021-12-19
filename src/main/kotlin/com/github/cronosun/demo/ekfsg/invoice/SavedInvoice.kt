package com.github.cronosun.demo.ekfsg.invoice

import com.github.cronosun.demo.ekfsg.file.FileId
import com.github.cronosun.demo.ekfsg.shared.currency.MaybeCurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.MaybeIban
import com.github.cronosun.demo.ekfsg.shared.ssn.Ssn
import com.github.cronosun.demo.ekfsg.user.UserId
import java.time.Instant

data class SavedInvoice(
    val id: InvoiceId,
    val state: InvoiceState,
    val internalComment: String,
    val createdAt: Instant,
    val createdBy: UserId,
    override val amount: MaybeCurrencyAmount,
    override val comment: String,
    override val document: FileId,
    override val ssn: Ssn,
    override val iban: MaybeIban
) : Invoice