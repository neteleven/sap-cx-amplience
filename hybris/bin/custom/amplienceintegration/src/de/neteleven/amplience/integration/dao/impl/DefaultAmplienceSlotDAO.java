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

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.neteleven.amplience.integration.dao.AmplienceSlotDAO;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DefaultAmplienceSlotDAO implements AmplienceSlotDAO {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceSlotDAO.class);

    private static final String QUERY_ALL_SLOTS_BY_ID = "SELECT {pk} FROM {AmpSlot} WHERE {id} = ?id";
    private static final String QUERY_ALL_SLOTS_BY_ID_VERSION = "SELECT {pk} FROM {AmpSlot} WHERE {id} = ?id AND {version} = ?version";
    private static final String QUERY_ALL_SLOTS_BY_EDITION = "SELECT {pk} FROM {AmpSlot} WHERE {editionId} = ?id";
    private static final String PUBLISHED = " AND {published} = ?published";
    private static final String EDITION_NULL = " AND {editionId} IS NULL";
    private static final String EDITION_NOT_NULL = " AND {editionId} = ?editionId";
    private static final String QUERY_ALL_ACTIVE_SLOTS = "SELECT {s:PK} FROM {AmpSlot as s JOIN AmpWebhooks2CMSSites as rel " +
            "ON {s:webhook} = {rel:source} JOIN CMSSite AS cs ON {rel:target} = {cs:PK}} " +
            "WHERE {cs:PK} = ?cmssite AND {s:position} = ?position AND {s.context} = ?context AND {lookup} = ?lookup " +
            "AND ({s:editionStart} IS NULL OR ({s:editionStart} <= ?date AND ({s.editionEnd} IS NULL OR {s.editionEnd} > ?date)))";
    private static final String QUERY_ALL_ACTIVE_SLOTS_DELIVERY_KEY = "SELECT {s:PK} FROM {AmpSlot as s JOIN AmpWebhooks2CMSSites as rel " +
            "ON {s:webhook} = {rel:source} JOIN CMSSite AS cs ON {rel:target} = {cs:PK}} " +
            "WHERE {cs:PK} = ?cmssite AND {s:deliveryKey} = ?deliveryKey AND ({s:editionStart} IS NULL OR " +
            "({s:editionStart} <= ?date AND ({s.editionEnd} IS NULL OR {s.editionEnd} > ?date)))";
    private static final String QUERY_ALL_SLOTS_EXPIRED_EDITIONS = "SELECT {pk} FROM {AmpSlot} WHERE {editionEnd} < ?date";


    private final FlexibleSearchService flexibleSearchService;

    public DefaultAmplienceSlotDAO(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    @Override
    public AmpSlotModel getLatestSlotById(final String slotId, final String editionId, final boolean onlyPublished) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", slotId);
        params.put("editionId", editionId);
        params.put("published", true);

        final String queryString = QUERY_ALL_SLOTS_BY_ID
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + (StringUtils.isEmpty(editionId) ? EDITION_NULL : EDITION_NOT_NULL)
                + " ORDER BY {version} DESC LIMIT 1";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);
        try {
            return flexibleSearchService.searchUnique(query);
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Slot for ID \"" + slotId + "\", Edition ID \"" + editionId + "\", published=" + onlyPublished + " not found.");
            return null;
        }
    }

    @Override
    public List<AmpSlotModel> getAllSlotsById(final String slotId, final String editionId, final boolean published) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", slotId);
        params.put("editionId", editionId);
        params.put("published", published);

        final String queryString = QUERY_ALL_SLOTS_BY_ID
                + (published ? PUBLISHED : StringUtils.EMPTY)
                + (StringUtils.isEmpty(editionId) ? EDITION_NULL : EDITION_NOT_NULL)
                + " ORDER BY {version}";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);
        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Slot for ID \"" + slotId + "\", Edition ID \"" + editionId + "\", published=" + published + " not found.");
            return Collections.emptyList();
        }
    }

    @Override
    public AmpSlotModel getLatestSlotById(final String slotId, final boolean onlyPublished) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", slotId);
        params.put("published", true);

        final String queryString = QUERY_ALL_SLOTS_BY_ID
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + " ORDER BY {version} DESC LIMIT 1";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);
        try {
            return flexibleSearchService.searchUnique(query);
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Slot for ID \"" + slotId + "\", published=" + onlyPublished + " not found.");
            return null;
        }
    }

    @Override
    public List<AmpSlotModel> getAllSlotsById(final String slotId, final boolean onlyPublished) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", slotId);
        params.put("published", true);

        final String queryString = QUERY_ALL_SLOTS_BY_ID
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + " ORDER BY {version} DESC";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);
        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("No Slots for ID \"" + slotId + "\", published=" + onlyPublished + " found.");
            return Collections.emptyList();
        }
    }

    @Override
    public List<AmpSlotModel> getAllSlotsByEditionId(final String editionId, final boolean onlyPublished) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", editionId);
        params.put("published", true);

        final String queryString = QUERY_ALL_SLOTS_BY_EDITION
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + " ORDER BY {version} DESC";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);
        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("No Slots for Edition ID \"" + editionId + "\", published=" + onlyPublished + " found.");
            return Collections.emptyList();
        }
    }

    @Override
    public List<AmpSlotModel> getAllActiveSlots(final String position, final String context, final String lookup, final boolean onlyPublished, final CMSSiteModel cmssite, final Date date) {
        final Map<String, Object> params = new HashMap<>();
        params.put("position", position);
        params.put("context", context);
        params.put("lookup", lookup);
        params.put("cmssite", cmssite);
        params.put("date", date);
        params.put("published", true);

        final String queryString = QUERY_ALL_ACTIVE_SLOTS
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + " ORDER BY {version}";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);

        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("No Slots for position \"" + position + "\", " +
                    "context \"" + context + "\", " +
                    "lookup \"" + lookup + "\", " +
                    "published \"" + onlyPublished + "\"," +
                    "cmssite \"" + cmssite.getUid() + "\"" +
                    "for date \"" + date + "\"" + "found.");
            return Collections.emptyList();
        }
    }

    @Override
    public List<AmpSlotModel> getAllActiveSlotsByDeliveryKey(final String deliveryKey, final boolean onlyPublished, final CMSSiteModel cmssite, final Date date) {
        final Map<String, Object> params = new HashMap<>();
        params.put("deliveryKey", deliveryKey);
        params.put("cmssite", cmssite);
        params.put("date", date);
        params.put("published", true);

        final String queryString = QUERY_ALL_ACTIVE_SLOTS_DELIVERY_KEY
                + (onlyPublished ? PUBLISHED : StringUtils.EMPTY)
                + " ORDER BY {version}";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString, params);

        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("No Slots for deliveyKey \"" + deliveryKey + "\", " +
                    "published \"" + onlyPublished + "\"," +
                    "cmssite \"" + cmssite.getUid() + "\"" +
                    "for date \"" + date + "\"" + "found.");
            return Collections.emptyList();
        }
    }

    @Override
    public List<AmpSlotModel> getAllSlotsWithExpiredEditions(final Date date) {
        final Map<String, Object> params = new HashMap<>();
        params.put("date", date);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_ALL_SLOTS_EXPIRED_EDITIONS, params);
        try {
            return flexibleSearchService.<AmpSlotModel>search(query).getResult();
        } catch (final ModelNotFoundException modelNotFoundException) {
            return Collections.emptyList();
        }
    }

    @Override
    public AmpSlotModel getSlotByIdAndVersion(final String slotId, final Integer version) {
        final Map<String, Object> params = new HashMap<>();
        params.put("id", slotId);
        params.put("version", version);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(QUERY_ALL_SLOTS_BY_ID_VERSION, params);
        try {
            return flexibleSearchService.searchUnique(query);
        } catch (final ModelNotFoundException modelNotFoundException) {
            LOG.debug("Slot for ID \"" + slotId + "\" and version \"" + version + "\" not found.");
            return null;
        }
    }

}
