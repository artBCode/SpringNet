##### How to build:

If the mysql container is NOT running [the integration tests require a db]:

`mvn clean install -DskipTests`

If the mysql container is running:

`mvn clean install`


##### How to run the web app

1. Run the db container

`docker container stop network-db-container ; docker container rm network-db-container ; docker run -p 3306:3306 --name network-db-container -e MYSQL_ROOT_PASSWORD=network-pass -e MYSQL_DATABASE=db_network -e MYSQL_USER=springuser -e MYSQL_PASSWORD=SpringUserPassword -d mysql:8`

2. Run the Spring based app

`./mvnw spring-boot:run` 

##### Postman + System tests 
A postman collection that contains end to end tests is provided in [PostmanNewmanTests](PostmanNewmanTests)


##### Assumptions
Not all the things were clear when I started to design and implement the app so. I was not able to get answers for 
my questions and, unfortunately I had to make some assumptions. 

Requirements and assumptions are in [Requirements/Questions&Assumptions.txt](Requirements/Questions&Assumptions.txt)


##### Improvements / TODOs

- Use an in memory db for integration tests. This way the docker container with the db is not be needed anymore 
when building with tests: `mvn clean install`
- Improve the REST response status and provide extra messages on errors.
   

