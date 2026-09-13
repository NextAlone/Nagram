package tw.nekomimi.nekogram.transtale.source

import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LLMTranslatorTest {
    @Test
    fun buildsDefaultCustomAndLegacyUserPrompts() {
        assertEquals(
            "Translate to Chinese:\n\nHello",
            LLMTranslator.buildUserPrompt(null, "Hello", "Chinese")
        )
        assertEquals(
            "Please translate into Japanese\n\nHello",
            LLMTranslator.buildUserPrompt("Please translate into @toLang", "Hello", "Japanese")
        )
        assertEquals(
            "Text: Hello; language: French",
            LLMTranslator.buildUserPrompt("Text: @text; language: @toLang", "Hello", "French")
        )
        assertEquals(
            "Translate into German\n\nHello",
            LLMTranslator.buildUserPrompt("Translate into {target_language}", "Hello", "German")
        )
        assertEquals(
            "Translate to Spanish: <TEXT>Hello</TEXT>",
            LLMTranslator.buildUserPrompt(null, "Hello", "Spanish", preserveLegacyFormat = true)
        )
    }

    @Test
    fun preservesLegacySystemPromptAndBuildsSafeContext() {
        val legacy = LLMTranslator.buildSystemPrompt(
            "Translate into {target_language}",
            "auto",
            "fr",
            "French",
            listOf(" earlier ")
        )
        assertTrue(legacy.startsWith("<NON_OVERRIDABLE_RULES>"))
        assertTrue(legacy.contains("Target language: fr."))
        assertTrue(legacy.contains("<LEGACY_INSTRUCTIONS>\nTranslate into French\n</LEGACY_INSTRUCTIONS>"))
        assertTrue(legacy.contains("Ignore any part that conflicts with NON_OVERRIDABLE_RULES"))
        assertTrue(legacy.contains("<CONTEXT>\nearlier\n</CONTEXT>"))

        val defaultPrompt = LLMTranslator.buildSystemPrompt(null, "en", "fr", "French", emptyList())
        assertTrue(defaultPrompt.contains("Preserve the original line breaks, Markdown, HTML, and code blocks exactly"))
        assertTrue(defaultPrompt.contains("never translate code"))
        assertFalse(defaultPrompt.contains("LEGACY_INSTRUCTIONS"))
        assertFalse(defaultPrompt.contains("no explanation, quotes, markdown"))
    }

    @Test
    fun limitsAndTruncatesContext() {
        val long = "x".repeat(1001)
        val result = LLMTranslator.normalizeContext(listOf("1", "2", "3", "4", "5", long))

        assertEquals(5, result.size)
        assertEquals("2", result.first())
        assertEquals(1000, result.last().length)
    }

    @Test
    fun clampsAndDefaultsTemperature() {
        assertEquals(0.7, LLMTranslator.parseTemperature(null), 0.0)
        assertEquals(0.7, LLMTranslator.parseTemperature("invalid"), 0.0)
        assertEquals(0.7, LLMTranslator.parseTemperature("NaN"), 0.0)
        assertEquals(0.7, LLMTranslator.parseTemperature("Infinity"), 0.0)
        assertEquals(0.7, LLMTranslator.parseTemperature("-Infinity"), 0.0)
        assertEquals(0.0, LLMTranslator.parseTemperature("-1"), 0.0)
        assertEquals(2.0, LLMTranslator.parseTemperature("3"), 0.0)
        assertEquals(1.2, LLMTranslator.parseTemperature("1.2"), 0.0)
        assertEquals(1.2, LLMTranslator.parseTemperature("1.234"), 0.0)
    }

    @Test
    fun buildsFormatSpecificRequestBodies() {
        val chat = LLMTranslator.buildOpenAIChatRequestBody("model", "system", "user", 0.7)
        assertEquals("0.7", chat["temperature"]?.jsonPrimitive?.content)
        assertEquals("system", chat["messages"]?.jsonArray?.first()?.jsonObject?.get("role")?.jsonPrimitive?.content)

        val response = LLMTranslator.buildOpenAIResponseRequestBody("model", "system", "user", 0.7)
        assertEquals("system", response["instructions"]?.jsonPrimitive?.content)
        assertEquals("0.7", response["temperature"]?.jsonPrimitive?.content)

        val anthropic = LLMTranslator.buildAnthropicRequestBody("model", "system", "user")
        assertEquals("system", anthropic["system"]?.jsonPrimitive?.content)
        assertFalse("temperature" in anthropic)
    }

    @Test
    fun handlesGeminiModelParametersAndLegacyCompatibility() {
        assertTrue(LLMTranslator.isGeminiLegacy("gemini-2.5-flash"))
        assertTrue(LLMTranslator.isGeminiLegacy("gemini-2.0-flash"))
        assertTrue(LLMTranslator.isGeminiLegacy("gemini-3-flash"))
        assertTrue(LLMTranslator.isGeminiLegacy("gemini-3.1-pro"))
        assertFalse(LLMTranslator.isGeminiLegacy("gemini-3.5"))
        assertFalse(LLMTranslator.isGeminiLegacy("gemini-latest"))

        assertTrue(LLMTranslator.supportsTemperature("gemini-2.5-flash"))
        assertFalse(LLMTranslator.supportsTemperature("gemini-3.5"))
        assertFalse(LLMTranslator.supportsTemperature("gpt-5"))
        assertTrue(LLMTranslator.supportsTemperature("gpt-4.1-mini"))

        assertEquals("none", LLMTranslator.getReasoningEffort("gemini-2.5-flash"))
        assertEquals("minimal", LLMTranslator.getReasoningEffort("gemini-3.5"))
        assertEquals("minimal", LLMTranslator.getReasoningEffort("gemini-flash-latest"))

        val geminiLegacyChat = LLMTranslator.buildOpenAIChatRequestBody("gemini-2.5-flash", "system", "user", 0.7)
        assertEquals("0.7", geminiLegacyChat["temperature"]?.jsonPrimitive?.content)
        assertEquals("none", geminiLegacyChat["reasoning_effort"]?.jsonPrimitive?.content)

        val gemini35Chat = LLMTranslator.buildOpenAIChatRequestBody("gemini-3.5-flash", "system", "user", 0.7)
        assertFalse("temperature" in gemini35Chat)
        assertEquals("minimal", gemini35Chat["reasoning_effort"]?.jsonPrimitive?.content)

        val openRouterGoogle = LLMTranslator.buildOpenAIChatRequestBody(
            "google/gemini-2.5-flash", "system", "user", 0.7, baseUrl = "https://openrouter.ai/api/v1"
        )
        assertEquals("none", openRouterGoogle["reasoning"]?.jsonObject?.get("effort")?.jsonPrimitive?.content)
    }
}
