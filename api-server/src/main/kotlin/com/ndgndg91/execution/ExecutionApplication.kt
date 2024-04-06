package com.ndgndg91.execution

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExecutionApplication

fun main(args: Array<String>) {
    runApplication<ExecutionApplication>(*args)
}
