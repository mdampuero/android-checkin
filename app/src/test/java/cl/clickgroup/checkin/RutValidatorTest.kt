package cl.clickgroup.checkin

import cl.clickgroup.checkin.utils.RutValidatorUtils
import org.junit.Test
import org.junit.Assert.*

class RutValidatorTest {
    @Test
    fun testIsValidRut() {
        // RUTs valid
        val validRuts = listOf(
            "12345678-5",
            "87654321-4",
            "15345678-K",
            "3403231-9",
            "77067083-7"
        )

        // RUTs invalid
        val invalidRuts = listOf(
            "3456789-2",
            "20345678-1",
            "12345678-0",
            "15345678-9",
            "1234-9",
            "87654321-9",
            "12a345678-5"
        )

        for (rut in validRuts) {
            assertTrue("Expected valid RUT: $rut", RutValidatorUtils.isValidRut(rut))
        }

        for (rut in invalidRuts) {
            assertFalse("Expected invalid RUT: $rut", RutValidatorUtils.isValidRut(rut))
        }
    }

    @Test
    fun testGetRutFromString() {
        // RUTs valid
        val stringsURL = listOf(
            "https://portal.sidiv.registrocivil.cl/docstatus?RUN=17703992-6&type=CEDULA_EXT&serial=603641129&mrz=603641129290123182809300",
            "https://portal.sidiv.registrocivil.cl/docstatus?RUN=12345678-5&type=CEDULA_EXT&serial=603641129&mrz=603641129290123182809300",
            "https://portal.sidiv.registrocivil.cl/docstatus?RUN=87654321-k&type=CEDULA_EXT&serial=603641129&mrz=603641129290123182809300",
            "https://portal.sidiv.registrocivil.cl/docstatus?RUN=87654321-K&type=CEDULA_EXT&serial=603641129&mrz=603641129290123182809300",
            "https://portal.sidiv.registrocivil.cl/docstatus?RUN=3403231-9&type=CEDULA_EXT&serial=603641129&mrz=603641129290123182809300",
            "strocivil.cl docstatus_RU31'9/type¿CEDULA/serial¿529012364/mrz¿529",
            "13245dsadsa1'099",
            ""
        )

        // RUTs invalid
        val ruts = listOf(
            "17703992-6",
            "12345678-5",
            "87654321-K",
            "87654321-K",
            "3403231-9",
            null,
            null,
            null,
        )

        for ((index, string) in stringsURL.withIndex()) {
            assertEquals(ruts[index], RutValidatorUtils.extractRut(string))
        }

    }
}