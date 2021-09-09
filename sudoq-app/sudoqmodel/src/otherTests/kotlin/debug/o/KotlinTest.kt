package debug.o

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class KotlinTest {
    @Test
    fun testF() {
        Assertions.assertTrue(false)
    }

    @Test
    fun testT() {
        Assertions.assertTrue(true)
    }
}