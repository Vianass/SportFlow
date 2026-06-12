package com.sportflow.app

import com.sportflow.app.model.AppLanguage
import com.sportflow.app.ui.localization.translateText
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalizationTest {
    @Test
    fun portugueseKeepsOriginalText() {
        assertEquals("INÍCIO", translateText("INÍCIO", AppLanguage.PT))
    }

    @Test
    fun englishTranslatesNavigationAndDynamicValues() {
        assertEquals("HOME", translateText("INÍCIO", AppLanguage.EN))
        assertEquals("12 SPOTS LEFT", translateText("12 VAGAS RESTANTES", AppLanguage.EN))
        assertEquals("STARTS IN 3H", translateText("INICIA EM 3H", AppLanguage.EN))
    }

    @Test
    fun englishTranslatesDatesAndPositions() {
        assertEquals("15 MAY, 2024", translateText("15 MAIO, 2024", AppLanguage.EN))
        assertEquals("05 JUNE, 18:00", translateText("05 de JUNHO, 18:00", AppLanguage.EN))
        assertEquals("4th", translateText("4º", AppLanguage.EN))
        assertEquals(
            "Avenida do Atlântico, Viana do Castelo",
            translateText("Avenida do Atlântico, Viana do Castelo", AppLanguage.EN),
        )
    }
}
