package com.github.cronosun.demo.ekfsg.mail.ui

import com.github.cronosun.demo.ekfsg.mail.SentMail
import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.rx.bindToCustom
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

@PageTitle("Mail Postausgang")
@Route(value = "/outbox", layout = MainView::class)
class SentMailsView(
    @Autowired
    val controller: ControllerProvider
) : VerticalLayout() {
    init {
        add(text(text = "Hier sehen Sie die vom System versendeten Mail-Nachrichten."))
        val controller = controller.get.sentMailsController

        val grid = Grid<SentMail>()

        grid.addColumn(SentMail::to).setHeader("An")
        grid.addColumn(SentMail::subject).setHeader("Betreff")
        grid.addColumn(SentMail::sentAt).setHeader("Sendedatum")
        grid.addColumn(SentMail::from).setHeader("Von")
        grid.addColumn(SentMail::body).setHeader("Inhalt")

        controller.sentMails.bindToCustom(grid) { component, data ->
            component.setItems(data)
        }

        add(grid)
    }
}
