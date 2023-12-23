# Configuration package
This package is aimed at providing global configuration constants for all other classes and packages.

## ConfigurationProvider.java
Public singleton class containing all the constants initialized at class-load time. The constructor reads the `{classpath}/resources/Application.properties` file to override the default values.

- If no override value is found for a particular constant, the default will be used.
- Unknown properties will be silently ignored.

### Property list
|Property                          |Min   |Max              |Default                                    |Unit        |
|----------------------------------|------|-----------------|-------------------------------------------|------------|
|FLUSH_AFTER_WRITE                 |-     |-                |true                                       |-           |
|LOG_QUEUE_INSERT_TIMEOUT          |0     |Integer.MAX_VALUE|500                                        |milliseconds|
|LOG_QUEUE_POLL_TIMEOUT            |0     |Integer.MAX_VALUE|500                                        |milliseconds|
|MAX_LOG_QUEUE_INSERT_ATTEMPTS     |0     |Integer.MAX_VALUE|10                                         |-           |
|MAX_LOG_QUEUE_POLL_ATTEMPTS       |0     |Integer.MAX_VALUE|10                                         |-           |
|MAX_LOG_QUEUE_SIZE                |100   |Integer.MAX_VALUE|100000                                     |elements    |
|READER_WRITER_BUFFER_SIZE         |256   |Integer.MAX_VALUE|65536                                      |bytes       |
|METRICS_TASK_SCHEDULING_NUM_CHUNKS|100   |Integer.MAX_VALUE|600                                        |-           |
|METRICS_TASK_SCHEDULING_CHUNK_SIZE|100   |Integer.MAX_VALUE|500                                        |milliseconds|
|CLIENT_SOCKET_TIMEOUT             |1000  |Integer.MAX_VALUE|10000                                      |milliseconds|
|MAX_CLIENTS_PER_IP                |1     |Integer.MAX_VALUE|2                                          |-           |
|MAX_INCOMING_CONNECTIONS          |1     |Integer.MAX_VALUE|50                                         |-           |
|MAX_REQUEST_SIZE                  |100000|Integer.MAX_VALUE|1000000                                    |bytes       |
|MAX_SOCKETS                       |10    |Integer.MAX_VALUE|10000                                      |-           |
|SERVER_PORT                       |0     |65535            |8080                                       |-           |
|SERVER_SOCKET_TIMEOUT             |100   |Integer.MAX_VALUE|500                                        |milliseconds|
|MAX_USER_SESSIONS                 |1     |Integer.MAX_VALUE|2                                          |-           |
|SESSION_DURATION                  |900000|Integer.MAX_VALUE|3600000                                    |milliseconds|
|DATABASE_ADDRESS                  |-     |-                |jdbc:postgresql://127.0.0.1:5432/example_db|-           |
|DATABASE_CONNECTION_CHECK_TIMEOUT |1     |Integer.MAX_VALUE|5                                          |s           |
|DATABASE_USERNAME                 |-     |-                |admin                                      |-           |
|DATABASE_PASSWORD                 |-     |-                |admin                                      |-           |
|GENERATE_DATABASE_SCHEMA          |-     |-                |false                                      |-           |
|INITIALIZE_DATABASE_DATA          |-     |-                |false                                      |-           |
|MAX_NUM_CACHEABLE_STATEMENTS      |1     |Integer.MAX_VALUE|250                                        |-           |
|MAX_POOL_POLL_ATTEMPTS            |0     |Integer.MAX_VALUE|10                                         |-           |
|MAX_STATEMENTS_CACHE_ENTRY_SIZE   |1     |Integer.MAX_VALUE|5                                          |mebibytes   |
|NUM_DATABASE_CONNECTIONS          |1     |Integer.MAX_VALUE|10                                         |-           |
|NUM_DATABASE_CONNECTIONS_PER_POOL |1     |Integer.MAX_VALUE|10                                         |-           |
|POOL_SHUTDOWN_SLEEP_CHUNK_SIZE    |100   |Integer.MAX_VALUE|500                                        |milliseconds|
|PREPARE_THRESHOLD                 |1     |Integer.MAX_VALUE|5                                          |-           |
|CACHE_CAPACITY                    |0     |Integer.MAX_VALUE|1073741824                                 |bytes       |
|CACHE_ENTRY_DURATION              |10000 |Integer.MAX_VALUE|60000                                      |milliseconds|
|TASK_MANAGER_SLEEP_CHUNK_SIZE     |100   |Integer.MAX_VALUE|500                                        |milliseconds|
