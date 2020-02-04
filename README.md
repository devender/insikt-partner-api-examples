# Insikt's Partner API Usage

## Prerequisites

*   Java 1.8
*   Gradle
*   Access to one of Insikt's environments
*   Valid RSA key & Unique Identifier please see src/main/resources/application.properties

## Testing

*   Navigate to this project and Run `./gradlew bootRun`, this will run all the examples

#### Echo Example
```
DEBUG [2018-08-10 09:57:26,331] [main] [] [] i.partner.api.examples.EchoExample: []/[] [] [] *********************************Running Echo Example.**********************************************
TRACE [2018-08-10 09:57:26,338] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] Sending: echo http://partner-api.training.lendifyfin.com/api/v2/echo?message=Hello
TRACE [2018-08-10 09:57:27,100] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] Returned: response {success=true, message=Echo response, data={message=Hello}}
DEBUG [2018-08-10 09:57:27,101] [main] [] [] i.partner.api.examples.EchoExample: []/[] [] [] *********************************Done**********************************************
```

### Payment Example
```
DEBUG [2018-08-10 09:57:27,101] [main] [] [] i.p.api.examples.PaymentExample: []/[] [] [] *********************************Running Payment Example..**********************************************
DEBUG [2018-08-10 09:57:27,101] [main] [] [] i.p.api.examples.PaymentExample: []/[] [] [] Properties to use for payment PaymentExampleProperties(partnerLocationId=1000, providerTransactionId=898989, partnerTransactionId=12345, totalPaymentAmount=10.0, paymentMethod=CASH, productAccountId=124-022179-101, paymentMeanId=0)
TRACE [2018-08-10 09:57:27,105] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] partner api authorize request {paymentMeanId=0, partnerLocationId=1000, providerTransactionId=898989, partnerTransactionId=12345, totalProducts=1, paymentMethod=CASH, totalPaymentAmount=10.0, products=[{productAccountId=124-022179-101, paymentAmount=10.0, paymentDate=2018-08-10}]}
TRACE [2018-08-10 09:57:27,930] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] partner api authorize response {success=true, message=, errors=[], data={partnerLocationId=1000, partnerTransactionId=12345, providerTransactionId=898989, totalProducts=1, totalPaymentAmount=10, paymentMethod=CASH, paymentTransactionId=SHAyYzBYRlR3ZjZjT0VJSmNsR1lDS29Ua2dvSG9yUkNQT1FqM04xVHhPST0=, products=[{productAccountId=124-022179-101, paymentAmount=10, paymentDate=2018-08-10, authorizationId=3000, transactionFee=0, existingBalance=3224.98, updatedBalance=3214.98, nextPaymentDueDate=2018-02-03, nextPaymentAmount=221}]}, httpHeaders={}}
DEBUG [2018-08-10 09:57:27,930] [main] [] [] i.p.api.examples.PaymentExample: []/[] [] [] authorize succeeded , proceeding to submit
TRACE [2018-08-10 09:57:27,932] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] partner api submit payment request {paymentMeanId=0, partnerLocationId=1000, providerTransactionId=898989, partnerTransactionId=12345, totalProducts=1, paymentMethod=CASH, totalPaymentAmount=10.0, source={"paymentLatitude":null,"paymentType":null,"paymentLongitude":null}, paymentTransactionId=SHAyYzBYRlR3ZjZjT0VJSmNsR1lDS29Ua2dvSG9yUkNQT1FqM04xVHhPST0=, products=[{productAccountId=124-022179-101, paymentAmount=10.0, paymentDate=2018-08-10, authorizationId=3000, transactionFee=0.0}]}
TRACE [2018-08-10 09:57:29,465] [main] [] [] insikt.partner.api.PartnerApiClient: []/[] [] [] partner api submit payment response {success=true, message=, errors=[], data={partnerLocationId=1000, partnerTransactionId=12345, providerTransactionId=898989, paymentTransactionId=SHAyYzBYRlR3ZjZjT0VJSmNsR1lDS29Ua2dvSG9yUkNQT1FqM04xVHhPST0=, statusReason=, createdDate=2018-08-10T16:57:29.317Z, modifiedDate=2018-08-10T16:57:29.317Z, status=ACCEPTED}, httpHeaders={}}
DEBUG [2018-08-10 09:57:29,465] [main] [] [] i.p.api.examples.PaymentExample: []/[] [] [] submit successful ? true
DEBUG [2018-08-10 09:57:29,465] [main] [] [] i.p.api.examples.PaymentExample: []/[] [] [] *********************************Done..**********************************************
```
