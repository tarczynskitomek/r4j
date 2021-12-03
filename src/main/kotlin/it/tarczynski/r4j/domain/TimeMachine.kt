package it.tarczynski.r4j.domain

import it.tarczynski.r4j.domain.TimeSettings.zoneId
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId

object TimeSettings {

    val zoneId: ZoneId = ZoneId.of("Europe/Warsaw")
}

class TimeMachine {

    fun now(): Instant = Instant.now()

    fun currentDay(): DayOfWeek = now().atZone(zoneId).dayOfWeek
}
