package com.github.cronosun.demo.ekfsg.invoice

import com.github.cronosun.demo.ekfsg.file.FileId
import com.github.cronosun.demo.ekfsg.shared.currency.MaybeCurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.MaybeIban
import com.github.cronosun.demo.ekfsg.shared.ssn.Ssn

/**
 * Rechnung, wie vom Benutzer im externen Zugang eingegeben.
 *
 * Zu sehen ist: Da gibts kein ID/UUID, kein Status, kein Timestamp (denn das wird ja auch nicht eingegeben
 * vom Benutzer) - Also ein spezifisches Pojo für diesen Use-Case (im Gegensatz zu einem "generischen"
 * Entity wo die Tabellenstruktur nach Aussgen leakt - und die Hälfte der Felder "null" ist).
 */
data class NewExternalInvoice(
    override val amount: MaybeCurrencyAmount,
    override val comment: String,
    override val document: FileId,
    override val ssn: Ssn,
    override val iban: MaybeIban
) : Invoice