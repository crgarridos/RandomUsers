package com.crgarridos.randomusers.fixtures

import com.crgarridos.randomusers.domain.model.User
import com.crgarridos.randomusers.domain.model.UserLocation
import kotlin.random.Random

object UserFixtures { // TODO this class is duplicated, implement testFixtures instead

    private val titles = listOf("Mr", "Ms", "Mrs", "Dr", "Prof")
    private val firstNames = listOf(
        "John", "Jane", "Alex", "Emily", "Chris", "Katie", "Michael", "Sarah", "David", "Laura"
    )
    private val lastNames = listOf(
        "Smith", "Jones", "Williams", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson"
    )
    private val streetNameSuffixes = listOf("St", "Ave", "Rd", "Ln", "Blvd", "Dr", "Ct", "Pl")
    private val streetNamePrefixes = listOf(
        "Main", "High", "Oak", "Park", "Elm", "Pine", "Maple", "Cedar", "Washington", "Lake"
    )
    private val cities = listOf(
        "Springfield", "Riverside", "Fairview", "Clinton", "Greenville", "Madison", "Georgetown", "Salem", "Franklin", "Marion"
    )
    private val states = listOf(
        "California", "Texas", "Florida", "New York", "Pennsylvania", "Illinois", "Ohio", "Georgia", "North Carolina", "Michigan"
    )
    private val countries = listOf( // Country name and its common 2-letter code for nationality
        "United States" to "US",
        "Canada" to "CA",
        "United Kingdom" to "GB",
        "Australia" to "AU",
        "Germany" to "DE",
        "France" to "FR",
        "Brazil" to "BR",
        "India" to "IN",
        "Japan" to "JP",
        "Mexico" to "MX"
    )

    fun generateRandomUser(): User {
        val selectedTitle = titles.random()
        val selectedFirstName = firstNames.random()
        val selectedLastName = lastNames.random()
        val (countryName, nationalityCode) = randomCountryAndNationality()

        val genderForPhoto = when (selectedTitle) {
            "Mr", "Prof" -> "men" // Assuming Prof can be either, defaulting to men for simplicity
            else -> "women"
        }

        return User(
            title = selectedTitle,
            firstName = selectedFirstName,
            lastName = selectedLastName,
            email = "${selectedFirstName.lowercase()}.${selectedLastName.lowercase()}${Random.nextInt(1,99)}@example.com",
            phone = "(${Random.nextInt(100, 999)}) ${Random.nextInt(100, 999)}-${Random.nextInt(1000, 9999)}",
            thumbnailUrl = "https://randomuser.me/api/portraits/thumb/${genderForPhoto}/${Random.nextInt(0, 99)}.jpg",
            largePictureUrl = "https://randomuser.me/api/portraits/${genderForPhoto}/${Random.nextInt(0, 99)}.jpg",
            nationality = nationalityCode,
            location = randomUserLocation()
        )
    }

    private fun randomCountryAndNationality(): Pair<String, String> {
        return countries.random()
    }

    private fun randomStreetName(): String {
        return "${streetNamePrefixes.random()} ${streetNameSuffixes.random()}"
    }

    private fun randomUserLocation(): UserLocation {
        val (countryName, _) = randomCountryAndNationality()
        return UserLocation(
            streetNumber = Random.nextInt(1, 2000).toString(),
            streetName = randomStreetName(),
            city = cities.random(),
            state = states.random(),
            country = countryName,
            postcode = Random.nextInt(10000, 99999).toString()
        )
    }


    fun generateRandomUsers(count: Int): List<User> {
        return List(count) {
            generateRandomUser()
        }
    }
}
