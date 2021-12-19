package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@PageTitle("Rechnung Einreichen")
@Route(value = "/add-invoice", layout = MainView::class)
class ExternalInvoicePageView(
    @Autowired
    val controller: ControllerProvider
) : VerticalLayout() {

    init {
        add(
            text(
                text = "Bitte reichen Sie über diese Seite eine Rechnung ein. Die Rechnung wird danach von der " +
                        "zuständigen Stelle geprüft und freigegeben."
            )
        )
        val externalInvoiceView = ExternalInvoiceView(controller.get.externalInvoice)
        add(externalInvoiceView)
    }
}