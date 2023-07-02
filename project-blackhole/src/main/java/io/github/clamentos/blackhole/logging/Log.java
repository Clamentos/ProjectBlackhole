package io.github.clamentos.blackhole.logging;

/**
 * <p>Log object.</p>
 * Used as an item in the log queue.
*/
public record Log(String message, LogLevel log_level) {}
