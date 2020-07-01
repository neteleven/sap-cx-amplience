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

import de.hybris.platform.servicelayer.model.ModelService;
import de.neteleven.amplience.integration.constants.AmplienceintegrationConstants;
import de.neteleven.amplience.integration.dao.AmplienceJsonDAO;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("publishAmplienceWebhookProcessor")
public class PublishAmplienceWebhookProcessor implements AmplienceWebhookProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(PublishAmplienceWebhookProcessor.class);

    private final AmplienceJsonDAO jsonDAO;
    private final ModelService modelService;
    private final AmplienceContentService contentService;

    public PublishAmplienceWebhookProcessor(final AmplienceJsonDAO jsonDAO,
                                            final ModelService modelService,
                                            final AmplienceContentService contentService) {
        this.jsonDAO = jsonDAO;
        this.modelService = modelService;
        this.contentService = contentService;
    }

    @Override
    public boolean processWebhookRequest(final AmpWebhookRequestModel webhookRequest) {
        Assert.notNull(webhookRequest, "Request must not be null!");

        final String json = webhookRequest.getJson();
        final String slotId = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Publish.ID);

        if (StringUtils.isEmpty(slotId)) {
            LOG.info("Content ID not found in request \"" + webhookRequest.getId() + "\" - removing!");
            modelService.remove(webhookRequest);
            return true;
        }

        contentService.publishSlot(slotId, null, webhookRequest);

        modelService.remove(webhookRequest);
        return true;
    }


}
