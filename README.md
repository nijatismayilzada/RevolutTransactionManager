# RevolutTransactionManager

## Setup

* Run below to start the microservice:

For Linux:
```bash
./gradlew run
```
For Windows:
```bash
gradlew.bat run
```

* Application starts at 8082 port.

* You also need to get RevolutAccountManager and start that too (that starts at 8081 port): https://github.com/nijatismayilzada/RevolutAccountManager


## Rest API

* Create new user named "Nijat":
> This returns created user id -> 1
```bash
curl --request POST
  --url http://localhost:8081/users/create
  --header 'Content-Type: application/json'
  --data '{"name":"Nijat"}'
```

* Create new GBP account for user id 1 ("Nijat" user):
> This returns created account id -> 1
```bash
curl --request POST
  --url http://localhost:8081/users/accounts/create
  --header 'Content-Type: application/json'
  --data '{"userId" : 1,"currency" : "GBP"}'
```

* Create new simple Revolut payment transaction for account id 1 to increase balance by 10 pounds:
> This returns created transaction id -> 1
```bash
curl --request POST
  --url http://localhost:8082/transactions/create
  --header 'Content-Type: application/json'
  --data '{"accountId":1,"reference" : "SomeBankPayment","transactionType":"REVOLUT_SIMPLE","transactionAction":"INCREASE","amount":10.00,"currency":"GBP"}'
```

* Get the details of transaction 1 to learn its status:

```bash
curl --request GET
  --url http://localhost:8082/transactions/transaction-id/1
```

* Get the details of user 1 with its account to learn the balance:

```bash
curl --request GET 
  --url http://localhost:8081/users/user-id/1 
```


* You can create new simple Revolut payment transaction for account id 1 to decrease balance by 5 pounds:
```bash
curl --request POST
  --url http://localhost:8082/transactions/create
  --header 'Content-Type: application/json'
  --data '{"accountId":1,"reference" : "SomeBankPayment","transactionType":"REVOLUT_SIMPLE","transactionAction":"DECREASE","amount":5.00,"currency":"GBP"}'
```

## Some underlying technologies

* Gradle
* Jersey REST Api
* Embedded Jetty container
* Hk2 Dependency management
* H2 in-memory non-persistent database
* Embedded ActiveMQ broker messaging
* JUnit & Mockito for testing

## How it works

![Image of architecture](https://raw.githubusercontent.com/nijatismayilzada/RevolutTransactionManager/master/revolut.jpg?token=ABVDDEQ3NQLF2V7BDVLE4SC54Q6V6)