# Sample Rest API
This sample rest api contains following RESTful APIs:
- Account to Account Transfer Amount
- Deposit from Account to And External Address
- Withdrawal from an External into an Internal Account

## Framework
- Vert.x

## Domains
- Account : To keep data of internal account
- TransactionInfo : contains information about created transactions


## How to build and run

Generate Jar File:

$ ./gradlew shadowJar

Run application:

$ java -jar build/libs/sample-rest-1.0.0-fat.jar

## How it Works
By running application, it creates about 10 pre-defined accounts with ids from 1 to 10 that are known as internal accounts.
Also, can do deposit or withdrawal to and from a external account.

### APIS

#### Get list of all pre-defined accounts:
$ curl --location 'localhost:8080/accounts'


#### Get just an account:
Ex. account with id 1 :

$ curl --location 'localhost:8080/accounts/1'


#### Internal transfer:
Node: RequestId should be the UUID formatted

Ex. transfer $100 from account id 1 to 2 :

$ curl --location 'localhost:8080/transactions/transfers/internal' \
--header 'Content-Type: application/json' \
--data '{
"requestId":"9458037a-40ee-4503-b3c3-e409b3d05a55",
"fromAccountId":1,
"toAccountId":2,
"amount":100
}'


#### Deposit from internal account to an external account:
Ex. transfer $100 from account id 1 to external account address 9458037a :

$ curl --location 'localhost:8080/transactions/transfers/to-external' \
--header 'Content-Type: application/json' \
--data '{
"requestId":"9458037a-40ee-4503-b3c3-e409b3d05a52",
"fromAccountId":1,
"withdrawalAddress":"9458037a",
"amount":100
}'


#### Withdrawal from an external address to an internal account:
Ex. transfer $100 from external account address 9458037a to internal account 1 :

curl --location 'localhost:8080/transactions/transfers/from-external' \
--header 'Content-Type: application/json' \
--data '{
"requestId":"9458037a-40ee-4503-b3c3-e409b3d05a61",
"toAccountId":1,
"withdrawalAddress":"9458037a",
"amount":100
}'


#### Get Transaction Status:
Process of transfer happened in an async, to get status of transaction you can call transaction detail api.

Ex. Get status of a transaction with request id "9458037a-40ee-4503-b3c3-e409b3d05a52" :

$ curl --location 'localhost:8080/transactions/9458037a-40ee-4503-b3c3-e409b3d05a52'