package com.ndgndg91.executionaggregator.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig.*
import org.apache.kafka.streams.kstream.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration
import org.springframework.kafka.config.KafkaStreamsConfiguration
import java.time.Duration


@Configuration
@EnableKafka
@EnableKafkaStreams
class KafkaStreamConfig {

    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String? = null

    private val logger = LoggerFactory.getLogger(KafkaStreamConfig::class.java)
    private val om = jacksonObjectMapper()

    @Bean(name = [KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME])
    fun kStreamsConfig(): KafkaStreamsConfiguration {
        val props: MutableMap<String, Any?> = HashMap()
        props[APPLICATION_ID_CONFIG] = "execution-aggregator"
        props[BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress
        props[DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name
        props[DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass.name

        return KafkaStreamsConfiguration(props)
    }

    @Bean
    fun processStream(streamsBuilder: StreamsBuilder): KStream<String, String> {
        val stream =
            streamsBuilder.stream("mysql.my_sandbox.execution_history", Consumed.with(Serdes.String(), Serdes.String()))

        val groupedStream: KGroupedStream<String, String> = stream
            .mapValues { value -> parseJson(value) }  // JSON 문자열을 파싱
            .filter { _, value -> value != null }  // null 값 제거
            .map { _, value -> KeyValue("${value!!.userId}_${value.symbol}", value.amount.toString()) }
            .groupByKey()  // user_id와 symbol 로 그룹화

        val windowedStream = groupedStream.windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(1)))

        // 그룹화된 스트림에 대해 합계 계산
        val resultStream = windowedStream.reduce(
            { aggValue: String, newValue: String -> (aggValue.toLong() + newValue.toLong()).toString() },
            Materialized.`as`("aggregated.1min.execution_history")
        ).toStream()
            .map { windowedKey, value ->
                val key = windowedKey.key()  // Windowed<Pair<Long, String>>에서 Pair<Long, String>를 추출
                val start = windowedKey.window().start()
                val end = windowedKey.window().end()
                KeyValue("${key}_${start}_$end", value)  // 새로운 키 포맷팅
            }
        resultStream.to("aggregated.1min.execution_history", Produced.with(Serdes.String(), Serdes.String()))

        return resultStream
    }

    private fun parseJson(json: String): ExecutionHistory? {
        return try {
            om.readValue<ExecutionHistory>(json)
        } catch (e: Exception) {
            logger.error("failed parsing : {}", json, e)
            null  // 파싱 실패 시 null 반환
        }
    }
}