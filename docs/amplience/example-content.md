# Example Content

This is some example content, that has been created using our provided schemas and templates.

## JSON API

https://abcdef01234567890abcdef01.staging.bigcontent.io/content/id/4d7493d0-965b-4d6b-b618-38da4806a651?depth=all&format=inlined

```
{
  "content": {
    "_meta": {
      "name": "slot-example",
      "schema": "https://raw.githubusercontent.com/neteleven/sap-cx-amplience/master/amplience/schemas/slot-example.json",
      "deliveryId": "4d7493d0-965b-4d6b-b618-38da4806a651"
    },
    "_environment": {
      "slot": {
        "position": "main-content-slot",
        "context": "page",
        "lookup": "example-page"
      }
    },
    "content": [
      {
        "_meta": {
          "name": "content-example",
          "schema": "https://raw.githubusercontent.com/neteleven/sap-cx-amplience/master/amplience/schemas/content-example.json",
          "deliveryId": "0bd87e11-8888-48dd-a8f8-c61acbb266db"
        },
        "text": "# Content example #1\n\nThis is content example no. 1, it is very beautiful."
      },
      {
        "_meta": {
          "name": "content-example-2",
          "schema": "https://raw.githubusercontent.com/neteleven/sap-cx-amplience/master/amplience/schemas/content-example.json",
          "deliveryId": "c4178337-9ca7-460a-8979-d8c7e60024e7"
        },
        "text": "## Content example #2\n\nAnd here comes content example no. 2 with some formatted text like **bold**, _italic_ and a list with\n\n*   one\n*   two\n*   three\n\nelements."
      }
    ]
  }
}
```

## Content Rendering Service

http://abcdef01234567890abcdef01.staging.bigcontent.io/v1/content/neteleven/content-item/4d7493d0-965b-4d6b-b618-38da4806a651?template=content_slot_template

```
<div id="main-content-container">
	<section class="section-row">
		<div class="section-text">
			<h1>Content example #1</h1>
			<p>This is content example no. 1, it is very beautiful.</p>
		</div>
	</section>
	<section class="section-row">
		<div class="section-text">
			<h2>Content example #2</h2>
			<p>And here comes content example no. 2 with some formatted text like <strong>bold</strong>, <em>italic</em> and a list with</p>
			<ul>
				<li>one</li>
				<li>two</li>
				<li>three</li>
			</ul>
			<p>elements.</p>
		</div>
	</section>
</div>
```

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
