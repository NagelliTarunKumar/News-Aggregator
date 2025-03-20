package test.colorado.capstone

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import test.colorado.capstone.testsupport.testApp
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class IndexTest {
    @Test
    fun testIndex() = testApp {
        val response = client.get("/")
        println("Response Status: ${response.status}")

        assertEquals(HttpStatusCode.OK, response.status)
        assertContains(response.bodyAsText(), "Capstone Starter")
    }
}