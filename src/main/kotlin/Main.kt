package ie.setu

import mu.KotlinLogging

val carAPI = CarAPI()
val logger = KotlinLogging.logger {}

fun main() {
    logger.info { "Launching Car App" }
    addCarData()
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
         |   6. Update Car Details
         |   7. Remove Car
         |  -1. Exit
         |
         |Enter Option: """.trimMargin()
    )
    return readlnOrNull()?.toIntOrNull() ?: -1
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
            6 -> update()
            7 -> remove()
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
    val option = readlnOrNull()?.toIntOrNull() ?: -1

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
            val make = readlnOrNull().orEmpty()
            val results = carAPI.findByMake(make)
            if (results.isEmpty()) {
                println("No cars found with the given make.")
            } else {
                results.forEach { println(it) }
            }
        }
        3 -> {
            print("Enter the model to search by: ")
            val model = readlnOrNull().orEmpty()
            val results = carAPI.findByModel(model)
            if (results.isEmpty()) {
                println("No cars found with the searched model.")
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
    val maxPrice = readlnOrNull()?.toDoubleOrNull() ?: -1.0
    print("Enter the car type (e.g., sedan, coupe, hatchback): ")
    val carType = readlnOrNull().orEmpty()

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

    print("Enter the county code (e.g., C for Cork, D for Dublin, WX for Wexford): ")
    val countyCode = readlnOrNull()?.uppercase() ?: ""

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
    val carID = readlnOrNull()?.toIntOrNull() ?: return null
    return carAPI.findOne(carID)
}

fun add() {
    logger.info { "Adding a Car" }
    print("Enter Make: ")
    val make = readlnOrNull().orEmpty()
    print("Enter Model: ")
    val model = readlnOrNull().orEmpty()
    print("Enter Car Type (coupe/sedan/hatchback/etc): ")
    val carType = readlnOrNull().orEmpty()
    print("Enter Car Price: ")
    val carPrice = readlnOrNull()?.toDoubleOrNull() ?: 0.0
    print("Enter Car Registration (e.g., 202-C-12345 , please include - to seperate the reg: ")
    val registration = readlnOrNull().orEmpty()
    print("Enter Car Year: ")
    val year = readlnOrNull()?.toIntOrNull() ?: 0

    carAPI.create(Car(make, model, carType, 0, carPrice, registration, year))
    println("Car added successfully.")
}

fun remove() {
    logger.info { "Removing a Car" }

    print("Enter the Car ID to remove: ")
    val carID = readlnOrNull()?.toIntOrNull()
    if (carAPI.delete(carID ?: -1)) {
        println("Car removed successfully.")
    } else {
        println("No car found with the given ID.")
    }
}

fun update() {
    logger.info { "Updating Car Details" }

    print("Enter the Car ID to update: ")
    val carID = readlnOrNull()?.toIntOrNull()
    val car = carAPI.findOne(carID ?: -1)

    if (car == null) {
        println("No car found with the given ID.")
        return
    }

    var option: Int
    do {
        println(
            """
            |Update Options:
            |   1. Update Make
            |   2. Update Model
            |   3. Update Car Type
            |   4. Update Price
            |   5. Update Registration
            |   6. Update Year
            |  -1. Exit Update
            |
            |Enter Option: 
            """.trimMargin()
        )
        option = readlnOrNull()?.toIntOrNull() ?: -1
        when (option) {
            1 -> {
                print("Enter new Make: ")
                car.make = readlnOrNull().orEmpty()
            }
            2 -> {
                print("Enter new Model: ")
                car.model = readlnOrNull().orEmpty()
            }
            3 -> {
                print("Enter new Car Type: ")
                car.carType = readlnOrNull().orEmpty()
            }
            4 -> {
                print("Enter new Price: ")
                car.price = readlnOrNull()?.toDoubleOrNull() ?: car.price
            }
            5 -> {
                print("Enter new Registration: ")
                car.registration = readlnOrNull().orEmpty()
            }
            6 -> {
                print("Enter new Year: ")
                car.year = readlnOrNull()?.toIntOrNull() ?: car.year
            }
            -1 -> println("Exiting Update Menu.")
            else -> println("Invalid option.")
        }
    } while (option != -1)

    println("Updated Car Details: $car")
}

fun addCarData() {
    val cars = listOf(
        Car("Toyota", "Corolla", "Sedan", 0, 15000.00, "221-D-12345", 2021),
        Car("Ford", "Focus", "Hatchback", 1, 16000.00, "219-W-67890", 2019),
        Car("BMW", "320i", "Sedan", 2, 35000.00, "202-C-11223", 2020),
        Car("Audi", "A4", "Sedan", 3, 50000.00, "201-WX-234", 2020)
    )

    cars.forEach { carAPI.create(it) }
    println("Car data added.")
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

    fun delete(id: Int): Boolean = cars.removeIf { it.id == id }
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
