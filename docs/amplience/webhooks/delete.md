# SAP CX delete
When a slot gets archived, the corresponding slot objects in SAP CX get deleted.

## URL
*POST* - `http://<sap-cx-host>/amp/webhook/<webhook-uri>/delete`

## Webhook triggers
Content item: Updated

## Filters
`$.payload.status` Equals `ARCHIVED`

## Payload
Standard
