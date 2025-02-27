fun main() {

    var carParking = Parking(0)

    while (true) {
        val command = readln().split(" ")
        println(
            when (command.first()) {
                "create" -> {
                    carParking = Parking(command[1].toInt())
                    "Created a parking lot with ${command[1]} spots."
                }

                "park" -> carParking.parkACar(command[1], command[2])
                "status" -> carParking.checkStatus()
                "leave" -> carParking.leaveASpot(command[1].toInt())
                "spot_by_color" -> carParking.spotByColor(command[1])
                "reg_by_color" -> carParking.regByColor(command[1])
                "spot_by_reg" -> carParking.spotByReg(command[1])
                "exit" -> return
                else -> "Unknown command."
            }
        )
    }
}

data class Car(var regNumber: String, var color: String)

class Parking(size: Int) {

    private val parkingLots = MutableList<Car?>(size) { null }

    fun parkACar(regNumber: String, color: String): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."
        val freeSpot = parkingLots.indexOfFirst { it == null }
        if (freeSpot != -1) {
            parkingLots[freeSpot] = Car(regNumber, color)
            return "$color car parked in spot ${freeSpot + 1}."
        } else return "Sorry, the parking lot is full."
    }

    fun leaveASpot(lot: Int): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."

        parkingLots.indices.find { it + 1 == lot }.let {
            if (it != null) {
                parkingLots[it] = null
                return "Spot ${it + 1} is free."
            } else {
                return "Couldn't find that parking lot."
            }
        }
    }

    fun checkStatus(): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."
        return parkingLots.mapIndexedNotNull { i, car ->
            car?.let { "${i + 1} ${it.regNumber} ${it.color}" }
        }.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: "Parking lot is empty."
    }

    fun spotByColor(color: String): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."
        return parkingLots.filter { it?.color.equals(color, ignoreCase = true) }.joinToString(", ") {
            parkingLots.indexOf(it).plus(1).toString()
        }.ifEmpty { "No cars with color $color were found." }

    }

    fun regByColor(color: String): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."
        return parkingLots.filter { it?.color.equals(color, ignoreCase = true) }.joinToString(", ") {
            it!!.regNumber
        }.ifEmpty { "No cars with color $color were found." }
    }

    fun spotByReg(regNumber: String): String {
        if (parkingLots.size == 0) return "Sorry, a parking lot has not been created."
        return parkingLots.filter { it?.regNumber == regNumber }.joinToString {
            parkingLots.indexOf(it).plus(1).toString()
        }.ifEmpty { "No cars with registration number $regNumber were found." }
    }

}