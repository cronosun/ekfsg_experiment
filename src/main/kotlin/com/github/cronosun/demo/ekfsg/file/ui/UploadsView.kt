package com.github.cronosun.demo.ekfsg.file.ui

import com.github.cronosun.demo.ekfsg.shared.rx.bindToCustom
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.grid.Grid

/**
 * Zeigt eine tabelle mit allen uploads (dokumente) im system.
 *
 * NOTE: Zu sehen ist, dass diese view dumm ist, sie definiert lediglich das Layout und bindet Daten vom Controller
 * an die view; kein if-else oder sonstige Kontrollstrukturen.
 */
class UploadsView(val controller: UploadsController) : Composite<Component>() {

    override fun initContent(): Component {
        val grid = Grid<FileInTable>()

        grid.addColumn(FileInTable::name).setHeader("Filename")
        grid.addColumn(FileInTable::uploadedBy).setHeader("Uploaded by")
        controller.filesInTable.bindToCustom(grid) { component, data ->
            component.setItems(data)
        }
        return grid
    }
}

