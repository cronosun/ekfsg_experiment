package com.github.cronosun.demo.ekfsg.shared

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.file.ui.UploadsController
import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.invoice.InvoiceState
import com.github.cronosun.demo.ekfsg.invoice.ui.ApproveRejectInvoicePageController
import com.github.cronosun.demo.ekfsg.invoice.ui.ExternalInvoiceController
import com.github.cronosun.demo.ekfsg.invoice.ui.InvoiceListController
import com.github.cronosun.demo.ekfsg.mail.MailService
import com.github.cronosun.demo.ekfsg.mail.ui.SentMailsController
import com.github.cronosun.demo.ekfsg.shared.navigator.Navigator
import org.springframework.beans.factory.annotation.Autowired

/**
 * Hält die Controller / den State. In der Anwendung selber ist dieser Bereich
 * Session-Scoped. Alternativ könnten die Controller auch per '@VaadinSessionScope'
 * konfiguriert werden, dann wöre das hier überflüssig (müsste man aber in den Unit-Tests
 * konfigurieren - geht sicherlich, hab aber nicht genau geschaut wie).
 */
class Controllers(
    @Autowired private val invoiceService: InvoiceService,
    @Autowired private val fileStorageService: FileStorageService,
    @Autowired private val fileContentService: FileContentService,
    @Autowired private val navigator: Navigator,
    @Autowired private val mailService: MailService,
) {

    val externalInvoice: ExternalInvoiceController by lazy {
        ExternalInvoiceController(
            invoiceService, fileStorageService, fileContentService,
            navigator
        )
    }

    val approveRejectInvoicePageController: ApproveRejectInvoicePageController by lazy {
        ApproveRejectInvoicePageController(invoiceService)
    }

    val approvedInvoiceListController: InvoiceListController by lazy {
        val controller = InvoiceListController(invoiceService)
        controller.state.onNext(InvoiceState.APPROVED)
        controller
    }

    val sentMailsController: SentMailsController by lazy {
        SentMailsController(mailService)
    }

    val uploads: UploadsController by lazy {
        UploadsController(fileStorageService)
    }
}