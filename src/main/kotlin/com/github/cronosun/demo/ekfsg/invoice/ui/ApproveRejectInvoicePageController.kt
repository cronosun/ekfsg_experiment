package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import io.reactivex.rxjava3.disposables.Disposable
import org.springframework.beans.factory.annotation.Autowired

class ApproveRejectInvoicePageController(
    @Autowired
    val invoiceService: InvoiceService
) {
    val invoiceListController = InvoiceListController(invoiceService)
    val approveRejectInvoiceController = ApproveRejectInvoiceController(invoiceService)
    private var subscription: Disposable? = null

    init {
        approveRejectInvoiceController.listOfInvoicesChanged = {
            invoiceListController.reloadListAndResetSelection()
        }
    }

    fun onAttach() {
        onDetach()
        // connect the selection from the list to the approve-reject controller
        subscription = invoiceListController.selection.subscribe {
            val maybeInvoice = when (it) {
                InvoiceSelection.None -> InvoiceToApproveOrReject.None
                is InvoiceSelection.Some -> InvoiceToApproveOrReject.Some(it.invoice)
            }
            approveRejectInvoiceController.approveOrRejectInvoice(maybeInvoice)
        }
    }

    fun onDetach() {
        val localSubscription = subscription
        if (localSubscription != null) {
            localSubscription.dispose()
            subscription = null
        }
    }
}