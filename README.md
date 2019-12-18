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
curl --request POST --url http://localhost:8081/users/create --header 'Content-Type: application/json' --data '{"name":"Nijat"}'
```
<p>&nbsp;</p>

* Create new GBP account for user id 1 ("Nijat" user):
> This returns created account id -> 1
```bash
curl --request POST --url http://localhost:8081/users/accounts/create --header 'Content-Type: application/json' --data '{"userId" : 1,"currency" : "GBP"}'
```
<p>&nbsp;</p>

* Create new simple Revolut payment transaction for account id 1 to increase balance by 10 pounds:
> This returns created transaction id -> 1
```bash
curl --request POST --url http://localhost:8082/transactions/create --header 'Content-Type: application/json' --data '{"accountId":1,"reference" : "SomeBankPayment","transactionType":"REVOLUT_SIMPLE_INCREASE","amount":10.00,"currency":"GBP"}'
```
<p>&nbsp;</p>

* Get the details of transaction 1 to learn its status:
```bash
curl --request GET --url http://localhost:8082/transactions/transaction-id/1
```
<p>&nbsp;</p>

* Get the details of user 1 with its account to learn the balance:
```bash
curl --request GET  --url http://localhost:8081/users/user-id/1 
```
<p>&nbsp;</p>

* You can create new simple Revolut payment transaction for account id 1 to decrease balance by 2 pounds:
> This returns created transaction id -> 2
```bash
curl --request POST --url http://localhost:8082/transactions/create --header 'Content-Type: application/json' --data '{"accountId":1,"reference" : "SomeBankPayment","transactionType":"REVOLUT_SIMPLE_DECREASE","amount":2.00,"currency":"GBP"}'
```
<p>&nbsp;</p>

* Create another GBP account for user id 1 ("Nijat" user):
> This returns newly created account id -> 2
```bash
curl --request POST --url http://localhost:8081/users/accounts/create --header 'Content-Type: application/json' --data '{"userId" : 1,"currency" : "GBP"}'
```
<p>&nbsp;</p>

* Create new Revolut transfer transaction for moving 3 pounds from account 1 to account 2:
> This returns created transaction id -> 3
```bash
curl --request POST --url http://localhost:8082/transactions/create --header 'Content-Type: application/json' --data '{"accountId":1,"reference" : "2","transactionType":"REVOLUT_TRANSFER","amount":3.00,"currency":"GBP"}'
```
<p>&nbsp;</p>

* Get the details of all transactions for account 1 and 2 to view overall account:
```bash
curl --request GET --url http://localhost:8082/transactions/account-id/1
curl --request GET --url http://localhost:8082/transactions/account-id/2
```
<p>&nbsp;</p>

* Check the details of user 1 with all of its accounts to learn the balances:
```bash
curl --request GET  --url http://localhost:8081/users/user-id/1 
```
<p>&nbsp;</p>


## Some underlying technologies

* Gradle
* Jersey REST Api
* Embedded Jetty container
* Hk2 Dependency management
* H2 in-memory non-persistent database
* Embedded ActiveMQ broker messaging
* JUnit & Mockito for testing

## How it works

## Create user
![Image of architecture](https://raw.githubusercontent.com/nijatismayilzada/RevolutTransactionManager/master/revolut-insert-user.jpg?token=ABVDDEQ3NQLF2V7BDVLE4SC54Q6V6)
## Create account
![Image of architecture](https://raw.githubusercontent.com/nijatismayilzada/RevolutTransactionManager/master/revolut-insert-account.jpg?token=ABVDDEQ3NQLF2V7BDVLE4SC54Q6V6)
## Simple transaction
![Image of architecture](https://raw.githubusercontent.com/nijatismayilzada/RevolutTransactionManager/master/revolut-simple-transaction.jpg?token=ABVDDEQ3NQLF2V7BDVLE4SC54Q6V6)
## Transfer transaction
![Image of architecture](https://raw.githubusercontent.com/nijatismayilzada/RevolutTransactionManager/master/revolut-transfer-transaction.jpg?token=ABVDDEQ3NQLF2V7BDVLE4SC54Q6V6)