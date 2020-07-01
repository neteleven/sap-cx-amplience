
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

package de.neteleven.amplience.addon;

import de.neteleven.amplience.data.AmpSlotData;

public interface AmplienceContentFacade {

    /**
     * Gets the slot for the context information
     *
     * @param position  the position
     * @param context   the context
     * @param lookup    the lookup
     * @param published if published
     * @return an AmpSlotData
     */
    AmpSlotData getSlot(final String position, final String context, final String lookup, final boolean published);

    /**
     * Gets the slot for the context information
     *
     * @param deliveryKey the delivery key
     * @param published   if published
     * @return an AmpSlotData
     */
    AmpSlotData getSlotByKey(final String deliveryKey, final boolean published);
}
