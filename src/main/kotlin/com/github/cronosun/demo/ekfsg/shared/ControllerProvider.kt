package com.github.cronosun.demo.ekfsg.shared

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.mail.MailService
import com.github.cronosun.demo.ekfsg.shared.navigator.Navigator
import com.vaadin.flow.spring.annotation.VaadinSessionScope
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

interface ControllerProvider {
    val get: Controllers
}

/**
 * Die controller sind stateful (anders als die Services) - sie müssen an die Session gehängt werden ("Session Scope").
 */
@VaadinSessionScope
@Component
class DefaultControllerProvider(
    @Autowired private val invoiceService: InvoiceService,
    @Autowired private val fileStorageService: FileStorageService,
    @Autowired private val fileContentService: FileContentService,
    @Autowired private val navigator: Navigator,
    @Autowired private val mailService: MailService,

    ) : ControllerProvider {

    override val get by lazy {
        Controllers(invoiceService, fileStorageService, fileContentService, navigator, mailService)
    }
}