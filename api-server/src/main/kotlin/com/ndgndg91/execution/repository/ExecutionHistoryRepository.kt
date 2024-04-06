package com.ndgndg91.execution.repository

import org.springframework.data.jpa.repository.JpaRepository

interface ExecutionHistoryRepository: JpaRepository<ExecutionHistory, Long>