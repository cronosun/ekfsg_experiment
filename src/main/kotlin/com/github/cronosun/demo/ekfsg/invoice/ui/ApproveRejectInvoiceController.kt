package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.invoice.*
import com.github.cronosun.demo.ekfsg.shared.currency.CurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.Iban
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ApproveRejectInvoiceController(
    private val invoiceService: InvoiceService
) {
    private val internalInvoiceToApproveOrReject: BehaviorSubject<InvoiceToApproveOrReject> =
        BehaviorSubject.createDefault(InvoiceToApproveOrReject.None)

    private val isValidating: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    var listOfInvoicesChanged: () -> Unit = {}

    fun approveOrRejectInvoice(maybeInvoice: InvoiceToApproveOrReject) {
        reset()
        internalInvoiceToApproveOrReject.onNext(maybeInvoice)
        when (maybeInvoice) {
            is InvoiceToApproveOrReject.Some -> {
                val invoice = maybeInvoice.invoice
                amount.onNext(invoice.amount.toString())
                iban.onNext(invoice.iban.toString())
            }
            else -> {}
        }
    }

    val ssn: Observable<String> = internalInvoiceToApproveOrReject.map {
        when (it) {
            InvoiceToApproveOrReject.None -> ""
            is InvoiceToApproveOrReject.Some -> it.invoice.ssn.toString()
        }
    }

    val amount: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val amountInvalid: Observable<Boolean> = Observable.combineLatest(isValidating, amount) { isValidating, amount ->
        isValidating && (CurrencyAmount.tryParseFromString(amount) == null)
    }

    val iban: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val ibanInvalid: Observable<Boolean> = Observable.combineLatest(isValidating, iban) { isValidating, iban ->
        isValidating && !Iban.isValid(iban)
    }

    fun reject() {
        val invoice = currentInvoice
        if (invoice != null) {
            when (invoiceService.rejectInvoice(invoice.id)) {
                RejectInvoiceResult.OK -> continueAfterSuccessfulReject()
                else -> {}
            }
        }
    }

    fun approve() {
        val valid = approveInternal()
        if (!valid) {
            // activate validation
            isValidating.onNext(true)
        } else {
            continueAfterSuccessfulApproval()
        }
    }

    fun reset() {
        internalInvoiceToApproveOrReject.onNext(InvoiceToApproveOrReject.None)
        isValidating.onNext(false)
        amount.onNext("")
        iban.onNext("")
    }

    private fun approveInternal(): Boolean {
        val invoice = currentInvoice
        return if (invoice != null) {
            val ibanString = iban.value
            val iban = if (ibanString != null) {
                Iban.tryFrom(ibanString) ?: return false
            } else {
                return false
            }
            val amountString = amount.value
            val amount = if (amountString != null) {
                CurrencyAmount.tryParseFromString(amountString) ?: return false
            } else {
                return false
            }

            val approval = InvoiceApproval(
                id = invoice.id,
                iban = iban,
                amount = amount
            )

            when (invoiceService.approveInvoice(approval)) {
                ApproveInvoiceResult.OK -> {
                    continueAfterSuccessfulApproval()
                }
                ApproveInvoiceResult.NOTHING_AFFECTED -> {
                    // we could display some error message here
                }
            }
            true
        } else {
            false
        }
    }

    private fun continueAfterSuccessfulReject() {
        listOfInvoicesChanged()
    }

    private fun continueAfterSuccessfulApproval() {
        listOfInvoicesChanged()
    }

    private val currentInvoice: SavedInvoice?
        get() {
            val value = internalInvoiceToApproveOrReject.value
            return if (value != null) {
                // NOTE: Schön zu sehen ist Pattern-Matching: Dadurch, dass es sich um eine Sealed-Klasse handelt,
                // kann Kotlin erkennen, ob alle Fälle abgehandelt wurden. Fügt ein anderer Entwickler einen neuen
                // Case ein, so führt das nicht zu einem Laufzeitfehler (wie in Java), sondern der Compiler gibt
                // bereits beim Bauen einen Fehler aus.
                when (value) {
                    InvoiceToApproveOrReject.None -> null
                    is InvoiceToApproveOrReject.Some -> value.invoice
                }
            } else {
                null
            }
        }
}

sealed class InvoiceToApproveOrReject {
    object None : InvoiceToApproveOrReject()
    data class Some(val invoice: SavedInvoice) : InvoiceToApproveOrReject()
}