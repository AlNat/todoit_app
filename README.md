# todoit_app

![CI](https://github.com/AlNat/todoit_app/workflows/CI/badge.svg)

Небольшое REST API backend приложение для списка задач.


## Особенности реализации

* Типы вынесены в отдельную библиотеку

* Постраничный поиск сделан через DeferredResult (парковка потоков), чтобы не занимать поток в HTTP тредпуле

* Версионирование БД проводиться через Flyway

* Подключен Sleuth для проброса trace-id сквозь запросы и логи (B3-spec)

* Документация API через Swagger (см `/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/`)

* Actuator вынесен в отдельный порт для удобных live\ready чеков при высокой нагрузке на сервис

* Сервис полностью Stateless, горизонтально масштабируется без ограничений

* Тесты написаны с применением test-container для запуска БД -- CI требует Environment который это поддерживает

* Автотесты написаны в кейсовом формате и по-факту представляют собой интеграционные тесты


## Конфигурация приложения

| Группа     | Параметр                      | Описание                               | Значение по умолчанию                                  |
|------------|-------------------------------|----------------------------------------|--------------------------------------------------------|
| JVM        | SERVER_PORT                   | порт приложения                        | 80                                                     |
| JVM        | MANAGEMENT_SERVER_PORT        | порт актуатора приложения              | 88                                                     |
| JVM        | JVM_HEAP                      | параметры по памяти в куче             | -Xms500m -Xmx1G                                        |
| JVM        | JVM_SYSTEM_MEMORY             | параметры по системной памяти          | -XX:MaxMetaspaceSize=500m -XX:MaxDirectMemorySize=500m |
| JVM        | JVM_GC                        | тип GC                                 | -XX:+UseG1GC                                           |
| JVM        | HTTP_MAX_THREADS              | максимальный HTTP пул                  | 100                                                    |
| JVM        | HTTP_MAX_QUEUE                | размер очереди HTTP пула               | 300                                                    |
| Datasource | DB_URL                        | адрес подключения к БД                 | jdbc:postgresql://localhost:5432/todoit                |
| Datasource | DB_USER                       | логин к БД                             | postgres                                               |
| Datasource | DB_PASSWORD                   | пароль к БД                            | postgres                                               |
| Datasource | DB_HIKARI_MIN_IDLE            | минимальный размер БД пула             | 1                                                      |
| Datasource | DB_HIKARI_MAX_POOL_SIZE       | максимальный размер БД пула            | 10                                                     |
| Datasource | DB_HIKARI_AUTO_COMMIT         | включение авто-комита                  | true                                                   |
| Datasource | DB_HIKARI_IDLE_TIMEOUT        | время простоя БД пула до уменьшения    | 30000                                                  |
| Swagger    | SWAGGER_OPERATIONS            | список включенных операций в SwaggerUI | "get", "post"                                          |
| Swagger    | ENABLE_SWAGGER                | флаг включения swagger                 | true                                                   |
| ThreadPool | EXECUTION_POOL_ALLOW_TIMEOUT  |                                        | true                                                   |
| ThreadPool | EXECUTION_POOL_CORE_SIZE      | размер тредпула по-умолчанию           | 100                                                    |
| ThreadPool | EXECUTION_POOL_MAX_SIZE       | максимальный размер тредпула           | 500                                                    |
| ThreadPool | EXECUTION_POOL_KEEP_ALIVE     | время жизни пула до уменьшения         | 60s                                                    |
| ThreadPool | EXECUTION_POOL_QUEUE_CAPACITY | размер очереди перед пулом             | 500                                                    |
| Other      | ATTACHMENT_MAX_SIZE           | максимальный размер загружаемого файла | 1MB                                                    |
