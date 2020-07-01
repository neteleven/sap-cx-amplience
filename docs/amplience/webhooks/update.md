# SAP CX update
Creates new versions of slot objects in SAP CX.

## URL
*POST* - `http://<sap-cx-host>/amp/webhook/<webhook-uri>/update`

## Webhook triggers
Content item: Created, Updated

## Filters
`$.payload.status` Equals `ACTIVE`

## Payload
Standard
