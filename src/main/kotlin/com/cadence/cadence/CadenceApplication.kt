package com.cadence.cadence

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CadenceApplication

fun main(args: Array<String>) {
	runApplication<CadenceApplication>(*args)
}
