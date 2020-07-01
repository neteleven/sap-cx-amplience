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
import de.neteleven.amplience.integration.model.AmpContentTypeModel;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceAPIService;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import de.neteleven.amplience.integration.service.AmplienceWebhookService;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;

@Service("updateAmplienceWebhookProcessor")
public class UpdateAmplienceWebhookProcessor implements AmplienceWebhookProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateAmplienceWebhookProcessor.class);

    private final AmplienceJsonDAO jsonDAO;
    private final AmplienceWebhookService webhookService;
    private final AmplienceAPIService apiService;
    private final AmplienceContentService contentService;
    private final ModelService modelService;
    private final AmplienceSlotDAO contentDAO;

    public UpdateAmplienceWebhookProcessor(final AmplienceJsonDAO jsonDAO,
                                           final AmplienceWebhookService webhookService,
                                           final AmplienceAPIService apiService,
                                           final AmplienceContentService contentService,
                                           final ModelService modelService,
                                           final AmplienceSlotDAO contentDAO) {
        this.jsonDAO = jsonDAO;
        this.webhookService = webhookService;
        this.apiService = apiService;
        this.contentService = contentService;
        this.modelService = modelService;
        this.contentDAO = contentDAO;
    }

    @Override
    public boolean processWebhookRequest(final AmpWebhookRequestModel webhookRequest) {
        Assert.notNull(webhookRequest, "Request must not be null!");

        final AmpContentTypeModel contentType = webhookService.getContentTypeIfMatches(webhookRequest);
        if (contentType == null) {
            LOG.info("Content type of webhook request \"" + webhookRequest.getId() + "\" did not match allowed content types - removing!");
            modelService.remove(webhookRequest);
            return true;
        }
        return createSlotVersion(webhookRequest, contentType);
    }

    private boolean createSlotVersion(final AmpWebhookRequestModel webhookRequest, final AmpContentTypeModel contentType) {
        final AmpWebhookModel webhook = webhookRequest.getWebhook();
        final String webhookRequestJson = webhookRequest.getJson();

        final String slotId = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.ID);
        final String label = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.LABEL);
        final Integer version = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.VERSION);
        final String editionId = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.EDITION_ID);
        final String editionStart = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.EDITION_START);
        final String editionEnd = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.EDITION_END);
        final String deliveryKey = jsonDAO.getElementFromJSON(webhookRequestJson, AmplienceintegrationConstants.Webhook.Update.DELIVERY_KEY);

        if (StringUtils.isEmpty(slotId) || version == null) {
            LOG.info("Content ID or Version not found in request \"" + webhookRequest.getId() + "\" - removing!");
            modelService.remove(webhookRequest);
            return true;
        }

        final AmpSlotModel slot = getAmpSlotModel(slotId, version);
        slot.setId(slotId);

        fillEdition(editionId, editionStart, editionEnd, slot);

        slot.setName(label);
        slot.setVersion(version);
        slot.setDeliveryKey(deliveryKey);
        slot.setPublished(false);
        slot.setContentType(contentType);
        slot.setWebhook(webhook);

        if (!fillLookup(slot)) {
            return false;
        }
        contentService.updateContent(slot);

        modelService.save(slot);

        LOG.info("Slot \"" + slotId + "\" created/updated from request \"" + webhookRequest.getId() + "\" - removing request!");
        modelService.remove(webhookRequest);
        return true;

    }

    /**
     * Fills information about the Edition.
     *
     * @param editionId    ID of the Edition
     * @param editionStart Start Date as Sting
     * @param editionEnd   End Date as String
     * @param slot         the Slot
     */
    protected void fillEdition(final String editionId, final String editionStart, final String editionEnd, final AmpSlotModel slot) {
        slot.setEditionId(editionId);

        Date startDate = null;
        Date endDate = null;
        if (StringUtils.isNotEmpty(editionStart)) {
            startDate = new DateTime(editionStart).toDate();
        }
        if (StringUtils.isNotEmpty(editionEnd)) {
            endDate = new DateTime(editionEnd).toDate();
        }

        slot.setEditionStart(startDate);
        slot.setEditionEnd(endDate);
    }

    /**
     * Fills mandatory lookup information. Data is retrieved from the Amplinece API.
     *
     * @param slot the Slot
     * @return false when no information in the JSON is found
     */
    protected boolean fillLookup(final AmpSlotModel slot) {
        final String json = apiService.retrieveJsonForSlot(slot.getId(), slot.getWebhook().getApiVSE(), null);

        if (StringUtils.isEmpty(json)) {
            return false;
        }

        final String position = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Content.POSITION);
        final String context = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Content.CONTEXT);
        final String lookup = jsonDAO.getElementFromJSON(json, AmplienceintegrationConstants.Webhook.Content.LOOKUP);

        if (StringUtils.isEmpty(position) || StringUtils.isEmpty(context) || StringUtils.isEmpty((lookup))) {
            return false;
        }

        slot.setPosition(position);
        slot.setContext(context);
        slot.setLookup(lookup);

        return true;
    }

    /**
     * Get or create Slot.
     *
     * @param slotId  Slot ID
     * @param version version
     * @return found or new AmpSlotModel
     */
    protected AmpSlotModel getAmpSlotModel(final String slotId, final Integer version) {
        final AmpSlotModel slot;
        AmpSlotModel lookup = null;

        try {
            lookup = contentDAO.getSlotByIdAndVersion(slotId, version);
        } catch (final Exception exception) {
            // nothing to do
        }

        if (lookup != null) {
            slot = lookup;
        } else {
            slot = modelService.create(AmpSlotModel.class);
        }
        return slot;
    }

}
