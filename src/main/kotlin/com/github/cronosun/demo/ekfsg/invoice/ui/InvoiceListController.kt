package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.invoice.SavedInvoice
import com.github.cronosun.demo.ekfsg.invoice.InvoiceState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant

class InvoiceListController(
    @Autowired
    val invoiceService: InvoiceService
) {
    private val internalSelectedItem: BehaviorSubject<InvoiceSelection> =
        BehaviorSubject.createDefault(InvoiceSelection.None)
    private val reloadTrigger: BehaviorSubject<Unit> = BehaviorSubject.createDefault(Unit)

    val state: BehaviorSubject<InvoiceState> = BehaviorSubject.createDefault(InvoiceState.NEW)

    fun select(selection: InvoiceSelection) {
        internalSelectedItem.onNext(selection)
    }

    val selection: Observable<InvoiceSelection> = internalSelectedItem.distinct()

    val invoicesInTable: Observable<List<InvoiceInTable>>
        get() {
            return reloadTrigger.flatMap {
                state.flatMap { state ->
                    Observable.create { emitter ->
                        val fromDatabase = loadFromDatabase(state)
                        emitter.onNext(fromDatabase)
                    }
                }
            }
        }

    fun reloadListAndResetSelection() {
        reloadTrigger.onNext(Unit)
        internalSelectedItem.onNext(InvoiceSelection.None)
    }

    private fun loadFromDatabase(state: InvoiceState): List<InvoiceInTable> {
        return invoiceService.listInvoicesSortedByDateDescendingInState(state, 100)
            .map { toInvoiceInTable(it) }.toList()
    }

    private fun toInvoiceInTable(invoice: SavedInvoice): InvoiceInTable {
        return InvoiceInTable(invoice.ssn.toString(), invoice.createdAt, invoice.createdBy.id, invoice)
    }
}

data class InvoiceInTable(
    val ssn: String, val createdAt: Instant,
    val uploadedBy: String, val source: SavedInvoice
)

sealed class InvoiceSelection {
    object None : InvoiceSelection()
    data class Some(val invoice: SavedInvoice) : InvoiceSelection()
}