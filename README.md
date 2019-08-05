# Multi-language Annotation Suggester (ASIA-MAS)

This project implements a multi-language header suggestion service that interacts with [Grafterizer](https://github.com/UNIMIBInside/asia-backend) and [ASIA](https://github.com/UNIMIBInside/asia-backend) ecosystem to help the used the annotate a table at schema level in several languagese.
This work is part of [EuBusinessGraph](https://www.eubusinessgraph.eu/) EU project. 

## Requirements and Prerequisites**: 
- ASIA-MAS has been developed and tested using **Java 1.8**.  
- An **Azure subscription key for Translator Text** (to get one follow this [tutorial](https://crunchify.com/microsoft-translator-text-api-example/))


## Build and Run
From root directory:
```
$ cd suggester
$ ./mvnw package
$ java -jar target/suggester-0.1.jar --suggester.translator.subscription-key=XXXXXXXXXXXXXXX
```

The Azure subscription key can also be set as an environment variable:

```
(on Linux)
$ SUGGESTER_TRANSLATOR_SUBSCRIPTION_KEY=XXXXX
$ echo $SUGGESTER_TRANSLATOR_SUBSCRIPTION_KEY
$ java -jar target/suggester-0.1.jar
```

To create a Docker container from sources:
```shell script
$ cd suggester
$ ./mvnw package
$ ./mvnw docker:build -Ddocker.account.name=<ACCOUNT_NAME>

```

