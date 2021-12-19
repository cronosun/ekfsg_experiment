package com.github.cronosun.demo.ekfsg.uc

import com.github.cronosun.demo.ekfsg.SpringTestBase
import com.github.cronosun.demo.ekfsg.file.TestFiles
import com.github.cronosun.demo.ekfsg.invoice.ui.InvoiceAddedView
import com.github.cronosun.demo.ekfsg.invoice.ui.InvoiceToApproveOrReject
import com.github.cronosun.demo.ekfsg.mail.MailExample
import com.github.cronosun.demo.ekfsg.mail.SenderBackendSimulator
import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.navigator.TestNavigator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Testet den in der "fiktiven" Spezifikation definierten Use-Case 705 "Rechnung freigeben".
 *
 * Was hier zu sehen ist: Man kann nah an der Spezifikation Testen; es ist möglich auch komplexere Use-Cases
 * abzuarbeiten: Prozesse wo mehrere Benutzer (Benutzer A und Mitarbeiter B) involviert sind. Das ist möglich
 * Dank dem eifachen Aufbau dieser eKFSG-App, den Simulatoren und dem Design welches auf Testbarkeit
 * ausgelegt ist.
 */
class UseCaseUC705Test(
    @Autowired private val controllers: ControllerProvider,
    @Autowired private val navigator: TestNavigator,
    @Autowired private val senderBackend: SenderBackendSimulator
) : SpringTestBase() {

    /**
     * Auszug aus der Spezifikation (oder dem Issue): "Der Benutzer A reicht über den externen Zugang eine Rechnung ein.
     * Der Mitarbeiter B (Kanton) sieht danach diese Rechnung in der Ansicht XY. Der Mitarbeiter B hat die Möglichkeit diese
     * Rechnung abzulehnen. Der Benutzer A wird per Mail informiert, dass die Rechnnung abgelehnt wurde."
     */
    @Test
    fun test_uc705_fua47() {
        navigator.clear()
        senderBackend.clearAll()

        // ---------------- Benutzer A ("externer Zugang")

        // navigieren auf die Rechnungseingabe vom "Externen Zugang"
        val externalInvoice = controllers.get.externalInvoice
        // Eingabe der Sozialversicherngsnummer
        externalInvoice.ssn.onNext("756.9217.0769.85")
        // Hochladen des Rechnungsdokuments
        TestFiles.simpleTextDocument.uploadToController(externalInvoice.invoiceDocumentController)
        // Einreichen der Rechnung
        externalInvoice.submit()

        // Nun sollte das System auf die Bestätigunsseite navigiert haben
        assertEquals(1, navigator.count(InvoiceAddedView.NAVIGATION))
        // ... und der Benutzer A hat auch eine Mail erhalten
        assertEquals(1, senderBackend.numberOfMatches(MailExample(subject = "Rechnung erfasst")))

        // ---------------- Mitarbeiter B ("interner Zugang")

        navigator.clear()
        senderBackend.clearAll()
        // navigieren auf die Seite "Rechnung genehmigen oder ablehnen."
        val approveReject = controllers.get.approveRejectInvoicePageController
        approveReject.onAttach()
        // sieht der Mitarbeiter die eingereichte Rechnung? (die mit der SSN "756.9217.0769.85")
        val entriesInTable = approveReject.invoiceListController.invoicesInTable.blockingFirst()
        val maybeInvoice = entriesInTable.find { it.ssn == "756.9217.0769.85" }
        assertNotNull(maybeInvoice)
        // Auswählen dieser Rechnung aus der Tabelle (selektieren)
        approveReject.approveRejectInvoiceController.approveOrRejectInvoice(InvoiceToApproveOrReject.Some(maybeInvoice!!.source))
        // nun die Rechnung ablehnen
        approveReject.approveRejectInvoiceController.reject()

        // ---------------- Benutzer A

        // der Benutzer A sollte nun ne Mail erhalten haben, dass seine Rechnung abgelehnt wurde
        assertEquals(1, senderBackend.numberOfMatches(MailExample(subject = "Rechung Abgelehnt")))
    }

    // <...> viele weitere solche Tests sind möglich, Schritt für Schritt kann die Spezifikation abgearbeitet werden.
}