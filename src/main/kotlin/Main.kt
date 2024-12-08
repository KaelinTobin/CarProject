package ie.setu

import mu.KotlinLogging

val carAPI = CarAPI()
val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Launching Car App" }
    start()
}

fun menu(): Int {
    print(
        """
         |Car Menu
         |   1. Add Car
         |   2. List All Cars
         |   3. Search Cars
         |   4. Search Cars to Buy
         |   5. Search Cars by County (Registration)
         |  -1. Exit
         |
         |Enter Option: """.trimMargin()
    )
    return readLine()?.toIntOrNull() ?: -1
}

fun start() {
    var input: Int
    do {
        input = menu()
        when (input) {
            1 -> add()
            2 -> list()
            3 -> search()
            4 -> searchToBuy()
            5 -> searchByCounty()
            -1 -> println("Exiting App")
            else -> println("Invalid Option")
        }
        println()
    } while (input != -1)
}

fun list() {
    logger.info { "Listing Cars" }
    carAPI.findAll()
        .forEach { println(it) }
}

fun search() {
    logger.info { "Searching Cars" }

    println(
        """
        |Search Options:
        |   1. Search by ID
        |   2. Search by Make
        |   3. Search by Model
        |Enter Option: 
        """.trimMargin()
    )
    val option = readLine()?.toIntOrNull() ?: -1

    when (option) {
        1 -> {
            val car = getCarById()
            if (car == null) {
                println("No car found with the given ID.")
            } else {
                println(car)
            }
        }
        2 -> {
            print("Enter the make to search by: ")
            val make = readLine().orEmpty()
            val results = carAPI.findByMake(make)
            if (results.isEmpty()) {
                println("No cars found with the given make.")
            } else {
                results.forEach { println(it) }
            }
        }
        3 -> {
            print("Enter the model to search by: ")
            val model = readLine().orEmpty()
            val results = carAPI.findByModel(model)
            if (results.isEmpty()) {
                println("No cars found with the given model.")
            } else {
                results.forEach { println(it) }
            }
        }
        else -> println("Invalid option.")
    }
}

fun searchToBuy() {
    logger.info { "Searching Cars to Buy" }

    print("Enter the maximum price: ")
    val maxPrice = readLine()?.toDoubleOrNull() ?: -1.0
    print("Enter the car type (e.g., sedan, coupe, hatchback): ")
    val carType = readLine().orEmpty()

    val results = carAPI.findByPriceAndType(maxPrice, carType)
    if (results.isEmpty()) {
        println("No cars found matching the price.")
    } else {
        println("Cars available for purchase:")
        results.forEach { println(it) }
    }
}

fun searchByCounty() {
    logger.info { "Searching Cars by County (Registration)" }

    print("Enter the county code (e.g., C for Cork, D for Dublin, WX for Wexfodr): ")
    val countyCode = readLine()?.uppercase() ?: ""

    if (countyCode in listOf("C", "D", "W", "WX", "KK")) {
        val results = carAPI.findByCountyCode(countyCode)
        if (results.isEmpty()) {
            println("No cars found for the county.")
        } else {
            println("Cars registered in the county:")
            results.forEach { println(it) }
        }
    } else {
        println("Invalid county code. Please enter one of the following: C (Cork), D (Dublin), W (Waterford), WX (Wexford), KK (Kilkenny).")
    }
}

fun getCarById(): Car? {
    print("Enter the Car id to search by: ")
    val carID = readLine()?.toIntOrNull() ?: return null
    return carAPI.findOne(carID)
}

fun add() {
    logger.info { "Adding a Car" }
    print("Enter Make: ")
    val make = readLine().orEmpty()
    print("Enter Model: ")
    val model = readLine().orEmpty()
    print("Enter Car Type (coupe/sedan/hatchback/etc): ")
    val carType = readLine().orEmpty()
    print("Enter Car Price: ")
    val carPrice = readLine()?.toDoubleOrNull() ?: 0.0
    print("Enter Car Registration (e.g., 202-C-12345 , please include - to seperate the reg: ")
    val registration = readLine().orEmpty()
    print("Enter Car Year: ")
    val year = readLine()?.toIntOrNull() ?: 0

    carAPI.create(Car(make, model, carType, 0, carPrice, registration, year))
    println("Car added successfully.")
}

class CarAPI {
    private val cars = mutableListOf<Car>()
    private var idCounter = 0

    fun create(car: Car) {
        car.id = ++idCounter
        cars.add(car)
    }

    fun findAll(): List<Car> = cars

    fun findOne(id: Int): Car? = cars.find { it.id == id }

    fun findByMake(make: String): List<Car> = cars.filter { it.make.equals(make, ignoreCase = true) }

    fun findByModel(model: String): List<Car> = cars.filter { it.model.equals(model, ignoreCase = true) }

    fun findByPriceAndType(maxPrice: Double, carType: String): List<Car> {
        return cars.filter { it.price <= maxPrice && it.carType.equals(carType, ignoreCase = true) }
    }

    fun findByCountyCode(countyCode: String): List<Car> {
        return cars.filter { car ->
            val county = car.registration.split("-").getOrNull(1)?.uppercase() ?: ""
            county == countyCode
        }
    }
}

data class Car(
    var make: String,
    var model: String,
    var carType: String,
    var id: Int,
    var price: Double,
    var registration: String,
    var year: Int
)
