package io.github.clamentos.blackhole.logging;

/**
 * Log object.
 * Used by the log queue.
*/
public record Log(String message, LogLevel log_level, String file_path) {}
