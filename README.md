# CDC Order Demo - Debezium + Spring Boot + Java

Change Data Capture (CDC)를 활용한 실시간 이벤트 기반 마이크로서비스 데모 프로젝트 (Java 버전)

## 📋 목차

- [프로젝트 개요](#프로젝트-개요)
- [아키텍처](#아키텍처)
- [기술 스택](#기술-스택)
- [시작하기](#시작하기)
- [CDC 동작 원리](#cdc-동작-원리)
- [트러블슈팅](#트러블슈팅)
- [CDC 장단점](#cdc-장단점)

## 🎯 프로젝트 개요

이 프로젝트는 Debezium을 사용한 Change Data Capture (CDC) 패턴을 구현합니다.

### 주요 특징

- **Order Service**: 주문 생성 및 상태 관리 (Spring Boot + Java + Lombok)
- **Debezium**: MySQL binlog를 모니터링하여 변경사항을 Kafka로 발행
- **Notification Service**: CDC 이벤트를 소비하여 알림 발송
- **Zero Code Change**: 애플리케이션 코드 변경 없이 이벤트 발행

### 해결하는 문제

**Dual Write Problem**: DB 저장과 메시지 발행이 원자적이지 않은 문제

```java
❌ 기존 방식의 문제:
@Transactional
public void createOrder(CreateOrderRequest request) {
    // 1. DB에 주문 저장 ✅
    orderRepository.save(order);
    
    // 2. Kafka로 이벤트 발행 ❌ (실패 가능)
    kafkaTemplate.send("orders", orderEvent);
    
    → 데이터 정합성 깨짐
}

✅ CDC 방식:
@Transactional
public OrderResponse createOrder(CreateOrderRequest request) {
    // DB에 주문 저장만!
    Order savedOrder = orderRepository.save(order);
    
    // Debezium이 자동으로:
    // 1. binlog 읽기
    // 2. Kafka 발행
    // 3. 데이터 정합성 보장
    
    return toResponse(savedOrder);
}
```

## 🏗️ 아키텍처

```
┌─────────────────┐
│  Order Service  │
│ (Spring Boot)   │
└────────┬────────┘
         │ INSERT/UPDATE
         ▼
┌─────────────────┐
│   MySQL DB      │
│   (binlog ON)   │
└────────┬────────┘
         │ Reads binlog
         ▼
┌─────────────────┐
│   Debezium      │
│ (Kafka Connect) │
└────────┬────────┘
         │ Publishes
         ▼
┌─────────────────┐
│   Kafka Topic   │
│ (order changes) │
└────────┬────────┘
         │ Consumes
         ▼
┌─────────────────┐
│ Notification    │
│    Service      │
└─────────────────┘
```

## 🛠️ 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.2.0
- **라이브러리**: Lombok
- **데이터베이스**: MySQL 8.0
- **CDC 도구**: Debezium 2.4
- **메시지 브로커**: Apache Kafka 7.5
- **인프라**: Docker Compose

## 🚀 시작하기

### 사전 요구사항

- Docker & Docker Compose
- JDK 17+
- Gradle
- curl & jq (테스트용)

### 1. 인프라 시작

```bash
# 실행 권한 부여
chmod +x setup.sh test-api.sh

# Docker 컨테이너 시작 및 Debezium 설정
./setup.sh
```

### 2. 애플리케이션 시작

**Terminal 1 - Order Service:**
```bash
cd order-service
./gradlew bootRun
```

**Terminal 2 - Notification Service:**
```bash
cd notification-service
./gradlew bootRun
```

### 3. 테스트 실행

```bash
./test-api.sh
```

### 4. 모니터링

- **Kafka UI**: http://localhost:8081
- **Kafka Connect**: http://localhost:8083/connectors
- **Order Service**: http://localhost:8081/api/orders

