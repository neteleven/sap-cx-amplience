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

import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;

public interface AmplienceContentService {

    /**
     * Publishes an Amplience Slot.
     *
     * @param id             ID of the Slot
     * @param editionId      ID of the Edition
     * @param webhookRequest the AmpWebhookRequestModel
     */
    void publishSlot(final String id, final String editionId, final AmpWebhookRequestModel webhookRequest);

    /**
     * Publishes an Amplience Slot.
     *
     * @param unpublishedSlot unpublished Slot
     * @param webhookRequest  the AmpWebhookRequestModel
     */
    void publishSlot(final AmpSlotModel unpublishedSlot, final AmpWebhookRequestModel webhookRequest);

    /**
     * Removes all found slots for Slot ID, Edition ID and published status but the last version.
     *
     * @param slotId    ID of the Slot
     * @param editionId ID of the Edition
     * @param published published status
     */
    void cleanup(String slotId, String editionId, boolean published);

    /**
     * Removes all Slots with ended editions.
     */
    void cleanupExpiredEditions();

    /**
     * Gets the active Amplience slot concerning the current date. Usable to retrieve unpublished slots.
     *
     * @param position  slot position
     * @param context   slot context
     * @param lookup    slot lookup
     * @param published published or not
     * @return the slot
     */
    AmpSlotModel getActiveSlot(final String position, final String context, final String lookup, final boolean published);

    /**
     * Gets the active and published Amplience slot concerning the current date.
     *
     * @param position slot position
     * @param context  slot context
     * @param lookup   slot lookup
     * @return the slot
     */
    AmpSlotModel getActiveSlot(final String position, final String context, final String lookup);

    /**
     * Gets the active Amplience slot concerning the current date. Usable to retrieve unpublished slots.
     *
     * @param deliveryKey delivery key
     * @param published   published or not
     * @return the slot
     */
    AmpSlotModel getActiveSlotByDeliveryKey(String deliveryKey, boolean published);

    /**
     * Gets the active Amplience slot concerning the current date.
     *
     * @param deliveryKey delivery key
     * @return the slot
     */
    AmpSlotModel getActiveSlotByDeliveryKey(String deliveryKey);

    /**
     * Fill the Slot for all configured Locale Mappings. Data is retrieved from the Amplience API.
     *
     * @param slot the Slot
     */
    void updateContent(final AmpSlotModel slot);
}
