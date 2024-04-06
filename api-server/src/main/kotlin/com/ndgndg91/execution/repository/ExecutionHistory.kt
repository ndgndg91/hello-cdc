package com.ndgndg91.execution.repository

import jakarta.persistence.*
import java.sql.Timestamp

@Entity
@Table(
    name = "execution_history", indexes = [
        Index(name = "index_timestamp", columnList = "timestamp")
    ]
)
class ExecutionHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    private var userId: Long,

    @Column(name = "symbol", nullable = false, length = 100)
    private var symbol: String,

    @Column(name = "amount", nullable = false)
    private var amount: Long,

    @Column(name = "timestamp", nullable = false)
    private var timestamp: Timestamp = Timestamp(System.currentTimeMillis())
)