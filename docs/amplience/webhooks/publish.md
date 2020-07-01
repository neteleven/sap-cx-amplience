# SAP CX publish
When a slot gets published, the corresponding slot object in SAP CX get published too.

## URL
*POST* - `http://<sap-cx-host>/amp/webhook/<webhook-uri>/publish`

## Webhook triggers
Snapshot: Published

## Filters
`$.payload.createdFrom` Equals `content-item`

## Payload
Standard
