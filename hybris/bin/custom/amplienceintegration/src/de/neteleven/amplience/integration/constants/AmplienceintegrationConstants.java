/*
 * Copyright (c) 2020. neteleven GmbH (https://www.neteleven.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.neteleven.amplience.integration.constants;

/**
 * Global class for all Amplienceintegration constants. You can add global constants for your extension into this class.
 */
public final class AmplienceintegrationConstants extends GeneratedAmplienceintegrationConstants {
    public static final String EXTENSIONNAME = "amplienceintegration";

    private AmplienceintegrationConstants() {
        //empty to avoid instantiating this constant class
    }

    public static class Webhook {

        public static class Request {
            public static final String ID = "amplience.json.webhook.request.id";
        }

        public static class Update {
            public static final String CONTENTTYPE = "amplience.json.webhook.update.contenttype";
            public static final String ID = "amplience.json.webhook.update.id";
            public static final String LABEL = "amplience.json.webhook.update.label";
            public static final String VERSION = "amplience.json.webhook.update.version";
            public static final String EDITION_ID = "amplience.json.webhook.update.editionid";
            public static final String EDITION_START = "amplience.json.webhook.update.editionstart";
            public static final String EDITION_END = "amplience.json.webhook.update.editionend";
            public static final String DELIVERY_KEY = "amplience.json.webhook.update.deliveryKey";
        }

        public static class Delete {
            public static final String ID = "amplience.json.webhook.delete.id";
        }

        public static class Publish {
            public static final String ID = "amplience.json.webhook.publish.id";
        }

        public static class Edition {
            public static final String ID = "amplience.json.webhook.edition.id";
        }

        public static class Content {
            public static final String POSITION = "amplience.json.slot.position";
            public static final String CONTEXT = "amplience.json.slot.context";
            public static final String LOOKUP = "amplience.json.slot.lookup";
        }
    }

}
