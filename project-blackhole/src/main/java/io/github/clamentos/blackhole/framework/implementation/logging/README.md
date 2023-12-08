# Logging package
This package is aimed at providing global synchronous and asynchronous logging features for all other classes and packages, as well as metrics and statistics about the system while it's running.

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

`LogPrinter` exposes the `logToConsole(String message, LogLevel severity)` and `logToFile(String message, LogLevel severity)` which are used to synchronously log messages to the console and log file respectively. This class also exposes other protected methods used by other classes.

> NOTE: The log id, even though unique, it's a simple variable in memory. If the application exits, the counter will start from 0. This isn't an issue because the logs also contains a millisecond based timestamp.

## LogTask.java
Public class that is instantiated by `Logger` and is tasked with consuming periodically the `Log` objects in order to print them via the dedicated protected `LogPrinter.log(Log log)` method.

## MetricsTask.java
Public class that periodically runs and stores to the database the obtained snapshots from the service. This task also stores all the logs that have been written to the log file so far, deleting it's content in the process.

## MetricsTracker.java
Public singleton class that exposes, to all the other classes, various methods to update all the possible metrics. This service also provides the  protected method `sample()` that can be used to take a "snapshot" of the current system status.
