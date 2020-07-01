# SAP CX edition
When an editions goes live, the corresponding slot objects in SAP CX get published.

## URL
*POST* - `http://<sap-cx-host>/amp/webhook/<webhook-uri>/edition`

## Webhook triggers
Edition: Published

## Filters
`$.payload.createdFrom` Equals `edition`

## Payload
Standard
