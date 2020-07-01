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

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.neteleven.amplience.integration.model.AmpSlotModel;

import java.util.Date;
import java.util.List;

/**
 * Deals with Amplience Slots.
 */
public interface AmplienceSlotDAO {
    /**
     * Finds the latest version Slot by its ID and Edition ID. It is possible to get only published Slots or all Slots.
     *
     * @param slotId        ID of the Slot
     * @param editionId     ID of the Edition
     * @param onlyPublished only return published Slots
     * @return AmpSlotModel
     */
    AmpSlotModel getLatestSlotById(final String slotId, final String editionId, final boolean onlyPublished);

    /**
     * Finds all versions Slot by its ID, Edition ID and published status.
     *
     * @param slotId    ID of the Slot
     * @param editionId ID of the Edition
     * @param published published status
     * @return List of AmpSlotModel
     */
    List<AmpSlotModel> getAllSlotsById(String slotId, String editionId, boolean published);

    /**
     * Finds the latest version Slot by its ID. It is possible to get only published Slots or all Slots.
     *
     * @param slotId        ID os the Slot
     * @param onlyPublished only return published Slots
     * @return AmpSlotModel
     */
    AmpSlotModel getLatestSlotById(final String slotId, final boolean onlyPublished);

    /**
     * Finds all Slot by its ID (for all Editions). It is possible to get only published Slots or all Slots.
     *
     * @param slotId        ID of the Slot
     * @param onlyPublished only return published Slots
     * @return List of AmpSlotModel
     */
    List<AmpSlotModel> getAllSlotsById(final String slotId, final boolean onlyPublished);

    /**
     * Finds all Slot by its Edition ID. It is possible to get only published Slots or all Slots.
     *
     * @param editionId     ID of the Edition
     * @param onlyPublished only return published Slots
     * @return List of AmpSlotModel
     */
    List<AmpSlotModel> getAllSlotsByEditionId(final String editionId, final boolean onlyPublished);

    /**
     * Finds all active Slots (edition start date optional or <= given date) by context information.
     *
     * @param position      the position
     * @param context       the context
     * @param lookup        the lookup
     * @param onlyPublished only return published Slots
     * @param cmssite       the CMSSite
     * @param date          the date
     * @return a list of active slots in this context
     */
    List<AmpSlotModel> getAllActiveSlots(final String position, final String context, final String lookup, final boolean onlyPublished, final CMSSiteModel cmssite, final Date date);

    /**
     * Finds all active Slots (edition start date optional or <= given date) by delivery key.
     *
     * @param deliveryKey   the delivery key
     * @param onlyPublished only return published Slots
     * @param cmssite       the CMSSite
     * @param date          the date
     * @return a list of active slots in this context
     */
    List<AmpSlotModel> getAllActiveSlotsByDeliveryKey(String deliveryKey, boolean onlyPublished, CMSSiteModel cmssite, Date date);

    /**
     * Finds all Slots, where the Edition end date is before the given date.
     *
     * @param date the given date
     * @return a list matching slots
     */
    List<AmpSlotModel> getAllSlotsWithExpiredEditions(Date date);

    /**
     * Finds a Slot by its ID and version.
     *
     * @param slotId  ID os the Slot
     * @param version version os the Slot
     * @return found Slot
     */
    AmpSlotModel getSlotByIdAndVersion(String slotId, Integer version);
}
