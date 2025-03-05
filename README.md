# Consistent Hash

## Description
This project is an implementation of a **consistent hashing system** for distributing data across shards. It consists of three independent modules written in Kotlin.

## Modules

### `consistent_hash`
This module provides an implementation of **consistent hashing** as a data structure. It can be reused anywhere a persistent cache is required, and values are read based on the closest positions on the consistent hashing ring rather than exact keys.
 Uses MurmurHash by default but can be replaced with other hashing algorithms if needed.

### `coordinator`
This module provides a set of **abstractions and default implementations** built on top of `consistent_hash`. It manages entities such as servers, allowing the addition and removal of servers from the ring and determining their state and current hash.

- **Features**:
    - Server management (addition/removal from the ring).
    - Abstract logic for data redistribution when servers are added or removed.
    - Facilitates state tracking of the distributed system.

### `redis_coordinator`
This module implements **sharding for Redis** using `coordinator`. It extends the abstract logic with Redis-specific server implementations and handles **data migration** operations.

- **Purpose**: Demonstrates a basic, functional implementation of Redis sharding.
- **Extensibility**: A similar approach can be applied to other sharded NoSQL databases by implementing the abstract methods in `coordinator`.
- **Example**: RedisCluster already provides built-in sharding capabilities, but this module serves as an educational reference.

## Technology Stack
- **Kotlin**: 1.9.25
- **Spring Boot**: 3.3.5
- **Jedis**: 5.2.0
- **Swagger**: API documentation
