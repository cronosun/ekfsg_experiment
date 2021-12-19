package com.github.cronosun.demo.ekfsg.shared.rx

import com.vaadin.flow.component.*
import com.vaadin.flow.component.html.Div
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function

/**
 * Extension method: render a stream using RxComponent.
 */
fun <TComponent : Component, T : Any> Observable<T>.render(mapper: Function<in T, out TComponent>): Component {
    return RxComponent(this.map {
        mapper.apply(it)
    })
}

private class RxComponent<T : Component>(val observable: Observable<T>) : Composite<Div>() {
    private var subscription: Disposable? = null

    override fun onAttach(attachEvent: AttachEvent?) {
        super.onAttach(attachEvent)
        unsubscribe()
        subscription = observable.subscribe({
            setContentInternal(it)
        }, {
            // TODO: Here you'd perform error handling. For this simple demo: just display the exception.
            setContentInternal(Text("Got error: $it"))
        })
    }

    override fun onDetach(detachEvent: DetachEvent?) {
        unsubscribe()
        super.onDetach(detachEvent)
    }

    private fun setContentInternal(content: Component) {
        val thisContent = this.content
        thisContent.removeAll()
        thisContent.add(content)
    }

    private fun unsubscribe() {
        val localSubscription = subscription
        if (localSubscription != null) {
            localSubscription.dispose()
            subscription = null
        }
    }
}