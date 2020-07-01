# Architecture

## Overview

```
┌────────────────────────────────────────────────┐    ╔═══════════════════════╗     ╔═════════════════════════╗     ╔═════════════════════════╗
│                                                │    ║                       ║     ║                         ║     ║                         ║
│              Amplience Dynamic Content         │    ║ amplienceintegration  ║     ║     Hybris Backend      ║     ║     Hybris Frontend     ║
│                                                │    ║                       ║     ║                         ║     ║                         ║
│                                                │    ║                       ║     ║                         ║     ║                         ║
│                                                │    ║                       ║     ║                         ║     ║                         ║
│ ┌──────────────────────┐                       │    ║ ┌──────────────────┐  ║     ║   ┌──────────────────┐  ║     ║                         ║
│ │                      │   update / publish    │    ║ │                  │  ║     ║   │                  │  ║     ║                         ║
│ │  Edition (optional)  ├───────────────────────┼────╬─▶   /amp/webhook   ├──╬─────╬───▶     Webhook      │  ║     ║                         ║
│ │                      │                     ┌─┼────╬─▶                  │  ║     ║   │  configuration   │  ║     ║                         ║
│ │                      │                     │ │    ║ │                  │  ║   ┌─╬───┤                  │  ║     ║                         ║
│ └─────┬───────────┬────┘                     │ │    ║ └──────────────────┘  ║   │ ║   └──────────────────┘  ║     ║                         ║
│       │           │                          │ │    ║                       ║   │ ║                         ║     ║                         ║
│       │           │                          │ │    ║ ┌───────────────────┐ ║   │ ║   ┌──────────────────┐  ║     ║  ┌──────────────────┐   ║
│ ┌─────▼──┐    ┌───▼────┐     ┌──────────┐    │ │    ║ │                   ◀─╬───┘ ║   │                  │  ║     ║  │                  │   ║
│ │        │    │        │     │   Slot   │    │ │    ║ │      Webhook      │ ║     ║   │       Slot       │  ║     ║  │     JSP Tag      │   ║
│ │  Slot  │    │  Slot  │     │ (without ├────┘ │    ║ │     Processor     ├─╬─────╬───▶                  │  ║     ║  │<amp:ampSlot .../>│   ║
│ │        │    │        │     │ Edition) │      │    ║ │                   │ ║     ║   │                  │  ║     ║  │                  │   ║
│ └─────┬──┘    └───┬────┘     └─────┬────┘      │    ║ └─────────┬─────────┘ ║     ║   └────────▲─────────┘  ║     ║  └─────────┳────────┘   ║
│       │           │                │           │    ║           │           ║     ║            ┃            ║     ║            ┃            ║
│       │           │                │           │    ╚═══════════╬═══════════╝     ╚════════════╬════════════╝     ╚════════════╬════════════╝
│       │           │                │           │                │                              ┃                               ┃
│ ┌─────▼──┐    ┌───▼────┐     ┌─────▼────┐      │                │                              ┃                               ┃
│ │Content │    │Content │     │ Content  │      │                │                              ┃        include content        ┃
│ │  Item  │    │  Item  │     │   Item   │      │                │                              ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
│ │        │    │        │     │          │      │                │
│ └────────┘    └────────┘     └──────────┘      │                │
│ ┌────────┐    ┌────────┐     ┌──────────┐      │                │
│ │Content │    │Content │     │ Content  │      │                │
│ │  Item  │    │  Item  │     │   Item   │      │                │
│ │        │    │        │     │          │      │    ┌───────────▼───────────┐
│ └────────┘    └────────┘     └──────────┘      │    │                       │
│ ┌────────┐    ┌────────┐     ┌──────────┐      │    │                       │
│ │Content │    │Content │     │ Content  │      │    │                       │
│ │  Item  │    │  Item  │     │   Item   │      │    │   Amplience Content   │
│ │        │    │        │     │          │  ◀───┼────│   Rendering Service   │
│ └────────┘    └────────┘     └──────────┘      │    │                       │
│                                                │    │                       │
│                                                │    │                       │
└────────────────────────────────────────────────┘    └───────────────────────┘
```
## Explanation
* Content Items are created in Amplience and organised in Slots
* Optionally, Slots can be grouped and scheduled in Editions
* Actions in Amplience, as changes to Slots, cause events
* Events can trigger Webhooks
	* Create/Update: a (new) Content Item or Slot is created / updated
	* Publish: a version of a Content Items or Slot gets published
	* Edition Published: an Edition goes live
	* Archived: a Content Items or Slot gets archived
* Those Webhooks are configured on Amplience side
* Webhook requests are received by Hybris and queued for later processing
* Each webhook request has its own type (e.g. Update ...)
* Webhook processors handle the queued requests
	* Update:
		* Hybris only knows about Slots, not individual Content Items
		* The content for a Slot is retrieved as JSON and HTML
		* The HTML is rendered on Amplience side with the Content Rendering Service
		* The JSON includes the whole tree of childelements
		* HTML and JSON is localized
		* Lookup information (position, context, lookup, delivery key) are stored in Hybris
		* Different versions of Slots are supported
		* Information about Edition runtime are supported
	* Publish:
		* A specific version of a Slot is marked as published
		* No syncronization is needed
		* Special case: to be able to refresh content, when a Slot is already published, its content is downloaded again
	* Edition:
		* All referenced Slots get published
	* Archive:
		* Slots get deleted
* Cleanup of old Slot versions is performed
* The Hybris Frontend can include content:
	* A taglib supports JSON and HTML output
	* Fallback rules are possible
	* The ID of Slots can be used to query Amplience directly in HTML/JS

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
