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

# License
Copyright (c) 2020. neteleven GmbH (https://www.neteleven.de/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
