package com.github.cronosun.demo.ekfsg.file.ui

import com.github.cronosun.demo.ekfsg.shared.ControllerProvider
import com.github.cronosun.demo.ekfsg.shared.navigator.Navigation
import com.github.cronosun.demo.ekfsg.views.MainView
import com.github.mvysny.karibudsl.v10.text
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import org.springframework.beans.factory.annotation.Autowired

private const val PATH = "/documents"

@PageTitle("Dokumente im System")
@Route(value = PATH, layout = MainView::class)
class UploadsPageView(
    @Autowired val controller: ControllerProvider
) : VerticalLayout() {
    init {
        add(text(text = "Sie sehen eine Liste aller Dokumente welche sich im System befinden."))
        add(UploadsView(controller.get.uploads))
    }

    companion object {
        val NAVIGATION = Navigation.Path(PATH)
    }
}