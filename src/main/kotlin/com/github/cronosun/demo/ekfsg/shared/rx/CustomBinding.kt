package com.github.cronosun.demo.ekfsg.shared.rx

import com.vaadin.flow.component.Component
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable

fun <T : Any, TComponent : Component> Observable<T>.bindToCustom(
    component: TComponent,
    applicator: (component: TComponent, value: T) -> Unit
) {
    CustomBinding(this, component, applicator)
}

private data class CustomBinding<T : Any, TComponent : Component>(
    private val observable: Observable<T>,
    private val component: TComponent,
    private val applicator: (component: TComponent, value: T) -> Unit
) {

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
            applicator(component, it)
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

