package com.github.cronosun.demo.ekfsg.shared.currency

sealed class MaybeCurrencyAmount {
    data class Some(val amount: CurrencyAmount) : MaybeCurrencyAmount() {
        override fun toString(): String {
            return amount.toString()
        }
    }

    object None : MaybeCurrencyAmount() {
        override fun toString(): String {
            return ""
        }
    }

    companion object {
        fun tryParseFromString(string: String?): MaybeCurrencyAmount? {
            return if (string == null || string.isBlank()) {
                MaybeCurrencyAmount.None
            } else {
                val maybeSome = CurrencyAmount.tryParseFromString(string)
                if (maybeSome != null) {
                    MaybeCurrencyAmount.Some(maybeSome)
                } else {
                    // seems to be invalid
                    null
                }
            }
        }

        fun fromMaybeCents(cents: Int?): MaybeCurrencyAmount {
            return if (cents == null) {
                return None
            } else {
                Some(CurrencyAmount(cents))
            }
        }
    }

    fun toMaybeCents(): Int? {
        return when (this) {
            None -> null
            is Some -> this.amount.cents
        }
    }
}