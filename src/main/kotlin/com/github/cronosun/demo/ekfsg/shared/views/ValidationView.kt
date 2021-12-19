package com.github.cronosun.demo.ekfsg.shared.views

import com.vaadin.flow.component.AttachEvent
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.DetachEvent
import com.vaadin.flow.component.html.Span
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Displays a validation error if something is invalid.
 */
class ValidationView(private val isInvalid: Observable<Boolean>, private val text: String) : Composite<Span>() {
    private var isCurrentlyInvalid = false
    private var disposable: Disposable? = null

    private fun onAttached() {
        dispose()
        disposable = isInvalid.subscribe {
            if (it != isCurrentlyInvalid) {
                isCurrentlyInvalid = it
                if (it) {
                    content.removeAll()
                    content.add(createInvalidComponent())
                } else {
                    content.removeAll()
                }
            }
        }
    }

    private fun createInvalidComponent(): Component {
        val error = Span(text)
        error.element.themeList.add("badge error pill")
        return error
    }

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        onAttached()
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        super.onDetach(detachEvent)
        dispose()
    }

    private fun dispose() {
        val localDisposable = disposable
        if (localDisposable != null) {
            localDisposable.dispose()
            disposable = null
        }
    }
}