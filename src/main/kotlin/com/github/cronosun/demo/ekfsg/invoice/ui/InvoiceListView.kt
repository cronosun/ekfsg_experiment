package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.rx.bindToCustom
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.grid.Grid

class InvoiceListView(private val controller: InvoiceListController) : Composite<Component>() {
    override fun initContent(): Component {
        val grid = Grid<InvoiceInTable>()

        grid.addColumn(InvoiceInTable::ssn).setHeader("SSN")
        grid.addColumn(InvoiceInTable::createdAt).setHeader("Datum")
        grid.addColumn(InvoiceInTable::uploadedBy).setHeader("Uploaded by")
        controller.invoicesInTable.bindToCustom(grid) { component, data ->
            component.setItems(data)
        }
        grid.addSelectionListener {
            val selectedItems = it.allSelectedItems
            if (selectedItems.isEmpty()) {
                controller.select(InvoiceSelection.None)
            } else {
                val invoice = selectedItems.first().source
                controller.select(InvoiceSelection.Some(invoice))
            }
        }
        return grid
    }
}