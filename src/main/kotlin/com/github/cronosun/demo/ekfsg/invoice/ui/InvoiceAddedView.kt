package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.navigator.Navigation
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

private const val PATH = "/invoice-added"

@PageTitle("Rechnung Hinzugefügt")
@Route(value = PATH, layout = MainView::class)
class InvoiceAddedView : VerticalLayout() {
    init {
        add(
            text(
                text = "Die Rechung wurde erfolgreich hinzugefügt. Die Rechnung wird danach von der " +
                        "zuständigen Stelle geprüft und freigegeben."
            )
        )
    }

    companion object {
        val NAVIGATION = Navigation.Path(PATH)
    }
}