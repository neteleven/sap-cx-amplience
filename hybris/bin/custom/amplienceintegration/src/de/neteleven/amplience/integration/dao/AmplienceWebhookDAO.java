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

package de.neteleven.amplience.integration.dao;

import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;

import java.util.List;

/**
 * Deals with Amplience Webhooks.
 */
public interface AmplienceWebhookDAO {
    /**
     * Find the Webhook configuration by its URI.
     *
     * @param webhookUri URI of the Webhook
     * @return AmpWebhookModel
     */
    AmpWebhookModel getWebhookByUri(final String webhookUri);

    /**
     * Find the Webhook Request by its ID.
     *
     * @param webhookRequestId ID of the Webhook Request
     * @return AmpWebhookRequestModel
     */
    AmpWebhookRequestModel getWebhookRequestById(final String webhookRequestId);

    /**
     * Finds all Webhook Requests.
     *
     * @return AmpWebhookRequestModel list
     */
    List<AmpWebhookRequestModel> getWebhookRequests();
}
