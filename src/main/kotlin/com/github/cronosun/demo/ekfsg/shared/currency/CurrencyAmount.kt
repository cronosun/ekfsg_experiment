package com.github.cronosun.demo.ekfsg.shared.currency

import java.lang.ArithmeticException
import java.math.BigDecimal

/**
 * Währungsbetrag mit "cent"-Präzision, ohne Angabe einer Währnung (fix auf CHF; je nach Anwendung müsste hier
 * auch noch die Währung rein). Muss positiv sein.
 */
@JvmInline
value class CurrencyAmount(val cents: Int) {
    init {
        if (cents < 0) {
            throw AssertionError("Amount must be positive")
        }
    }

    companion object {
        private val oneHundred = BigDecimal(100)

        fun tryParseFromString(string: String): CurrencyAmount? {
            val normalized = string.replace("'", "").replace("`", "")
                .replace(" ", "").replace(",", ".")
            return try {
                val bigDecimal = BigDecimal(normalized)
                val asCents = bigDecimal.multiply(oneHundred)
                val asCentsInt = asCents.intValueExact()
                if (asCentsInt >= 0) {
                    CurrencyAmount(asCentsInt)
                } else {
                    null
                }
            } catch (_: NumberFormatException) {
                null
            } catch (_: ArithmeticException) {
                null
            }
        }
    }

    override fun toString(): String {
        val asBigDecimal = BigDecimal(cents).divide(oneHundred)
        return asBigDecimal.toPlainString()
    }
}

