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

import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.neteleven.amplience.integration.dao.AmplienceSlotDAO;
import de.neteleven.amplience.integration.model.AmpLocaleMappingModel;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceAPIService;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class DefaultAmplienceContentService implements AmplienceContentService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceContentService.class);

    private final AmplienceSlotDAO contentDAO;
    private final AmplienceAPIService apiService;
    private final ModelService modelService;
    private final CMSSiteService cmsSiteService;
    private final TimeService timeService;
    private final CommonI18NService commonI18NService;

    public DefaultAmplienceContentService(final AmplienceSlotDAO contentDAO,
                                          final AmplienceAPIService apiService,
                                          final ModelService modelService,
                                          final CMSSiteService cmsSiteService,
                                          final TimeService timeService,
                                          final CommonI18NService commonI18NService) {
        this.contentDAO = contentDAO;
        this.apiService = apiService;
        this.modelService = modelService;
        this.cmsSiteService = cmsSiteService;
        this.timeService = timeService;
        this.commonI18NService = commonI18NService;
    }

    @Override
    public void publishSlot(final String slotId, final String editionId, final AmpWebhookRequestModel webhookRequest) {
        AmpSlotModel slot = contentDAO.getLatestSlotById(slotId, editionId, false);
        if (slot == null) {
            slot = contentDAO.getLatestSlotById(slotId, false);
        }
        publishSlot(slot, webhookRequest);

    }

    @Override
    public void publishSlot(final AmpSlotModel slot, final AmpWebhookRequestModel webhookRequest) {
        if (slot == null) {
            LOG.info("Slot for request must not be null - removing!");
            modelService.remove(webhookRequest);
            return;
        }

        if (slot.getPublished()) {
            updateContent(slot);
        } else {
            slot.setPublished(true);
        }
        modelService.save(slot);

        cleanup(slot.getId(), slot.getEditionId(), false);
        cleanup(slot.getId(), slot.getEditionId(), true);
        cleanupExpiredEditions();

        LOG.info("Slot for ID \"" + slot.getId() + "\" and Edition ID \"" + slot.getEditionId() + "\" published.");
    }

    @Override
    public void cleanup(final String slotId, final String editionId, final boolean published) {
        final List<AmpSlotModel> slots = new ArrayList<>(contentDAO.getAllSlotsById(slotId, editionId, published));
        if (slots.isEmpty()) {
            return;
        }
        final AmpSlotModel latest = slots.stream().max(Comparator.comparing(AmpSlotModel::getVersion)).get();
        slots.remove(latest);
        modelService.removeAll(slots);
    }

    @Override
    public void cleanupExpiredEditions() {
        final List<AmpSlotModel> slots = new ArrayList<>(contentDAO.getAllSlotsWithExpiredEditions(timeService.getCurrentTime()));
        if (slots.isEmpty()) {
            return;
        }
        modelService.removeAll(slots);
    }


    @Override
    public AmpSlotModel getActiveSlot(final String position, final String context, final String lookup, final boolean published) {
        final List<AmpSlotModel> slots = new ArrayList<>(contentDAO.getAllActiveSlots(position, context, lookup, published, cmsSiteService.getCurrentSite(), timeService.getCurrentTime()));

        return getLastSlot(slots);
    }

    @Override
    public AmpSlotModel getActiveSlot(final String position, final String context, final String lookup) {
        return getActiveSlot(position, context, lookup, true);
    }

    @Override
    public AmpSlotModel getActiveSlotByDeliveryKey(final String deliveryKey, final boolean published) {
        final List<AmpSlotModel> slots = new ArrayList<>(contentDAO.getAllActiveSlotsByDeliveryKey(deliveryKey, published, cmsSiteService.getCurrentSite(), timeService.getCurrentTime()));

        return getLastSlot(slots);
    }

    @Override
    public AmpSlotModel getActiveSlotByDeliveryKey(final String deliveryKey) {
        return getActiveSlotByDeliveryKey(deliveryKey, true);
    }

    @Override
    public void updateContent(final AmpSlotModel slot) {
        final AmpWebhookModel webhook = slot.getWebhook();
        final String apiVSE = webhook.getApiVSE();
        final String id = slot.getId();

        for (final AmpLocaleMappingModel localeMapping : webhook.getLocaleMappings()) {
            final String json = apiService.retrieveJsonForSlot(id, apiVSE, localeMapping.getAmpLocale());
            final String html = apiService.retrieveHtmlForSlot(id, apiVSE, webhook.getEndpoint(), slot.getContentType().getTemplate(), localeMapping.getAmpLocale());

            final Locale hybrisLocale = commonI18NService.getLocaleForIsoCode(localeMapping.getLocale());
            slot.setJson(json, hybrisLocale);
            slot.setHtml(html, hybrisLocale);

            LOG.debug("Filled slot \"" + id + "\" with content for locale (Amp->Hybris) " + localeMapping.getAmpLocale() + " -> " + localeMapping.getLocale());
        }
    }

    private AmpSlotModel getLastSlot(final List<AmpSlotModel> slots) {
        if (slots.isEmpty()) {
            return null;
        }

        slots.sort(Comparator.comparing(AmpSlotModel::getModifiedtime, Comparator.reverseOrder()));
        return slots.get(0);
    }

}
