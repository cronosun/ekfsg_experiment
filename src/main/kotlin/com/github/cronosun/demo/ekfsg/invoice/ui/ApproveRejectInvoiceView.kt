package com.github.cronosun.demo.ekfsg.invoice.ui

import com.github.cronosun.demo.ekfsg.shared.rx.rxBind
import com.github.cronosun.demo.ekfsg.shared.rx.rxHasTextBind
import com.github.cronosun.demo.ekfsg.shared.views.ValidationView
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent

class ApproveRejectInvoiceView(
    private val controller: ApproveRejectInvoiceController
) : KComposite() {

    init {
        ui {
            verticalLayout {
                maxWidth = "700px"
                isSpacing = true
                isPadding = false
                isMargin = false

                strong(text = "Sozialversicherungsnummer")
                text(text = "") {
                    rxHasTextBind(controller.ssn)
                }

                // TODO: Rechnungsdokument download...

                h5("IBAN")
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

                h5("Rechnungsbetrag")
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

                hr()

                horizontalLayout {
                    button {
                        icon = Icon(VaadinIcon.FILE_REMOVE)
                        text = "Ablehnen"
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        addClickListener {
                            controller.reject()
                        }
                    }
                    button {
                        icon = Icon(VaadinIcon.ARROW_FORWARD)
                        text = "Genehmigen"
                        addThemeVariants(ButtonVariant.LUMO_PRIMARY)
                        addClickListener {
                            controller.approve()
                        }
                    }
                }
            }
        }
    }

}