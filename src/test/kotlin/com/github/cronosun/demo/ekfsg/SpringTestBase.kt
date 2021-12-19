package com.github.cronosun.demo.ekfsg

import com.github.cronosun.demo.ekfsg.file.FileContentService
import com.github.cronosun.demo.ekfsg.file.FileStorageService
import com.github.cronosun.demo.ekfsg.invoice.InvoiceService
import com.github.cronosun.demo.ekfsg.mail.MailService
import com.github.cronosun.demo.ekfsg.mail.SenderBackend
import com.github.cronosun.demo.ekfsg.mail.SenderBackendSimulator
import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.Controllers
import com.github.cronosun.demo.ekfsg.shared.navigator.Navigator
import com.github.cronosun.demo.ekfsg.shared.navigator.TestNavigator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@SpringBootTest
class SpringTestBase {

    @Configuration
    class TestConfiguration {

        @Bean
        fun senderBackendSimulator(): SenderBackendSimulator {
            return SenderBackendSimulator()
        }

        @Bean
        fun senderBackend(simulator: SenderBackendSimulator): SenderBackend {
            return simulator
        }

        /**
         * Wir müssen das hier noch mal verdrahten, da wir in der "richtigen App" den Controller Session-Scoped
         * haben müssen; im Test jedoch nicht (geht sicherlich auch anders - hab da aber keine Zeit investiert).
         */
        @Bean
        @Primary
        fun controllerProvider(
            @Autowired invoiceService: InvoiceService,
            @Autowired fileStorageService: FileStorageService,
            @Autowired fileContentService: FileContentService,
            @Autowired navigator: Navigator,
            @Autowired mailService: MailService
        ): ControllerProvider {
            val controllers =
                Controllers(invoiceService, fileStorageService, fileContentService, navigator, mailService)
            return object : ControllerProvider {
                override val get: Controllers
                    get() = controllers

            }
        }

        @Bean
        fun testNavigator(): TestNavigator {
            return TestNavigator()
        }

        /**
         * Da wir nicht das ganze Vaadin-Zeugs hochziehen wollen, haben wir nen eigenen Navigator für die Tests.
         */
        @Bean
        @Primary
        fun navigator(testNavigator: TestNavigator): Navigator {
            return testNavigator
        }
    }
}