package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.rx.bindToCustom
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.*
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@PageTitle("Genehmigen & Ablehnen von Rechnungen")
@Route(value = "/approve-reject-invoice", layout = MainView::class)
class ApproveRejectInvoicePageView(
    @Autowired
    val controller: ControllerProvider
) : VerticalLayout() {
    init {
        add(
            text(
                text = "Hier haben Sie die Möglichkeit, Rechnungen (eingegeben über den Externen Zugang) zu " +
                        "vervollständigen (Betrag, IBAN) und sie danach zu akzeptieren - oder Rechnnungen abzulehnen. Wählen " +
                        "Sie dazu eine Rechnung aus der Liste."
            )
        )
        val invoiceListController = controller.get.approveRejectInvoicePageController.invoiceListController
        val listOfInvoices = InvoiceListView(invoiceListController)
        add(listOfInvoices)

        val approveRejectInvoiceController =
            controller.get.approveRejectInvoicePageController.approveRejectInvoiceController
        val approveRejectInvoiceView = ApproveRejectInvoiceView(approveRejectInvoiceController)
        add(listOfInvoices)

        add(hr {})
        add(h4 {
            text = "Genehmigen / Ablehnen"
        })
        // the container where the invoice can be accepted or rejected (it's dynamic)
        val container = Div()
        add(container)

        invoiceListController.selection.bindToCustom(container) { component, selection ->
            when (selection) {
                InvoiceSelection.None -> {
                    component.removeAll()
                    component.add(strong {
                        text = "Bitte wählen Sie eine Rechnung aus der Liste, welche sie genehmigen oder " +
                                "ablehnen möchten."
                    })
                }
                is InvoiceSelection.Some -> {
                    component.removeAll()
                    component.add(approveRejectInvoiceView)
                }
            }
        }
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        controller.get.approveRejectInvoicePageController.onAttach()
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        controller.get.approveRejectInvoicePageController.onDetach()
        super.onDetach(detachEvent)
    }
}