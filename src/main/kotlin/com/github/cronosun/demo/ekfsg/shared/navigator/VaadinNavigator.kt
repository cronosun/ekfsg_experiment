package com.github.cronosun.demo.ekfsg.shared.navigator

import com.vaadin.flow.component.UI
import org.springframework.stereotype.Component

@Component
class VaadinNavigator : Navigator {
    override fun navigate(request: Navigation) {
        val currentUi = UI.getCurrent()
        if (currentUi != null) {
            when (request) {
                is Navigation.Path -> {
                    currentUi.navigate(request.path)
                }
                is Navigation.WithData -> {
                    currentUi.navigate(request.path, request.parameters)
                }
            }
        } else {
            throw RuntimeException(
                "There's currently no UI defined. Are you trying " +
                        "to navigate from a background thread?"
            )
        }
    }
}
