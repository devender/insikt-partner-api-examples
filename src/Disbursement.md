# Disbursement Flow

## Disbursement Authorization Flow


5b71ee20e4b0da54ccfddf13
5b71f2d5e4b0da54ccfddf44
5b71f591e4b0da54ccfddf6f
5b71f746e4b0da54ccfddf97
5b71f8e9e4b0da54ccfddfbc

##### Application Loan - Disbursement Authorization
```
curl -k -v -H "Content-Type: application/json" -H "Authorization: Bearer $BEARER" -X POST -d '{
  "partnerUserId": "string",
  "partnerLocationId": "1000",
  "partnerStationId": "string",
  "partnerTransactionId": "string",
  "applicationId": "5b71f8e9e4b0da54ccfddfbc",
  "disbursementAmount": 500.00,
  "disbursementDate": "2018-08-15",
  "paymentMethod": "CASH"
}' "https://partner-api.training.lendifyfin.com/api/v2/products/loan/applications/5b71f8e9e4b0da54ccfddfbc/disbursements/fund"
```

Response
```
{"success":false,"message":"","errors":[{"type":"global","path":"applicationId","code":"notFound","messageCode":"applicationId.notFound","args":null}],"data":{},"httpHeaders":{}}
```