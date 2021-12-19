package com.github.cronosun.demo.ekfsg.shared.rx

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasText
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

fun <THasText> THasText.rxHasTextBind(observable: Observable<String>) where THasText : HasText, THasText : Component {
    HasTextReadValueBinding(observable.distinct(), this)
}

private data class HasTextReadValueBinding<THasText>(
    private val observable: Observable<String>,
    private val component: THasText
) where THasText : HasText, THasText : Component {

    private var disposable: Disposable? = null

    init {
        if (component.isAttached) {
            onAttached()
        }
        component.addAttachListener {
            onAttached()
        }
        component.addDetachListener {
            onDetached()
        }
    }

    private fun onAttached() {
        dispose()
        disposable = observable.subscribe {
            component.text = it
        }
    }

    private fun onDetached() {
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
