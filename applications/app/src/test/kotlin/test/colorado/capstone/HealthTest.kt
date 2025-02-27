package test.colorado.capstone

import io.ktor.client.request.*
import io.ktor.http.*
import test.colorado.capstone.testsupport.testApp
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {
    @Test
    fun testHealth() = testApp {
        val response = client.get("/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
