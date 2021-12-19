package com.github.cronosun.demo.ekfsg.shared.ssn

@JvmInline
value class Ssn(val ssn: String) {

    init {
        validate()
    }

    private fun validate() {
        if (!isValid(ssn)) {
            throw AssertionError("Got invalid SSN: $ssn")
        }
    }

    companion object {
        private val ssnRegex = Regex("^756[.]\\d{4}[.]\\d{4}[.]\\d{2}\$")

        fun tryFromString(ssn: String): Ssn? {
            return if (isValid(ssn)) {
                Ssn(ssn)
            } else {
                null
            }
        }

        fun isValid(ssn: String): Boolean {
            // only basic ssn validator for this demo (no checksum check)
            return ssnRegex.matches(ssn)
        }
    }

    override fun toString(): String {
        return ssn
    }
}