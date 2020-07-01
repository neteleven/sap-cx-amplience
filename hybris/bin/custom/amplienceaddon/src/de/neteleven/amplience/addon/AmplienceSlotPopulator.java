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

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.neteleven.amplience.data.AmpSlotData;
import de.neteleven.amplience.integration.model.AmpSlotModel;

public class AmplienceSlotPopulator implements Populator<AmpSlotModel, AmpSlotData> {

    @Override
    public void populate(final AmpSlotModel ampSlotModel, final AmpSlotData ampSlotData) throws ConversionException {
        ampSlotData.setPosition(ampSlotModel.getPosition());
        ampSlotData.setContext(ampSlotModel.getContext());
        ampSlotData.setLookup(ampSlotModel.getLookup());
        ampSlotData.setJson(ampSlotModel.getJson());
        ampSlotData.setHtml(ampSlotModel.getHtml());
    }
}
