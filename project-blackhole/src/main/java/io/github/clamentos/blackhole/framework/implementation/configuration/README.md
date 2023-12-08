# Configuration package
This package is aimed at providing global configuration constants for all other classes and packages.

## ConfigurationProvider.java
Public singleton class containing all the constants initialized at class-load time. The constructor reads the `{classpath}/resources/Application.properties` file to override the default values.

- If no override value is found for a particular constant, the default will be used.
- Unknown properties will be silently ignored.

> For a list of all the constants, please se the **JavaDocs** in the class itself.