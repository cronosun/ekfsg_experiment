package com.github.cronosun.demo.ekfsg.shared.iban

sealed class MaybeIban {
    object None : MaybeIban() {
        override fun toString(): String {
            return ""
        }
    }

    data class Some(val iban: Iban) : MaybeIban() {
        override fun toString(): String {
            return iban.toString()
        }
    }

    companion object {
        fun tryFromString(string: String?): MaybeIban? {
            return if (string == null || string.isBlank()) {
                None
            } else {
                val maybeIban = Iban.tryFrom(string)
                if (maybeIban != null) {
                    Some(maybeIban)
                } else {
                    // validation error
                    null
                }
            }
        }

        fun isValid(string: String?): Boolean {
            return if (string == null || string.isBlank()) {
                true
            } else {
                Iban.isValid(string)
            }
        }
    }
}