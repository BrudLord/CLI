# Сборка и запуск
Для сборки понадобиться `maven` (протестировано на версии `3.9.9`), а для запуска - `Java Runtime` версии `21` и выше.

## Сборка jar-архива
```bash
mvn clean package -DskipTests
```

## Запуск
```bash
java -jar target/cli.jar
```

## Запуск тестов
```bash
mvn clean test
```