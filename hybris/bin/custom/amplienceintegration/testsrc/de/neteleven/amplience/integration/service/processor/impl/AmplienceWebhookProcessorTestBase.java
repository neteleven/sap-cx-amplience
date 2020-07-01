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

package de.neteleven.amplience.integration.service.processor.impl;

import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpLocaleMappingModel;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;

import java.util.Collections;

public class AmplienceWebhookProcessorTestBase {

    static final String JSON = "json";
    static final String HTML = "html";
    static final String ID = "id";
    static final String API = "api";

    AmpWebhookModel getWebhookModel(final String uri, final String secret, final boolean validate) {
        final AmpWebhookModel webhook = new AmpWebhookModel();
        webhook.setUri(uri);
        webhook.setSecret(secret);
        webhook.setValidateSecret(validate);

        final AmpLocaleMappingModel mapping = new AmpLocaleMappingModel();
        mapping.setAmpLocale("en-GB");
        mapping.setLocale("en");
        webhook.setLocaleMappings(Collections.singletonList(mapping));

        return webhook;
    }

    AmpWebhookRequestModel getWebhookRequestModel(final WebhookType type) {
        final AmpWebhookRequestModel webhookRequestModel = new AmpWebhookRequestModel();
        webhookRequestModel.setWebhook(getWebhookModel("uri", "secret", true));
        webhookRequestModel.setType(type);
        webhookRequestModel.setJson(JSON);
        return webhookRequestModel;
    }

    AmpSlotModel getSlot(final String id, final boolean published) {
        final AmpSlotModel slot = new AmpSlotModel();
        slot.setId(id);
        slot.setPublished(published);
        return slot;
    }
}
