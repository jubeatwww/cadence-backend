package com.cadence.cadence

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<CadenceApplication>().with(TestcontainersConfiguration::class).run(*args)
}
