package io.github.clamentos.blackhole.logging;

/**
 * Log object, used as an item in the log queue.
*/
public record Log(String message, LogLevel log_level, String file_path) {}
