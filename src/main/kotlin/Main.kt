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
            print("Enter the car ID to search by: ")
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
                println("No cars found with the searched model.")
            } else {
                results.forEach { println(it) }
            }
        }
        else -> println("Invalid option.")
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

    carAPI.create(Car(make, model, carType, 0, carPrice))
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
}



data class Car(
    var make: String,
    var model: String,
    var carType: String,
    var id: Int,
    var price: Double
)