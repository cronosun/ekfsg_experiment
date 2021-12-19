package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@PageTitle("Genehmigte Rechnungen")
@Route(value = "/approved-invoices", layout = MainView::class)
class ApproveInvoicePageView(
    @Autowired
    val controller: ControllerProvider
) : VerticalLayout() {
    init {
        add(text(text = "Sie sehen hier eine Liste von genehmigten Rechnungen."))
        val invoiceListController = controller.get.approvedInvoiceListController
        val listOfInvoices = InvoiceListView(invoiceListController)
        add(listOfInvoices)
    }
}