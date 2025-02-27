fun main() {

    val carParking = Parking(0)

    while (true) {
        val input = readln().trim()
        if (input.isEmpty()) continue

        val parts = input.split(" ")
        val command = parts.first()
        val args = parts.drop(1)

        when (command) {
            "exit" -> return
            else -> {
                val validationResult = validateCommand(command, args, carParking)
                if (validationResult != null) {
                    println(validationResult)
                    continue
                }

                val result = executeCommand(command, args, carParking)
                println(result)
            }
        }

    }
}

private fun validateCommand(command: String, args: List<String>, parking: Parking): String? {
    return when (command) {
        "create" -> {
            when {
                args.size != 1 -> "Invalid arguments. Use: create [numbers of spots]"
                args[0].toIntOrNull()
                    ?.takeIf { it > 0 } == null -> "Invalid number of spots. Please enter a positive integer."

                else -> null
            }
        }

        "park" -> {
            when {
                args.size < 2 -> "Invalid arguments. Use: park [registration number] [color]"
                args[0].isEmpty() || args[1].isEmpty() -> "Registration number or color cannot be empty."
                parking.isNotCreated() -> "Sorry, a parking lot has not been created."
                else -> null
            }
        }

        "status" -> {
            if (parking.isNotCreated()) "Sorry, a parking lot has not been created." else null
        }

        "leave" -> {
            when {
                args.size != 1 -> "Invalid arguments. Use: leave [spot number]"
                parking.isNotCreated() -> "Sorry, a parking lot has not been created."
                args[0].toIntOrNull() == null -> "Invalid spot number. Please provide a valid integer."
                else -> null
            }
        }

        "spot_by_color", "reg_by_color", "spot_by_reg" -> {
            when {
                args.size != 1 -> "Invalid arguments. Use: $command [value]"
                parking.isNotCreated() -> "Sorry, a parking lot has not been created."
                else -> null
            }
        }

        else -> "Unknown command."
    }
}

private fun executeCommand(command: String, args: List<String>, parking: Parking): String {
    return when (command) {
        "create" -> {
            val spots = args[0].toInt()
            parking.recreate(spots)
            "Created a parking lot with $spots spots."
        }

        "park" -> parking.parkACar(args[0], args[1])
        "status" -> parking.checkStatus()
        "leave" -> parking.leaveASpot(args[0].toInt())
        "spot_by_color" -> parking.spotByColor(args[0])
        "reg_by_color" -> parking.regByColor(args[0])
        "spot_by_reg" -> parking.spotByReg(args[0])
        else -> throw IllegalArgumentException("Unhandled command: $command")
    }
}

data class Car(var regNumber: String, var color: String)

class Parking(private var size: Int) {

    private var parkingLots = MutableList<Car?>(size) { null }

    fun isNotCreated() = size == 0

    fun recreate(newSize: Int) {
        size = newSize
        parkingLots = MutableList(newSize) { null }
    }

    fun parkACar(regNumber: String, color: String): String {
        val freeSpot = parkingLots.indexOfFirst { it == null }
        if (freeSpot != -1) {
            parkingLots[freeSpot] = Car(regNumber, color)
            return "$color car parked in spot ${freeSpot + 1}."
        } else return "Sorry, the parking lot is full."
    }

    fun leaveASpot(lot: Int): String {
        val index = lot - 1
        return if (index !in parkingLots.indices) "Couldn't find that parking lot."
        else if (parkingLots[index] == null) "Spot $lot is already free."
        else {
            parkingLots[index] = null
            "Spot $lot is free."
        }
    }

    fun checkStatus(): String = parkingLots.mapIndexedNotNull { i, car ->
        car?.let { "${i + 1} ${it.regNumber} ${it.color}" }
    }.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: "Parking lot is empty."

    fun spotByColor(color: String): String =
        parkingLots.filter { it?.color.equals(color, ignoreCase = true) }.joinToString(", ") {
            parkingLots.indexOf(it).plus(1).toString()
        }.ifEmpty { "No cars with color $color were found." }


    fun regByColor(color: String): String =
        parkingLots.filter { it?.color.equals(color, ignoreCase = true) }.joinToString(", ") {
            it!!.regNumber
        }.ifEmpty { "No cars with color $color were found." }

    fun spotByReg(regNumber: String): String = parkingLots.filter { it?.regNumber == regNumber }.joinToString {
        parkingLots.indexOf(it).plus(1).toString()
    }.ifEmpty { "No cars with registration number $regNumber were found." }
}