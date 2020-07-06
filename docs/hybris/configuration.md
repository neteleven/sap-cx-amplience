# Configuration

Most of the configuration of the integration is done in the SAP CX Backoffice application.
In the backoffice, there is a menu item called `Amplience`. There you find all new itemtypes, created by the integration.

## Amplience Webhook

Main configuration object

* __URI:__ URI to be used in Amplience as part of the Webhook URL. Used to identify this configuration.
* __Secret:__ Secret used for validation of the content
* __Validate Secret:__ Whether to validate the content or not. Useful in DEV environment.
* __API Virtual Staging Environment:__ URL of the Virtual Staging Environment API
* __Endpoint:__ Endpoint in Amplience
* __Content Types:__ List of supported content types
* __Locale Mappings:__ Used locale mappings (Amplience locales and Hybris locales)
* __CMS Sites:__ corresponding CMS Site in SAP CX
* __Requests:__ List of pending requests for this webhook
* __Slots:__ List of slots, that use this webhook configuration


## Amplience Content Type

Object that marks a supported content type that should be handled in SAP CX

* __URI:__ URI of the supported content type
* __Template:__ Template used for content rendering service
* __Webhooks:__ List of webhooks, that use this content type
* __Slots:__ List of slots, that use this content type


## Amplience Webhook Request

A webhook request that is not yet processed

* __ID:__ Amplience ID of the webhook request
* __Type:__ Webhook type (UPDATE, PUBLISH, EDITION, DELETE)
* __JSON:__ Raw JSON of the request
* __Webhook:__ Webhook configuration


## Amplience Slot

Main object to store content in SAP CX. Representation of one version of the slot in Amplience

* __ID:__ Amplience ID of the slot
* __Name:__ Readable name of the slot
* __Version:__ Current version of the slot
* __Published:__ Whether this version was already published or not
* __Position:__ Lookup parameter "position" used to identify the slot
* __Context:__ Lookup parameter "context" used to identify the slot
* __Lookup:__ Lookup parameter "lookup" used to identify the slot
* __Delivery Key:__ Alternative lookup parameter to identify the slot - the delivery key by Amplience
* __Edition ID:__ ID of the edition if slot was published as part of an edition
* __Edition Start:__ Start date of an edition
* __Edition End:__ End date of an edition (only used when edion is set to expire)
* __HTML:__ Localized HTML of the slot version
* __JSON:__ Localized JSON of the slot version
* __Webhook:__ Webhook configuration
* __Content Type:__ Content type of the slot


## Amplience Locale Mapping

A mapping between the locale used in Amplience and SAP CX. The SAP CX locale is used when JSON and HTML is persisted.

* __Amplience Locale:__ Code of locale in Amplience
* __SAP CX Locale:__ Code of locale in SAP CX
* __Webhooks:__ List of webhooks, that use this mapping


## Configuration of JSON parsing

As the payload in Amplience webhooks is not fixed, we decided to have the extraction of data in the webhook processing configurable.
The `project.properties` file in the `amplienceintegration` extension contains JSONPath expressions, that specify what in the webhooks payload is used.

```
amplience.json.webhook.request.id=$.requestId
amplience.json.webhook.update.contenttype=$.payload.body._meta.schema
amplience.json.webhook.update.id=$.payload.id
amplience.json.webhook.update.label=$.payload.label
amplience.json.webhook.update.version=$.payload.version
amplience.json.webhook.update.editionid=$.payload.body._meta.edition.id
amplience.json.webhook.update.editionstart=$.payload.body._meta.edition.start
amplience.json.webhook.update.editionend=$.payload.body._meta.lifecycle.expiryTime
amplience.json.webhook.update.deliveryKey=$.payload.body._meta.deliveryKey
amplience.json.webhook.delete.id=$.payload.id
amplience.json.webhook.publish.id=$.payload.rootContentItem.id
amplience.json.webhook.edition.id=$.payload.id
amplience.json.slot.context=$.content._environment.slot.context
amplience.json.slot.lookup=$.content._environment.slot.lookup
amplience.json.slot.position=$.content._environment.slot.position
```

It is easily possible to overwrite those settings in your `local.properties` file and adjust the behaviour to your needs.

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
