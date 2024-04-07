```shell
$ sudo nano /etc/hosts

# 아래 내용 추가
127.0.0.1 kafka-broker1
127.0.0.1 kafka-broker2
127.0.0.1 kafka-broker3
127.0.0.1 kafka-connect
```

1. docker compose up
2. kafka topic 생성
3. kafka connect 설정
4. kafka connect 상태 확인
5. 타겟 테이블 쓰기 작업 진행
```bash
$ sh run.sh
$ sh create-topic.sh
$ sh config.sh
$ sh check.sh
```