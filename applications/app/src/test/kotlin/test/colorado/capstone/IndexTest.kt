// package test.colorado.capstone

// import io.ktor.client.request.*
// import io.ktor.client.statement.*
// import io.ktor.http.*
// import test.colorado.capstone.testsupport.testApp
// import kotlin.test.Test
// import kotlin.test.assertContains
// import kotlin.test.assertEquals

// class IndexTest {
//     @Test
//     fun testIndex() = testApp {
//         client.post("login", )
//         //TODO rewrite this test to login before trying to access "/"
// //        val response = client.get("/")
// //        println("Response Status: ${response.status}")
// //
// //        assertEquals(HttpStatusCode.OK, response.status)
// //        assertContains(response.bodyAsText(), "Capstone Starter")
//     }

//     @Test
//     fun testRegister() = testApp {
//         val response = client.get("/register")
//         println("Response Status: ${response.status}")

//         assertEquals(HttpStatusCode.OK, response.status)
//     }

//     @Test
//     fun testLogin() = testApp {
//         val response = client.get("/login")
//         println("Response Status: ${response.status}")

//         assertEquals(HttpStatusCode.OK, response.status)
//     }
// }
