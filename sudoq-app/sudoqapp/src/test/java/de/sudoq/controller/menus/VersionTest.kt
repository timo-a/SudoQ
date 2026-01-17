package de.sudoq.controller.menus

import de.sudoq.controller.menus.SplashActivity.Version
import org.amshove.kluent.`should be less than`
import org.amshove.kluent.`should not be less than`
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class VersionTest {

    @ParameterizedTest
    @ValueSource(strings = ["1.1.0", "1.1.0a", "1.1.0b", "1.1.0c", "1.0.10"])
    fun `should parse`(string: String) {
        Version.parse(string)
    }

    @ParameterizedTest
    @ValueSource(strings = ["0.0.1", "0.1.0", "1.0.0", "1.1.0", "1.1.0a", "1.0.20", "1.0.20Z"])
    fun `should be smaller`(string: String) {
        val milestone = Version.parse("1.1.0b")

        Version.parse(string) `should be less than` milestone
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.1.0b", "1.1.0c", "1.1.1", "2.0.0"])
    fun `should not be smaller`(string: String) {
        val milestone = Version.parse("1.1.0b")
        Version.parse(string) `should not be less than` milestone
    }
}