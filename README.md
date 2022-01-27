# todoit_app

![CI](https://github.com/AlNat/todoit_app/workflows/CI/badge.svg)

Небольшое REST API backend приложение для списка задач.


## Особенности реализации

* Типы вынесены в отдельную библиотеку

* Постраничный поиск сделан через DeferredResult (парковка потоков), чтобы не занимать поток в HTTP тредпуле

* Версионирование БД проводиться через Flyway

* Подключен Sleuth для проброса trace-id сквозь запросы и логи (B3-spec)

* Документация через Swagger (см `/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`)

* Actuator вынесен в отдельный порт для удобных live\readу чеков при высокой нагрузке на сервис

* Сервис полностью Stateless, горизонтально масштабируется без ограничений


## Конфигурация приложения

| Группа     | Параметр                      | Описание                                          | Значение по умолчанию                   |
|------------|-------------------------------|---------------------------------------------------|-----------------------------------------|
| JVM        | SERVER_PORT                   | порт приложения                                   | 80                                      |
| JVM        | MANAGEMENT_SERVER_PORT        | порт актуатора приложения                         | 88                                      |
| JVM        | JVM_GC                        | тип GC                                            | "-XX:+UseG1GC",                         |
| JVM        | HTTP_MAX_THREADS              | максимальное тредпула для обработки REST запросов | 100                                     |
| JVM        | HTTP_MAX_QUEUE                | размер очереди для обработки REST запросов        | 300                                     |
| Datasource | DB_URL                        | адрес подключения к БД задач                      | jdbc:postgresql://localhost:5432/todoit |
| Datasource | DB_USER                       | логин к БД                                        | postgres                                |
| Datasource | DB_PASSWORD                   | пароль к БД                                       | postgres                                |
| Datasource | DB_HIKARI_MIN_IDLE            |                                                   |                                         |
| Datasource | DB_HIKARI_MAX_POOL_SIZE       |                                                   |                                         |
| Datasource | DB_HIKARI_AUTO_COMMIT         |                                                   |                                         |
| Datasource | DB_HIKARI_IDLE_TIMEOUT        |                                                   |                                         |
| Swagger    | SWAGGER_OPERATIONS            | список включенных операций в SwaggerUI            | "get", "post"                           |
| Swagger    | ENABLE_SWAGGER                | флаг включения swagger                            | true                                    |
| ThreadPool | EXECUTION_POOL_ALLOW_TIMEOUT  |                                                   | true                                    |
| ThreadPool | EXECUTION_POOL_CORE_SIZE      | размер тредпула по-умолчанию                      | 100                                     |
| ThreadPool | EXECUTION_POOL_MAX_SIZE       | максимальный размер тредпула                      | 500                                     |
| ThreadPool | EXECUTION_POOL_KEEP_ALIVE     | время жизни пула до уменьшения                    | 60s                                     |
| ThreadPool | EXECUTION_POOL_QUEUE_CAPACITY | размер очереди перед пулом                        | 500                                     |
| other      | ATTACHMENT_MAX_SIZE           | максимальный размер загружаемого файла            | 1MB                                     |
