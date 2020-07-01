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
import de.neteleven.amplience.integration.dao.AmplienceWebhookDAO;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("deleteAmplienceWebhookProcessor")
public class DeleteAmplienceWebhookProcessor implements AmplienceWebhookProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteAmplienceWebhookProcessor.class);

    private final AmplienceWebhookDAO webhookDAO;
    private final AmplienceSlotDAO contentDAO;
    private final ModelService modelService;
    private final AmplienceJsonDAO jsonDAO;

    public DeleteAmplienceWebhookProcessor(final AmplienceWebhookDAO webhookDAO,
                                           final AmplienceSlotDAO contentDAO,
                                           final ModelService modelService,
                                           final AmplienceJsonDAO jsonDAO) {
        this.webhookDAO = webhookDAO;
        this.contentDAO = contentDAO;
        this.modelService = modelService;
        this.jsonDAO = jsonDAO;
    }

    @Override
    public boolean processWebhookRequest(final AmpWebhookRequestModel webhookRequest) {
        Assert.notNull(webhookRequest, "Request must not be null!");

        final String json = webhookRequest.getJson();
        final String slotId = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Delete.ID);

        if (StringUtils.isEmpty(slotId)) {
            LOG.info("Content ID not found in request \"" + webhookRequest.getId() + "\" - removing!");
            modelService.remove(webhookRequest);
            return true;
        }

        for (final AmpSlotModel publishedSlot : contentDAO.getAllSlotsById(slotId, true)) {
            modelService.remove(publishedSlot);
            LOG.info("Removed published Slot \"" + slotId + "\"");
        }

        for (final AmpSlotModel unpublishedSlot : contentDAO.getAllSlotsById(slotId, false)) {
            modelService.remove(unpublishedSlot);
            LOG.info("Removed unpublished Slot \"" + slotId + "\"");
        }

        modelService.remove(webhookRequest);
        return true;
    }
}
