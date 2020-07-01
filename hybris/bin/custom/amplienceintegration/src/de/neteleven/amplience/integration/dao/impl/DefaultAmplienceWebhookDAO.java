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

package de.neteleven.amplience.integration.dao.impl;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.neteleven.amplience.integration.dao.AmplienceWebhookDAO;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultAmplienceWebhookDAO implements AmplienceWebhookDAO {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceWebhookDAO.class);

    private final FlexibleSearchService flexibleSearchService;

    public DefaultAmplienceWebhookDAO(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Override
    public AmpWebhookModel getWebhookByUri(final String webhookUri) {
        final Map<String, String> params = new HashMap<>();
        params.put("uri", webhookUri);

        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {AmpWebhook} WHERE {uri} = ?uri", params);
        try {
            return flexibleSearchService.searchUnique(query);
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Webhook for URI \"" + webhookUri + "\" not found.");
            return null;
        }
    }

    @Override
    public AmpWebhookRequestModel getWebhookRequestById(final String webhookRequestId) {
        final Map<String, String> params = new HashMap<>();
        params.put("id", webhookRequestId);

        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {AmpWebhookRequest} WHERE {id} = ?id", params);
        try {
            return flexibleSearchService.searchUnique(query);
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Webhook Request for ID \"" + webhookRequestId + "\" not found.");
            return null;
        }
    }

    @Override
    public List<AmpWebhookRequestModel> getWebhookRequests() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery("SELECT {pk} FROM {AmpWebhookRequest} ORDER BY {CREATIONTIME} ASC");
        return flexibleSearchService.<AmpWebhookRequestModel>search(query).getResult();
    }
}
