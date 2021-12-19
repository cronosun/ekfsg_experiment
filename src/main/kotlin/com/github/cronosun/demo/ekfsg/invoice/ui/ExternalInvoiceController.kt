package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.file.ui.UploadController
import com.github.cronosun.demo.ekfsg.file.ui.UploadState
import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.invoice.NewExternalInvoice
import com.github.cronosun.demo.ekfsg.shared.currency.MaybeCurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.MaybeIban
import com.github.cronosun.demo.ekfsg.shared.navigator.Navigator
import com.github.cronosun.demo.ekfsg.shared.ssn.Ssn
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

class ExternalInvoiceController(
    private val invoiceService: InvoiceService,
    fileStorageService: FileStorageService,
    fileContentService: FileContentService,
    private val navigator: Navigator
) {

    private val isValidating: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    val invoiceDocumentController = UploadController(fileStorageService, fileContentService)

    val ssn: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    // so simpel ist die Validierung der Sozialversicherungsnummer mit RxJava.
    val ssnInvalid: Observable<Boolean> = Observable.combineLatest(isValidating, ssn) { isValidating, ssn ->
        isValidating && !Ssn.isValid(ssn)
    }

    val amount: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val amountInvalid: Observable<Boolean> = Observable.combineLatest(isValidating, amount) { isValidating, amount ->
        isValidating && (MaybeCurrencyAmount.tryParseFromString(amount) == null)
    }

    val iban: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val ibanInvalid: Observable<Boolean> = Observable.combineLatest(isValidating, iban) { isValidating, iban ->
        isValidating && (!MaybeIban.isValid(iban))
    }

    val invoiceDocumentInvalid: Observable<Boolean> =
        Observable.combineLatest(isValidating, invoiceDocumentController.observableState) { isValidating, state ->
            isValidating && state == UploadState.NoFileUploadedYet
        }

    val comment: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    fun submit() {
        val validationOk = trySubmit()
        if (validationOk) {
            navigateFinished()
            reset()
        } else {
            // error, make sure validation is active
            isValidating.onNext(true)
        }
    }

    private fun reset() {
        // reset the controller, so the user has a fresh controller for the next invoice
        isValidating.onNext(false)
        invoiceDocumentController.reset()
        ssn.onNext("")
        amount.onNext("")
        iban.onNext("")
        comment.onNext("")
    }

    private fun navigateFinished() {
        navigator.navigate(InvoiceAddedView.NAVIGATION)
    }

    private fun trySubmit(): Boolean {
        val amount = MaybeCurrencyAmount.tryParseFromString(amount.value) ?: return false

        val file = when (val uploadState = invoiceDocumentController.state) {
            is UploadState.FileUploaded -> {
                uploadState.file
            }
            UploadState.NoFileUploadedYet -> return false
        }
        val iban = MaybeIban.tryFromString(iban.value) ?: return false
        val ssnString = this.ssn.value
        val ssn = if (ssnString != null) {
            Ssn.tryFromString(ssnString) ?: return false
        } else {
            return false
        }

        val newInvoice = NewExternalInvoice(
            amount = amount,
            comment = this.comment.value!!,
            document = file.id,
            ssn = ssn,
            iban = iban
        )
        invoiceService.addInvoice(newInvoice)
        return true
    }
}