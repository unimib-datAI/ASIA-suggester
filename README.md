# Multi-language Annotation Suggester (ASIA-MAS)

This project implements a multi-language header suggestion service that interacts with [Grafterizer](https://github.com/UNIMIBInside/asia-backend) and [ASIA](https://github.com/UNIMIBInside/asia-backend) ecosystem to help the used the annotate a table at schema level in several languagese.
This work is part of [EuBusinessGraph](https://www.eubusinessgraph.eu/) EU project. 

## Build and Run

**Requirements**: ASIA-MAS has been developed and tested using Java 1.8.  

From root directory:
```
$ cd suggester
$ ./mvnw package
$ java -jar target/AsiaBackend.jar
```