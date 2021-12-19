package com.github.cronosun.demo.ekfsg.shared.rx

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.HasValue
import com.vaadin.flow.component.HasValue.ValueChangeEvent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

fun <E : ValueChangeEvent<V>, V : Any, THasValue : HasValue<E, V>> THasValue.rxBindWrite(observer: Observer<V>) {
    this.addValueChangeListener {
        val newValue = it.value
        observer.onNext(newValue)
    }
}

fun <E, V : Any, THasValue> THasValue.rxBindRead(observable: Observable<V>) where THasValue : HasValue<E, V>, THasValue : Component {
    ReadValueBinding(observable.distinct(), this)
}

fun <TRx, E : ValueChangeEvent<V>, V : Any, THasValue> THasValue.rxBind(reactive: TRx) where THasValue : HasValue<E, V>, THasValue : Component, TRx : Observer<V>, TRx : Observable<V> {
    this.rxBindRead(reactive)
    this.rxBindWrite(reactive)
}

private data class ReadValueBinding<T : Any, THasValue>(
    private val observable: Observable<T>,
    private val component: THasValue
) where THasValue : HasValue<*, T>, THasValue : Component {

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
            component.value = it
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

