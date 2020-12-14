#### How to build:

If the mysql container is NOT running [the integration tests require a db]:

`mvn clean install -DskipTests`

If the mysql container is running:

`mvn clean install`


##### How to run the web app

1. Run the mysql container. The port 3306 must not be in use on the machine you run this on.

`docker container stop network-db-container ; docker container rm network-db-container ; docker run -p 3306:3306 --name network-db-container -e MYSQL_ROOT_PASSWORD=network-pass -e MYSQL_DATABASE=db_network -e MYSQL_USER=springuser -e MYSQL_PASSWORD=SpringUserPassword -d mysql:8`

2. Run the Spring based app

`./mvnw spring-boot:run` 

#### How to use the app

- Insert new networks using the 

```
curl --location --request POST 'http://localhost:8080/insert-network' \
--header 'Content-Type: application/json' \
--data-raw '{
    <json payload containing networks>
}'
```

- Get the list of networks in which an IP is present

``curl --location --request GET 'http://localhost:8080/get-networks-by-ip?IP=<ip v4/v6 address>'``



#### Postman + System tests 
A postman collection that contains end to end tests is provided in [PostmanNewmanTests](PostmanNewmanTests)


#### Assumptions
Not all the things were clear when I started to design and implement the app so. I was not able to get answers for 
my questions and, unfortunately I had to make some assumptions. 

Requirements and assumptions are in [Requirements/Questions&Assumptions.txt](Requirements/Questions&Assumptions.txt)


#### Improvements / TODOs

- Use an in memory db for integration tests. This way the docker container with the db is not be needed anymore 
when building with tests: `mvn clean install`
- Improve the REST response status and provide extra messages on errors.
- Improve test coverage


#### Examples

``curl --location --request POST 'http://localhost:8080/insert-network' \
  --header 'Content-Type: application/json' \
  --data-raw '{
      "Berlin": {
          "BER-1": {
              "security_level": 1,
              "networks": {
                  "192.168.0.0/24": [
                      {
                          "address": "255.255.255.0",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168..0.3",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.288",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "invalid",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.1",
                          "available": false,
                          "last_used": "30/01/20 16:00:00"
                      },
                      {
                          "address": "192.168.0.4",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.2",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.3",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.1.1",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ],
                  "10.0.8.0/22": [
                      {
                          "address": "10.0.11.254",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "10.0.8.1",
                          "available": false,
                          "last_used": "30/01/20 16:00:00"
                      },
                      {
                          "address": "10.0.8.0",
                          "available": false,
                          "last_used": "30/01/20 16:00:00"
                      },
                      {
                          "address": "10.0.12.1",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "10.0.10.a",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ]
              }
          },
          "BER-203": {
              "security_level": 3,
              "networks": {
                  "192.168.10.0/24": [
                      {
                          "address": "192.168.10.8",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.10.5",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.10.6",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.7",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ],
                  "192.168.11.0/24": [
                      {
                          "address": "192.168.11.1",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.2.1",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.11.522",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ]
              }
          },
          "BER-4000": {
              "security_level": 3,
              "networks": {
                  "192.168.100.0/24": [
                      {
                          "address": "192.168.100.1",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ]
              }
          },
          "TEST-1": {
              "security_level": 3,
              "networks": {
                  "192.168.200.0/24": [
                      {
                          "address": "192.168.200.8",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ]
              }
          }
      },
      "Paris": {
          "PAR-1": {
              "security_level": 5,
              "networks": {
                  "192.168.203.0/24": [
                      {
                          "address": "192.168.203.20",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.203.21",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.203.19",
                          "available": false,
                          "last_used": "30/01/20 17:00:00"
                      },
                      {
                          "address": "192.168.0.0",
                          "available": true,
                          "last_used": "30/01/20 17:00:00"
                      }
                  ]
              }
          },
          "XPAR-2": {
              "security_level": 0,
              "networks": {
                  "2001:0db8:0000:0000:0000:0000:0000:0000/64": [
                      {
                          "address": "2001:0db8:0000:0000:0000:8a2e:0370:7334",
                          "available": true,
                          "last_used": "30/12/20 17:00:00"
                      }
                  ]
                  }
          }
      }
  }'``
  
  ``curl --location --request GET 'http://localhost:8080/get-networks-by-ip?IP=192.168.203.19'``
   

