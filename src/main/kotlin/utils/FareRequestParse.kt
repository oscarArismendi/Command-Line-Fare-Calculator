package org.fare.calculator.utils

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.fare.calculator.domain.dtos.FareRequest
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun parseInput(args: Array<String>): FareRequest {
    val parser = ArgParser("fare-calculator")
    println(args.contentToString())
    val from by parser.option(
        ArgType.String,
        shortName = "f",
        fullName = "from",
        description = "Origin station",
    ).required()

    val to by parser.option(
        ArgType.String,
        shortName = "t",
        fullName = "to",
        description = "Destination station",
    ).required()

    val type by parser.option(
        ArgType.String,
        fullName = "type",
        description = "Rider type (Adult, Child, Senior)",
    ).required()

    val time by parser.option(
        ArgType.String,
        fullName = "time",
        description = "Time of travel (optional)",
    )

    try {
        parser.parse(args)
        var localTime = LocalTime.now()
        if (time != null) {
            localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"))
        }

        return FareRequest(
            origin = from,
            destination = to,
            riderType = type,
            timeStamp = localTime,
        )
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("Invalid time format. Use HH:mm format.")
    } catch (e: Exception) {
        val usage = """
            Usage: fare-calculator --from <origin> --to <destination> --type <rider_type> [--time <time>]
            
            Required arguments:
              --from, -f    Origin station
              --to, -t      Destination station  
              --type        Rider type (Adult, Child, Senior)
              
            Optional arguments:
              --time        Time of travel
              
            Example: ./gradlew run --args="--from A --to B --type Adult --time 10:30"
        """.trimIndent()

        throw IllegalArgumentException("$usage\n\nError: ${e.message}")
    }
}
