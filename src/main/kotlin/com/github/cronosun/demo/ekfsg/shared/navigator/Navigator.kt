package com.github.cronosun.demo.ekfsg.shared.navigator

import com.vaadin.flow.router.QueryParameters

interface Navigator {
    fun navigate(request: Navigation)
}

sealed interface Navigation {
    data class Path(val path: String) : Navigation
    data class WithData(val path: String, val parameters: QueryParameters) : Navigation
}