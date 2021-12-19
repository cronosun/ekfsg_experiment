package com.github.cronosun.demo.ekfsg.invoice

import com.github.cronosun.demo.ekfsg.shared.currency.CurrencyAmount
import com.github.cronosun.demo.ekfsg.shared.iban.Iban

/**
 * Auch hier wieder: Ein passendes Datenmodell zum passenden Use-Case ("Rechnung Genehmigen"). Zu sehen ist:
 *
 * Dinge wie "document", "ssn", "createdBy" ist hier nicht zu finden (denn diese Dinge werden in diesem Use-Case
 * auch nicht verändert). Auch "state" ist hier nicht drinn (denn der wird in diesem Use-Case fix auf APPROVED gesetzt,
 * der Benutzer kann den nicht setzen).
 *
 * Dinge wie "iban" und "amountCents" können jedoch in diesem Use-Case verfollständigt oder angepasst werden, darum
 * sind sie hier auch zu finden. Was jedoch zu sehen ist, dass die nun nicht mehr nullable sind (da sie mandatory
 * sind).
 */
data class InvoiceApproval(
    val id: InvoiceId,
    val iban: Iban,
    val amount: CurrencyAmount
)