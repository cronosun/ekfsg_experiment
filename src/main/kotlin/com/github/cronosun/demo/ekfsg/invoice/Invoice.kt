package com.github.cronosun.demo.ekfsg.invoice

import com.github.cronosun.demo.ekfsg.file.FileId
import com.github.cronosun.demo.ekfsg.shared.currency.MaybeCurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.MaybeIban
import com.github.cronosun.demo.ekfsg.shared.ssn.Ssn
import java.util.*

interface Invoice {
    val amount: MaybeCurrencyAmount
    val comment: String
    val document: FileId
    val ssn: Ssn
    val iban: MaybeIban
}

enum class InvoiceState {
    NEW,
    APPROVED,
    REJECTED,
    SUBMITTED
}

@JvmInline
value class InvoiceId(val id: UUID)