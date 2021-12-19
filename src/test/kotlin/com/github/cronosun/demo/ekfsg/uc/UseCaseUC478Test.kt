package com.github.cronosun.demo.ekfsg.uc

import com.github.cronosun.demo.ekfsg.SpringTestBase
import com.github.cronosun.demo.ekfsg.file.TestFiles
import com.github.cronosun.demo.ekfsg.invoice.ui.InvoiceAddedView
import com.github.cronosun.demo.ekfsg.mail.MailExample
import com.github.cronosun.demo.ekfsg.mail.SenderBackendSimulator
import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.navigator.TestNavigator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * Testet den in der "fiktiven" Spezifikation definierten Use-Case 478 "Rechnung Einreichen" (externer Zugang).
 */
class UseCaseUC478Test(
    @Autowired private val controllers: ControllerProvider,
    @Autowired private val navigator: TestNavigator,
    @Autowired private val senderBackend: SenderBackendSimulator
) : SpringTestBase() {

    /**
     * Auszug aus der Spezifikation (oder dem Issue): "Benutzer muss eine Rechnung eingeben können, dazu reicht
     * aus, dass eine Datei vorhanden ist (das Rechnungsdokument) und die Sozialversicherungsnummer. Nach der
     * Erfolgreichen Eingabe sieht der Benutzer eine Bestätigunsseite."
     */
    @Test
    fun test_uc478_fua03() {
        navigator.clear()
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
    }

    /**
     * Auszug aus der Spezifikation (oder dem Issue): "Hat der Benutzer eine Rechnung eingegeben, so soll er
     * eine Bestätigunsmail erhalten".
     */
    @Test
    fun test_uc478_fua47() {
        senderBackend.clearAll()
        // navigieren auf die Rechnungseingabe vom "Externen Zugang"
        val externalInvoice = controllers.get.externalInvoice
        // Eingabe der Sozialversicherngsnummer
        externalInvoice.ssn.onNext("756.9217.0769.85")
        // Hochladen des Rechnungsdokuments
        TestFiles.simpleTextDocument.uploadToController(externalInvoice.invoiceDocumentController)
        // Einreichen der Rechnung
        externalInvoice.submit()

        // Mail vorhanden?
        assertEquals(1, senderBackend.numberOfMatches(MailExample(subject = "Rechnung erfasst")))
    }

    /**
     * Auszug aus der Spezifikation (oder dem Issue): "Wird keine gültige Sozialversicherungsnummer eingegeben, so soll
     * die Rechnung nicht eingereicht werden können. Eine Meldung wird dem Benutzer angezeigt."
     */
    @Test
    fun test_uc478_fua78() {
        senderBackend.clearAll()
        navigator.clear()
        // navigieren auf die Rechnungseingabe vom "Externen Zugang"
        val externalInvoice = controllers.get.externalInvoice
        // ... die kann nicht gültig sein...
        externalInvoice.ssn.onNext("BLAAAAAH BLA ...")
        // Hochladen des Rechnungsdokuments
        TestFiles.simpleTextDocument.uploadToController(externalInvoice.invoiceDocumentController)
        // Einreichen der Rechnung
        externalInvoice.submit()

        // das senden sollte nicht geklappt haben (keine Mail sollte da sein / kein Navigieren)
        assertEquals(0, senderBackend.numberOfMatches(MailExample(subject = "Rechnung erfasst")))
        assertEquals(0, navigator.count(InvoiceAddedView.NAVIGATION))

        // ... dafür sollte der Benutzer ne Meldung angezeigt kriegen
        assertTrue(externalInvoice.ssnInvalid.blockingFirst())
    }

    // <...> viele weitere solche Tests sind möglich, Schritt für Schritt kann die Spezifikation abgearbeitet werden.
}