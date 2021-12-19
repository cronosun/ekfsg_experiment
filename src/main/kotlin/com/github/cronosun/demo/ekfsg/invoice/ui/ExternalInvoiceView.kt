package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.file.ui.UploadView
import com.github.cronosun.demo.ekfsg.shared.rx.rxBind
import com.github.cronosun.demo.ekfsg.shared.views.ValidationView
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

/**
 * View für die Eingabe der Rechnung (Externer Zugang).
 *
 * TESTING: Diese View ist das Einzige was im Bereich Rechnung-Hochladen nicht getestet wird. Die View ist
 * deshalb sehr schlank zu halten. Keine Logik, kein if-else, rein nur das Layout der View und Datenbinding ist
 * hier zu sehen.
 */
class ExternalInvoiceView(private val controller: ExternalInvoiceController) : KComposite() {

    init {
        ui {
            // Demonstriert die Verwendung von Kotlin-DSL für Vaadin (karibudsl)
            verticalLayout {
                maxWidth = "700px"
                isSpacing = true
                isPadding = false
                isMargin = false

                h5("Sozialversicherungsnummer")
                textField {
                    placeholder = "Beispiel: 756.9217.0769.85"
                    isRequiredIndicatorVisible = true
                    isRequired = true
                    alignSelf = FlexComponent.Alignment.STRETCH
                    // Data binding
                    rxBind(controller.ssn)
                }
                // Validierungs-Anzeige für die Sozialversicherungsnummer.
                add(
                    ValidationView(
                        controller.ssnInvalid, "Die Sozialversicherungsnummer ist ungültig. Bitte " +
                                "geben Sie eine Nummer ein, in dieser Form '756.9217.0769.85'."
                    )
                )

                h5("Upload (Rechnungsdokument)")
                add(UploadView(controller.invoiceDocumentController))
                add(ValidationView(controller.invoiceDocumentInvalid, "Bitte laden Sie eine Rechnung hoch."))

                h5("Rechnungsbetrag (optional)")
                textField {
                    suffixComponent = Span("CHF")
                    alignSelf = FlexComponent.Alignment.STRETCH
                    // Data binding
                    rxBind(controller.amount)
                }
                add(
                    ValidationView(
                        controller.amountInvalid, "Der eingegebene Betrag ist ungültig. Beispiele " +
                                "für gültige Beträge: '150', '150.00', '14758.99'."
                    )
                )

                h5("IBAN (optional)")
                textField {
                    placeholder = "Beispiel: CH1880379000052717256"
                    isRequired = false
                    alignSelf = FlexComponent.Alignment.STRETCH
                    rxBind(controller.iban)
                }
                add(
                    ValidationView(
                        controller.ibanInvalid, "Die eingegebene IBAN-Nummer ist ungültig. Beispiel " +
                                "für eine gültige IBAN: CH1880379000052717256."
                    )
                )

                h5("Bemerkungen (optional)")
                textArea {
                    height = "10rem"
                    alignSelf = FlexComponent.Alignment.STRETCH
                    isRequired = false
                    rxBind(controller.comment)
                }

                hr()

                button {
                    icon = Icon(VaadinIcon.ARROW_FORWARD)
                    text = "Absenden"
                    addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                    addClickListener {
                        controller.submit()
                    }
                }
            }
        }
    }
}