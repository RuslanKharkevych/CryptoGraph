package me.khruslan.cryptograph.data.tests

import com.google.common.truth.Truth.assertThat
import me.khruslan.cryptograph.data.fixtures.STUB_DTO_PREFERENCES
import me.khruslan.cryptograph.data.fixtures.STUB_PREFERENCES
import me.khruslan.cryptograph.data.preferences.mapper.PreferencesMapper
import org.junit.Before
import org.junit.Test

internal class PreferencesMapperTests {

    private lateinit var mapper: PreferencesMapper

    @Before
    fun setUp() {
        mapper = PreferencesMapper()
    }

    @Test
    fun `Map preferences`() {
        val preferences = mapper.mapPreferences(STUB_DTO_PREFERENCES)
        assertThat(preferences).isEqualTo(STUB_PREFERENCES)
    }

    @Test
    fun `Map theme`() {
        val themeValue = mapper.mapTheme(STUB_PREFERENCES.theme)
        assertThat(themeValue).isEqualTo(STUB_DTO_PREFERENCES.themeValue)
    }

    @Test
    fun `Map chart style`() {
        val chartStyleValue = mapper.mapChartStyle(STUB_PREFERENCES.chartStyle)
        assertThat(chartStyleValue).isEqualTo(STUB_DTO_PREFERENCES.chartStyleValue)
    }

    @Test
    fun `Map chart period`() {
        val chartPeriodValue = mapper.mapChartPeriod(STUB_PREFERENCES.chartPeriod)
        assertThat(chartPeriodValue).isEqualTo(STUB_DTO_PREFERENCES.chartPeriodValue)
    }
}