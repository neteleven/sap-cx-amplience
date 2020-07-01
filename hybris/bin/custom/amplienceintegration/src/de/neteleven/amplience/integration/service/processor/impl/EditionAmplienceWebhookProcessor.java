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
import de.neteleven.amplience.integration.dao.AmplienceSlotDAO;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("editionAmplienceWebhookProcessor")
public class EditionAmplienceWebhookProcessor implements AmplienceWebhookProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EditionAmplienceWebhookProcessor.class);

    private final AmplienceSlotDAO contentDAO;
    private final AmplienceJsonDAO jsonDAO;
    private final AmplienceContentService contentService;
    private final ModelService modelService;

    public EditionAmplienceWebhookProcessor(final AmplienceSlotDAO contentDAO,
                                            final AmplienceJsonDAO jsonDAO,
                                            final AmplienceContentService contentService,
                                            final ModelService modelService) {
        this.contentDAO = contentDAO;
        this.jsonDAO = jsonDAO;
        this.contentService = contentService;
        this.modelService = modelService;
    }

    @Override
    public boolean processWebhookRequest(final AmpWebhookRequestModel webhookRequest) {
        Assert.notNull(webhookRequest, "Request must not be null!");

        final String json = webhookRequest.getJson();
        final String editionId = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Edition.ID);

        if (StringUtils.isEmpty(editionId)) {
            LOG.info("Edition ID not found in request \"" + webhookRequest.getId() + "\" - removing!");
            modelService.remove(webhookRequest);
            return true;
        }

        for (final AmpSlotModel slot : contentDAO.getAllSlotsByEditionId(editionId, false)) {
            contentService.publishSlot(slot, webhookRequest);
        }

        LOG.info("Edition with ID  \"" + editionId + "\" published.");
        modelService.remove(webhookRequest);
        return true;
    }
}
