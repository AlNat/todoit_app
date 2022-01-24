# todoit_app

![CI](https://github.com/AlNat/todoit_app/workflows/CI/badge.svg)

Небольшое REST API приложение список задач


## Особенности реализации

* Типы вынесены в отдельную библиотеку

* Постраничный поиск сделан через DeferredResult, она же парковка тредов, чтобы не держать общий тредпул

* Сделана миграция БД через Flyway

* Подключен Sleuth для проброса trace-id

* Документация через Swagger (см `/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`)

* Actuator вынесен в отдельный порт для удобных live\readу чеков при высокой нагрузке на сервис

* Сервис полностью Stateless, горизонтально масштабируется


## Конфигурация приложения

| Группа     | Параметр                       | Описание                                                | Значение по умолчанию                   |
|------------|--------------------------------|---------------------------------------------------------|-----------------------------------------|
| JVM        | SERVER_PORT                    | порт приложения                                         | 80                                      |
| JVM        | MANAGEMENT_SERVER_PORT         | порт актуатора приложения                               | 88                                      |
| JVM        | JVM_GC                         | тип GC                                                  | "-XX:+UseG1GC",                         |
| JVM        | HTTP_MAX_THREADS               | максимальное кол-во потоков для обработки REST запросов | 100                                     |
| JVM        | HTTP_MAX_QUEUE                 | размер очереди потоков для обработки REST запросов      | 300                                     |
| Datasource | DB_URL                         | адрес подключения к БД задач                            | jdbc:postgresql://localhost:5432/todoit |
| Datasource | DB_USER                        | логин к БД                                              | postgres                                |
| Datasource | DB_PASSWORD                    | пароль к БД                                             | postgres                                |
| Datasource | DB_HIKARI_MIN_IDLE             |                                                         |                                         |
| Datasource | DB_HIKARI_MAX_POOL_SIZE        |                                                         |                                         |
| Datasource | DB_HIKARI_AUTO_COMMIT          |                                                         |                                         |
| Datasource | DB_HIKARI_IDLE_TIMEOUT         |                                                         |                                         |
| Swagger    | SWAGGER_OPERATIONS             | список включенных операций в SwaggerUI                  | "get", "post"                           |
| Swagger    | ENABLE_SWAGGER                 | флаг включения swagger                                  | true                                    |
| ThreadPool | EXECUTION_POOL_ALLOW_TIMEOUT   |                                                         |                                         |
| ThreadPool | EXECUTION_POOL_CORE_SIZE       |                                                         |                                         |
| ThreadPool | EXECUTION_POOL_MAX_SIZE        |                                                         |                                         |
| ThreadPool | EXECUTION_POOL_KEEP_ALIVE      |                                                         |                                         |
| ThreadPool | EXECUTION_POOL_QUEUE_CAPACITY  |                                                         |                                         |
| ThreadPool | SCHEDULING_POOL_SIZE           |                                                         |                                         |
