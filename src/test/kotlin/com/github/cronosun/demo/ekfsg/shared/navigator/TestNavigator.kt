package com.github.cronosun.demo.ekfsg.shared.navigator

class TestNavigator : Navigator {

    private val mutableNavigationRequests = mutableListOf<Navigation>()
    val navigationRequests: List<Navigation> get() = mutableNavigationRequests

    override fun navigate(request: Navigation) {
        mutableNavigationRequests.add(request)
    }

    fun count(request: Navigation): Int {
        return mutableNavigationRequests.count { it == request }
    }

    fun has(request: Navigation): Boolean {
        return count(request) > 0
    }

    fun clear() {
        mutableNavigationRequests.clear()
    }
}

