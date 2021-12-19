package com.github.cronosun.demo.ekfsg.start

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.h5
import com.vaadin.flow.component.html.Image
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route

@Route(value = "/", layout = MainView::class)
@PageTitle("Startseite")
class StartView(
    val fileContentService: FileContentService,
    val fileStorageService: FileStorageService,
    val invoiceService: InvoiceService
) : VerticalLayout() {
    init {
        add(h5 { text = "Willkommen bei eKFSG" })

        val image = Image("images/IMG_20211216_172402.jpg", "Start Image")
        image.setSizeFull()
        add(image)
    }
}