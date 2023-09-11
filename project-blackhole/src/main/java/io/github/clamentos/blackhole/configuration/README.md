# Configuration package
This package is aimed at providing global configuration constants for all other classes and packages.

## ConfigurationProvider.java
Public singleton class containing all the constants initialized at class-load time. The constructor reads the `{classpath}/resources/Application.properties` file to override the default values.

- If no override value is found for a particular constant, the default will be used.
- Unknown properties will be silently ignored.

The class exposes a `Map<String, String> problems` accessible via the `getProblems()` method. If errors (such as illegal values) are encountered, this map will be populated with the name of the offending property as the key and an error string as the value. This can be used for logging and diagnostics since this class cannot log anything as it holds the configuration for everything.

> For a list of all the constants, please se the **JavaDocs** in the class itself.