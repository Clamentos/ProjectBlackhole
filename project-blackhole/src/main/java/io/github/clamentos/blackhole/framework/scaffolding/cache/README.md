# Cache package
This package is aimed at providing basic entity caching support.

## Cacheability.java
Public enumeration composed of three values that describe the cacheability level of the entity. The values are:

- **ALWAYS** : The entity will always be cached.
- **SIZE_LIMITED** : The entity will be cached if its size is less or equal to the values specified by the `getCacheabilitySizeLimit()` method of the entity itself.
- **NEVER** : The entity will never be cached.
