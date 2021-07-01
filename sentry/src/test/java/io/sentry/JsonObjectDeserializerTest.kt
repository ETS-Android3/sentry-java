package io.sentry

import org.junit.Test
import java.io.StringReader
import java.lang.Exception
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.fail

class JsonObjectDeserializerTest {

    private class Fixture {
        fun getSut(): JsonObjectDeserializer {
            return JsonObjectDeserializer()
        }
    }

    private val fixture = Fixture()

    @Test
    fun `deserialize null`() {
        val json = "null"
        val actual = deserialize(json)

        assertNull(actual)
    }

    @Test
    fun `deserialize string fails`() {
        val json = "\"String\""
        try {
            deserialize(json)
            fail()
        } catch (e: Exception) {
            // Success
        }
    }

    @Test
    fun `deserialize int fails`() {
        val json = "1"
        try {
            deserialize(json)
            fail()
        } catch (e: Exception) {
            // Success
        }
    }

    @Test
    fun `deserialize double fails`() {
        val json = "1.1"
        try {
            deserialize(json)
            fail()
        } catch (e: Exception) {
            // Success
        }
    }

    @Test
    fun `deserialize array fails`() {
        val json = "[\"a\",\"b\"]"
        try {
            deserialize(json)
            fail()
        } catch (e: Exception) {
            // Success
        }
    }

    @Test
    fun `deserialize malformed fails`() {
        val json = "{\"fixture-key\": \"fixture-value\""
        try {
            deserialize(json)
            fail()
        } catch (e: Exception) {
            // Success
        }
    }

    @Test
    fun `deserialize json string`() {
        val json = "{\"fixture-key\": \"fixture-value\"}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to "fixture-value"), actual)
    }

    @Test
    fun `deserialize json object int`() {
        val json = "{\"fixture-key\": 123}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to 123), actual)
    }

    @Test
    fun `deserialize json object double`() {
        val json = "{\"fixture-key\": 123.321}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to 123.321), actual)
    }

    @Test
    fun `deserialize json object boolean`() {
        val json = "{\"fixture-key\": true}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to true), actual)
    }

    @Test
    fun `deserialize json object string array`() {
        val json = "{\"fixture-key\":[\"fixture-entry-1\",\"fixture-entry-2\"]}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to listOf("fixture-entry-1", "fixture-entry-2")), actual)
    }

    @Test
    fun `deserialize json object int array`() {
        val json = "{\"fixture-key\":[1,2]}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to listOf(1, 2)), actual)
    }

    @Test
    fun `deserialize json object double array`() {
        val json = "{\"fixture-key\":[1.1,2.2]}"
        val actual = deserialize(json)

        assertEquals(mapOf("fixture-key" to listOf(1.1, 2.2)), actual)
    }

    @Test
    fun `deserialize json object object`() {
        val json = """
        {
            "key": {
                "key": "value"
            }
        }
        """.trimIndent()

        val expected = mapOf<String, Any>(
            "key" to mapOf(
                "key" to "value"
            )
        )

        val actual = deserialize(json)
        assertEquals(expected, actual)
    }

    @Test
    fun `deserialize json object object with nesting`() {
        val json = """
        {
            "fixture-key":
            {
                "string": "fixture-string",
                "int": 123,
                "double": 123.321,
                "boolean": true,
                "array":
                [
                    "a",
                    "b",
                    "c"
                ],
                "object":
                {
                    "key": "value"
                }
            }
        }
        """.trimIndent()

        val expected = mapOf<String, Any>(
            "fixture-key" to mapOf(
                "string" to "fixture-string",
                "int" to 123,
                "double" to 123.321,
                "boolean" to true,
                "array" to listOf("a", "b", "c"),
                "object" to mapOf(
                    "key" to "value"
                )
            )
        )

        val actual = deserialize(json)
        assertEquals(expected, actual)
    }

    // Helper

    private fun deserialize(string: String): Map<String, Any>? {
        val rdr = StringReader(string)
        val jsonRdr = JsonObjectReader(rdr)
        return fixture.getSut().deserialize(jsonRdr)
    }
}
