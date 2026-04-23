# Shipping Service (GHN)

Spring Boot shipping service integrated with GHN sandbox API.

## Requirements

- Java 17+
- PostgreSQL (database: `shipping_db`, port `5439`)

## Configuration

Main config is in `src/main/resources/application.yaml`.

Set environment variables (recommended):

- `GHN_TOKEN`
- `GHN_SHOP_ID`
- `GHN_FROM_DISTRICT_ID` (optional, default `1542`)

## Run

```bash
cd /home/nguyendat/workspace/SHIPPING-SERVICE-ECOMMERCE
JAVA_BIN="$(readlink -f "$(command -v java)")"
export JAVA_HOME="$(dirname "$(dirname "$JAVA_BIN")")"
./mvnw spring-boot:run
```

## Test

```bash
cd /home/nguyendat/workspace/SHIPPING-SERVICE-ECOMMERCE
JAVA_BIN="$(readlink -f "$(command -v java)")"
export JAVA_HOME="$(dirname "$(dirname "$JAVA_BIN")")"
./mvnw -q test
```

## Main APIs

- `POST /api/shipping/calculate-fee`
- `POST /api/shipping/create`
- `GET /api/shipping/track/{trackingNumber}`
- `GET /api/shipping/order/{orderId}`

