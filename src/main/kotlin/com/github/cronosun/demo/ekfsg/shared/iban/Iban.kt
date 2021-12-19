package com.github.cronosun.demo.ekfsg.shared.iban

@JvmInline
value class Iban constructor(val iban: String) {
    init {
        validate()
    }

    private fun validate() {
        if (!isValid(iban)) {
            throw AssertionError("Got invalid IBAN: $iban")
        }
    }

    override fun toString(): String {
        return iban
    }

    companion object {
        fun tryFrom(iban: String): Iban? {
            return if (isValid(iban)) {
                Iban(iban)
            } else {
                null
            }
        }

        fun isValid(iban: String): Boolean {
            // This validation is just for demonstration (would need to compute checksum, ...)
            return iban.startsWith("CH") && iban.length > 8
        }
    }
}