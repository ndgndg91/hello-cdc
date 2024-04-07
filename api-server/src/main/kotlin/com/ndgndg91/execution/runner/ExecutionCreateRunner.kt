package com.ndgndg91.execution.runner

import com.ndgndg91.execution.repository.ExecutionHistory
import com.ndgndg91.execution.repository.ExecutionHistoryRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.util.Random
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

@Component
class ExecutionCreateRunner(
    private val repository: ExecutionHistoryRepository
) : ApplicationRunner {
    private val logger = LoggerFactory.getLogger(ExecutionCreateRunner::class.java)
    private val hasStop: AtomicBoolean = AtomicBoolean(true)
    private val symbols = listOf(
        "BTC",
        "BCH",
        "ETH",
        "XRP",
        "SOL",
        "EOS",
        "ADA",
        "AVAX",
        "BTG",
        "ETC",
        "APT",
        "SEI"
    )

    override fun run(args: ApplicationArguments?) {
        logger.info("start runner")
        val startTime = System.currentTimeMillis()
        var insertCount = 0
        while (hasStop.get()) {
            if (insertCount > 1_000_000) {
                hasStop.set(false)
            }
            if (insertCount % 100 == 0) {
                val time = System.currentTimeMillis()
                logger.info("{} rows inserted. {}", insertCount, time - startTime)
            }

            val firstTask = CompletableFuture.runAsync { repository.save(history()) }
            val secondTask = CompletableFuture.runAsync { repository.save(history()) }
            val thirdTask = CompletableFuture.runAsync { repository.save(history()) }
            val fourthTask = CompletableFuture.runAsync { repository.save(history()) }
            val fifthTask = CompletableFuture.runAsync { repository.save(history()) }
            CompletableFuture.allOf(firstTask, secondTask, thirdTask, fourthTask, fifthTask).get()
            insertCount += 5
        }
    }

    private fun history(): ExecutionHistory {
        return ExecutionHistory(
            userId = userId(),
            symbol = symbol(),
            amount = amount()
        )
    }

    private fun userId(): Long {
        return Random().nextLong(1, 1000001)
    }

    private fun symbol(): String {
        return symbols[Random().nextInt(0, symbols.size)]
    }

    private fun amount(): Long {
        return Random().nextLong(100000000, 10000000000000000)
    }
}