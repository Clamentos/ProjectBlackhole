package io.github.clamentos.blackhole.logging;

import io.github.clamentos.blackhole.config.LogFiles;

/**
 * Log object, used as an item in the log queue.
*/
public record Log(String message, LogLevel log_level, LogFiles log_file) {}
