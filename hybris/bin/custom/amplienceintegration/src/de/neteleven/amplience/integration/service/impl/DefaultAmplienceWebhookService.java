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

package de.neteleven.amplience.integration.service.impl;

import de.hybris.platform.servicelayer.model.ModelService;
import de.neteleven.amplience.integration.constants.AmplienceintegrationConstants;
import de.neteleven.amplience.integration.dao.AmplienceJsonDAO;
import de.neteleven.amplience.integration.dao.AmplienceWebhookDAO;
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpContentTypeModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceWebhookService;
import de.neteleven.amplience.integration.utils.SignatureUtil;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultAmplienceWebhookService implements AmplienceWebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceWebhookService.class);

    private final ModelService modelService;
    private final AmplienceWebhookDAO webhookDAO;
    private final AmplienceJsonDAO jsonDAO;

    public DefaultAmplienceWebhookService(final ModelService modelService,
                                          final AmplienceWebhookDAO webhookDAO,
                                          final AmplienceJsonDAO jsonDAO) {
        this.modelService = modelService;
        this.webhookDAO = webhookDAO;
        this.jsonDAO = jsonDAO;
    }

    @Override
    public String getWebhookSecretByUri(final String webhookUri) {
        final AmpWebhookModel webhook = webhookDAO.getWebhookByUri(webhookUri);

        if (webhook != null) {
            return webhook.getSecret();
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public boolean checkWebhookSignature(final String webhookUri, final String hash, final String message) {
        final AmpWebhookModel webhook = webhookDAO.getWebhookByUri(webhookUri);
        if (BooleanUtils.isNotTrue(webhook.getValidateSecret())) {
            return true;
        }
        final String secret = getWebhookSecretByUri(webhookUri);
        return SignatureUtil.checkSignature(secret, hash, message);
    }

    @Override
    public boolean queueWebhook(final String webhookUri, final String message, final String type) {
        final AmpWebhookModel webhook = webhookDAO.getWebhookByUri(webhookUri);
        if (webhook == null || message == null || type == null) {
            return false;
        }
        try {
            final String requestId = jsonDAO.getElementFromJSON(message, AmplienceintegrationConstants.Webhook.Request.ID);

            getOrCreateWebhookRequest(requestId, message, WebhookType.valueOf(type.toUpperCase()), webhook);

            LOG.info("Created Webhook Request \"" + requestId + "\" for Webhook URI \"" + webhookUri + "\"");
            return true;
        } catch (final Exception ex) {
            LOG.error("Could not create WebhookRequest: ", ex);
            return false;
        }
    }

    @Override
    public AmpContentTypeModel getContentTypeIfMatches(final AmpWebhookRequestModel webhookRequest) {

        if (webhookRequest == null || webhookRequest.getWebhook() == null || webhookRequest.getType() == null) {
            return null;
        }

        final AmpWebhookModel webhook = webhookRequest.getWebhook();
        final String json = webhookRequest.getJson();
        final String contentType = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Update.CONTENTTYPE);

        for (final AmpContentTypeModel webhookContentType : webhook.getContentTypes()) {
            if (StringUtils.equals(contentType, webhookContentType.getUri())) {
                return webhookContentType;
            }
        }
        return null;
    }

    /**
     * Creates a Webhook Request in the DB if the ID does not exist. If it exists, update the content.
     *
     * @param requestId ID of the request
     * @param message   Webhook message
     * @param type      Webhook Type
     * @param webhook   Webhook model from Hybris
     */
    private void getOrCreateWebhookRequest(final String requestId, final String message, final WebhookType type, final AmpWebhookModel webhook) {
        final AmpWebhookRequestModel webhookRequest;
        AmpWebhookRequestModel lookup = null;

        try {
            lookup = webhookDAO.getWebhookRequestById(requestId);
        } catch (final Exception exception) {
            // nothing to do
        }

        if (lookup != null) {
            webhookRequest = lookup;
        } else {
            webhookRequest = modelService.create(AmpWebhookRequestModel.class);
        }

        webhookRequest.setId(requestId);
        webhookRequest.setType(type);
        webhookRequest.setWebhook(webhook);
        webhookRequest.setJson(message);
        modelService.save(webhookRequest);
    }

}
