# Gradebook project

## Learning Objectives

* Learn usage of Spring Boot for testing
* Learning usage of H2 Embedded DB
* Applying TDD method
* Practicing JUnit and Mockito

## Description

Testing an existing project using Spring Boot framework, JUnit, Mockito, MockMvc, H2 Embedded DB and SQL. Most of the tests were done using the Test Driven Development (TDD) approach, which I found to be very useful and time saving.

## How to Install and Run the Project
1. Clone the repository to your local machine 
    `git clone https://github.com/Ezzerof/3.00-starting-project.git`
2. Open the project in your preferred IDE.
3. Run `java test package` to start the application.

## Requirements

* Java(17)
* Git
* Spring boot
* H2 DB

## Dependencies

* Thymeleaf
``` 
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
* Spring Boot web
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
* Spring Boot test
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-test</artifactId>
	<scope>test</scope>
</dependency>
```
* MySQL java connector
```
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<scope>runtime</scope>
</dependency>
```
* Spring data jpa
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
* H2 database
```
<dependency>
	<groupId>com.h2database</groupId>
	<artifactId>h2</artifactId>
	<scope>runtime</scope>
</dependency>
```
