# JROMP (Java Runtime OpenMP)

JROMP is a Java library that provides a simple API for parallel programming using OpenMP-like constructs, but in
runtime. It is designed to provide a simple way to parallelize Java code. JROMP is implemented
using Java's `ThreadPoolExecutor` and designed to be efficient and scalable.

## Features

- Simple API for parallel programming.
- Efficient and scalable implementation using `ThreadPoolExecutor`.
- Compatible with **Java 21** and later.
- Minimal dependencies.
- Easy to use.

### Supported constructs

- `parallel` - Executes a block of code in parallel.
- `for` - Executes a loop in parallel.
- `sections` - Executes a block of code in parallel.
- `single` - Executes a block of code in a single thread.
- `barrier` - Synchronizes all threads.
- `critical` - Executes a block of code in a critical section.
- `atomic` - Executes a block of code atomically.
- `reduction` - Performs a reduction operation.
- `masked` - Executes a block of code in the specified thread.

### Supported variables

- `shared` - Shared variable between threads.
- `private` - Private variable for each thread.
- `firstprivate` - Private variable with initial value (it takes the value prior to the parallel region).
- `lastprivate` - Last private variable (it takes the value after the parallel region).
- `reduction` - Reduction variable (it accumulates the value of each thread depending on the operation).
- `atomic` - Atomic variable (it is updated atomically).

## Installation

To use JROMP in your project, you can add the dependency using the following code snippets:

### Maven

<!-- @formatter:off -->
```xml
<dependency>
  <groupId>io.github.java-romp</groupId>
  <artifactId>jromp</artifactId>
  <version>2.0.1</version>
</dependency>
```
<!-- @formatter:on -->

### Gradle

```groovy
implementation 'io.github.java-romp:jromp:2.0.1'
```

If your package manager is not listed here, you can check the latest version on
the [Maven Central Repository](https://central.sonatype.com/artifact/io.github.java-romp/jromp).

## Usage

Here is an example of how to use JROMP to execute a simple parallel task in all available threads:

```java
import jromp.Constants;
import jromp.JROMP;

import static jromp.JROMP.getNumThreads;
import static jromp.JROMP.getThreadNum;

public class BasicUsage {
  public static void main(String[] args) {
    JROMP.allThreads()
         .block(variables -> System.out.printf("Hello World from thread %d of %d%n", getThreadNum(), getNumThreads()))
         .join();
  }
}
```

This code will print `Hello World from thread X of Y` for each thread, where X is the thread ID and Y is the total
number of threads.

## More examples

If you want to see more examples of how to use JROMP, you can check all the available examples
(with its equivalent code written in C) in the [jromp-examples](https://github.com/java-romp/jromp-examples)
repository.

