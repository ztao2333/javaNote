Has Claim Service Design Document

## Document History

|Date|Author|Version|Change|
|  ----- | ----- | ----- | ----  |
|2020-08-30|Aike Zhang|V0.1|

## Content
* <a href="#feature-description">Feature Description</a>
* <a href="#URL">URL</a>
* <a href="#mime-types">MIME Types</a>
  * <a href="#request-specification">Request Specification</a>
    * <a href="#hasclaimrequestdto">HasClaimRequestDTO</a>
    * <a href="#request-sample">Request Sample</a>
  * <a href="#response-specification">Response Specification</a>
    * <a href="#hasclaimresponsedto">HasClaimResponseDTO</a>
    * <a href="#response-sample">Response Sample</a>
    
## Feature Description
Has Claim API to check if the policy has claim.

## URL
/gcs/rest/hasClaimByPolicyNo

## MIME Types
**Content-Type:** application/json

**Method:** Post

## Request Specification

## HasClaimRequestDTO
|Field Name|Field Type|Mandatory|Description|Remark|Example|
|--|--|--|--|--|--|
|policyNo|String|Y|ZA's Policy No||CV000000040412|
|type|String|Y|Claim Status|1=closed;2=unclosed;3=all|3|

## Request Sample
```
{
  policyNo:"CV000000040412",
  type:"3"
}
```

## Response Specification
## HasClaimResponseDTO
|Field Name|Field Type|Description|Remark|Example|
|--|--|--|--|--|
|hasClaim|String|check result|YES,NO|YES|

## Response Sample

Success response
```
{
   "status":    {
      "code": "0",
      "message": "Success"
   },
   "hasClaim": "YES"
}
```
Error response
```
{"status": {
   "code": "ERR_GC_0001",
   "message": "Policy No or Type Parameter Error."
}}
```
