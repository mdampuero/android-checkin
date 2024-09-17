package cl.clickgroup.checkin.utils

object RutValidatorUtils {

    fun isValidRut(rut: String): Boolean {
        val regex = Regex("^[1-9]\\d{0,7}-(\\d|k|K)$")
        if (!regex.matches(rut)) {
            return false
        }

        val parts = rut.split("-")
        if (parts.size != 2) return false

        val numberPart = parts[0]
        val verifierPart = parts[1].toUpperCase()

        return verifyDigit(numberPart, verifierPart)
    }

    fun extractRut(url: String): String? {
        val regex = Regex("RUN=(\\d+-[\\dkK])")
        val matchResult = regex.find(url)
        return matchResult?.let {
            it.groupValues[1]
        }
    }

    private fun verifyDigit(numberPart: String, verifierPart: String): Boolean {
        val reversedDigits = numberPart.reversed().map { it.toString().toInt() }
        var sum = 0
        var multiplier = 2

        for (digit in reversedDigits) {
            sum += digit * multiplier
            multiplier = if (multiplier == 7) 2 else multiplier + 1
        }

        val expectedVerifier = 11 - (sum % 11)
        return when (expectedVerifier) {
            11 -> verifierPart == "0"
            10 -> verifierPart == "K"
            else -> verifierPart == expectedVerifier.toString()
        }
    }


}