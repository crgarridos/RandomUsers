package com.crgarridos.randomusers.test.utils.fixtures

import com.crgarridos.randomusers.domain.model.User
import java.util.UUID
import kotlin.random.Random

object UserFixtures {

    val sampleTitles = listOf("Mr", "Ms", "Mrs", "Dr", "Prof")
    val sampleNationalities = listOf("US", "CA", "GB", "AU", "DE", "FR", null)

    fun generateRandomUser(): User {
        val randomFirstName = "FirstName" + Random.nextInt(1000, 9999)
        val randomLastName = "LastName" + Random.nextInt(1000, 9999)
        val randomEmailDomain = listOf("example.com", "test.org", "sample.net").random()

        return User(
            title = sampleTitles.random(),
            firstName = randomFirstName,
            lastName = randomLastName,
            email = "$randomFirstName.$randomLastName@$randomEmailDomain",
            phone = if (Random.nextBoolean()) "555-${
                Random.nextInt(
                    100,
                    999
                )
            }-${Random.nextInt(1000, 9999)}" else null,
            thumbnailUrl = "https://picsum.photos/seed/${UUID.randomUUID()}/100/100.jpg",
            largePictureUrl = if (Random.nextBoolean()) "https://picsum.photos/seed/${UUID.randomUUID()}/600/600.jpg" else null,
            nationality = sampleNationalities.random()
        )
    }

    val testUser1 = User(
        title = "Mr",
        firstName = "John",
        lastName = "Doe",
        email = "john.doe@example.com",
        phone = "555-0101",
        thumbnailUrl = "https://example.com/thumbnails/johndoe.jpg",
        largePictureUrl = "https://example.com/pictures/johndoe.jpg",
        nationality = "US"
    )

    val testUser2 = User(
        title = "Ms",
        firstName = "Jane",
        lastName = "Aria",
        email = "jane.aria@example.com",
        phone = null, // Example of a nullable field being null
        thumbnailUrl = "https://example.com/thumbnails/janearia.jpg",
        largePictureUrl = null, // Example of a nullable field being null
        nationality = "CA"
    )
    val testUsersList = listOf(testUser1, testUser2)


    fun generateRandomUsers(count: Int): List<User> {
        return List(count) {
            generateRandomUser()
        }
    }
}
