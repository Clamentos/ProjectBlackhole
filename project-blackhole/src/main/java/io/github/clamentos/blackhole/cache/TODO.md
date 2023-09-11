# Things to do
This file will keep track of the tasks that have to be done for all the sub-packages. Each sub-section will be composed of:

- Title.
- Priority.
- Status.
- Description.
- Notes.

## Caching

- **Priority:** `low`
- **Status:** `not started`

Implement an entity caching mechanism to improve request performance and lower database traffic. The cache should not be allowed to grow indefinetly.

### Note
The cache will have to map the request itself (as the key) with one or more database entities. This helps in cases where 2 distinct read requests map to the same set of entities. The replacement policy could be LRU or PLRU and the cache could be write-back too.

A potential solution could be having `Map<Request, Entry>`, where `Entry` would be an object containing

- `List<Entity>` to point to the cached entities.
- The LRU counter (or similar to track replacement).
- Dirty flag.