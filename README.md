Expected output will be in package root dir in `ApacheBeamTutorials/out`

There will be 2 files emitted, but one will be empty, this is probably due to: https://stackoverflow.com/questions/72126198/how-can-we-prevent-empty-file-write-in-dataflow-pipeline-when-collection-size-is

Input File

```
{"segmentName":  "abc", "requestId": "123", "eventType": "REQUEST", "payload": "{\"key\": \"val\"}"}
{"segmentName":  "abc", "requestId": "123", "eventType": "RESPONSE", "payload": "{\"key\": \"val2\"}"}
{"segmentName":  "abc", "requestId": "123", "eventType": "RESPONSE", "payload": "{\"key\": \"val3\"}"}
{"segmentName":  "abc", "requestId": "123", "eventType": "RESPONSE", "payload": "{\"key\": \"val4\"}"}
{"segmentName":  "abc", "requestId": "123", "eventType": "REQUEST", "payload": "{\"key\": \"val\"}"}
{"segmentName":  "abc", "requestId": "123", "eventType": "RESPONSE", "payload": "{\"key\": \"val2\"}"}

```

Expected output in file: 

`parsed payloads`
```
REQUEST val
RESPONSE val2
```

`counts by parsed payloads`
```
RESPONSE-val2: 2
RESPONSE-val4: 1
RESPONSE-val3: 1
REQUEST-val: 2
```
