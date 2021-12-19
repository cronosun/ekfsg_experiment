package com.github.cronosun.demo.ekfsg.user

import org.springframework.stereotype.Service

/**
 * Hinweis: Nur "angedacht", müssten dann Session-Scoped sein.
 */
@Service
class UserService {
    // For this demo: No authentication, user is hardcoded
    private var currentUser: UserInfo? = UserInfo(UserId("test_user"), "Max", "Muster", "max.muster@mail.com")

    /**
     * NOTE: Zu sehen ist schön, dass Kotlin nullable direkt unterstützt ("?" in
     * "UserInfo?").
     */
    fun currentUser(): UserInfo? {
        return currentUser
    }

    fun requireCurrentUser(): UserInfo {
        val user = currentUser()
        if (user != null) {
            return user
        } else {
            throw RuntimeException("Operation requires a user (login required).")
        }
    }

    fun login(user: UserInfo) {
        currentUser = user
    }
}