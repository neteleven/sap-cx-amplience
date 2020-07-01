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

package de.neteleven.amplience.integration.service;

import de.neteleven.amplience.integration.model.AmpContentTypeModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;

/**
 * Service to deal with Amplience Webhooks.
 */
public interface AmplienceWebhookService {
    /**
     * Returns the stored secret of a Webhook by a lookup of the webhook URI.
     *
     * @param webhookUri the Webhook URI
     * @return the secret or empty String, if Wehbook was not found
     */
    String getWebhookSecretByUri(final String webhookUri);

    /**
     * Validates the webhook message against its hash. The secret is loaded from the Webhook in the DB
     * (that is looked up by its URI). When validation is turned off, the check always succeeds.
     *
     * @param webhookUri the Webhook URI
     * @param hash       the have value to check against
     * @param message    the Webhooks message
     * @return true is validation succeeded
     */
    boolean checkWebhookSignature(final String webhookUri, final String hash, final String message);

    /**
     * Writes Webhook Requests to the DB for later processing.
     *
     * @param webhookUri the Webhook URI
     * @param message    the Webhooks message
     * @param type       the Type
     * @return true if successfull
     */
    boolean queueWebhook(final String webhookUri, final String message, final String type);

    /**
     * Checks, if the content type in the request matches the allowed ones from the webhook. When it matches,
     * the AmpContentTypeModel is returned
     *
     * @param webhookRequest the request.
     * @return AmpContentTypeModel if it matches, null if not
     */
    AmpContentTypeModel getContentTypeIfMatches(AmpWebhookRequestModel webhookRequest);
}
