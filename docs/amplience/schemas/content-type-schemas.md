# SAP CX content type schemas

Schemas are needed to define and validate the structure of the data and the input fields.
The file `slot-definition.json` provides the basic contract, that is used in SAP CX.

The file `slot-example.json` provides an example of a slot. Each slot schema __MUST__ contain a property `_environment` which has to be included like this (use the appropriate enum for each particular slot):

```json
"_environment": {
  "title": "Environment",
  "type": "object",
  "properties": {
    "slot": {
      "allOf": [
        {
          "$ref": "https://neteleven.de/amplience/sap-cx/sapcx-slot.json#/definitions/slot"
        },
        {
          "properties": {
            "position": {
              "enum": [
                "stage-slot", "main-content-slot", "header-slot", "footer-slot"
              ]
            }
          }
        }
      ]
    }
  }
},
```

The `_environment` carries the meta information of `position`, `context` and `lookup`, which are essential to identify the right page to place the slot in Hybris.

The file `content-example.json` shows an example content type to be used inside a slot.

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
