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

## Getting Started

## Installation

To use JROMP in your project, you can add the dependency using the following code snippets:

### Maven

<!-- @formatter:off -->
```xml
<dependency>
  <groupId>io.github.java-romp</groupId>
  <artifactId>jromp</artifactId>
  <version>0.0.1</version>
</dependency>
```
<!-- @formatter:on -->

### Gradle

```groovy
implementation 'io.github.java-romp:jromp:0.0.1'
```

## Usage

Here is an example of how to use JROMP to execute a simple parallel task in all available threads:

```java
import jromp.Constants;
import jromp.parallel.Parallel;

public class BasicUsage {
  public static void main(String[] args) {
    Parallel.defaultConfig()
            .begin((id, variables) -> System.out.printf("Hello World from thread %d of %d%n", id,
                    variables.<Integer>get(Constants.NUM_THREADS).value()))
            .join();
  }
}
```

This code will print `Hello World from thread X of Y` for each thread, where X is the thread ID and Y is the total
number of threads.

## More examples

If you want to see more examples of how to use JROMP, you can check all the available examples
(with its equivalent code written in C) in the [jromp-examples](https://github.com/scastd/jromp-examples)
repository.

