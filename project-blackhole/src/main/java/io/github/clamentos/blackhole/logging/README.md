# Logging package
This package is aimed at providing global synchronous and asynchronous logging features for all other classes and packages.

## Log.java
Public record class that contains the data that needs to be logged, which includes:

- `message`: The actual message string.
- `log_level`: The log severity enumeration.
- `timestamp`: The timestamp at the time of creation.
- `id`: The unique log id.

## Logger.java
Public singleton class that logs messages asynchronously. This class is responsible for inserting the `Log` objects into the log queue, as well as starting the `LogTask` threads.

`Logger` exposes the `log(String message, LogLevel severity)` method which is what is used to create the `Log` objects as well as inserting them into the queue.

## LogLevel.java
Public enumeration that contains all the possible severity values of a `Log` object. Each constant has 3 properties associated to it:

- `value`: Formatted name of the severity.
- `color`: ANSI color code.
- `to_file`: Specifies if all log events with this particular severity should go to the console or the log file. This property can be configured via the `ConfigurationProvider`.

> For a list of all the severity levels, please see the **JavaDocs** in the class itself.

## LogPrinter.java
Public singleton class that is responsible for actually printing the `Log` objects, either to the console or to the log file.

The logs will have the following format (the values are just an example):

    [INFO]-[20/10/2023 14:10:34.123]-[1234567890]-[... message ...]

Which is composed of four parts:

1. The log level name.
2. The log date and timestamp (millisecond precision).
3. Unique log id (incremental and in hex format).
4. The log message itself.

`LogPrinter` exposes the `log(String message, LogLevel severity)` which is used to synchronously write the `Log` objects.

This class also manages the log files. The files have a maximum size which can be set via the `ConfigurationProvider` and, if the limit is exceeded, a new one will be generated. `LogPrinter` will, therefore, always write to the most recent one.

## LogTask.java

Public class that is instantiated by `Logger` and is tasked with consuming periodically the `Log` objects in order to print them via the protected `LogPrinter.printLog(Log log)` method.