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

package de.neteleven.amplience.addon.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.neteleven.amplience.addon.AmplienceContentFacade;
import de.neteleven.amplience.data.AmpSlotData;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class DefaultAmplienceContentFacade implements AmplienceContentFacade {

    private final AmplienceContentService contentService;
    private final Converter<AmpSlotModel, AmpSlotData> amplienceSlotConverter;

    public DefaultAmplienceContentFacade(final AmplienceContentService contentService, @Qualifier("amplienceSlotConverter") final Converter<AmpSlotModel, AmpSlotData> amplienceSlotConverter) {
        this.contentService = contentService;
        this.amplienceSlotConverter = amplienceSlotConverter;
    }

    @Override
    public AmpSlotData getSlot(final String position, final String context, final String lookup, final boolean published) {
        final AmpSlotModel ampSlotModel = contentService.getActiveSlot(position, context, lookup, published);
        return getAmpSlotData(ampSlotModel);
    }

    @Override
    public AmpSlotData getSlotByKey(final String deliveryKey, final boolean published) {
        final AmpSlotModel ampSlotModel = contentService.getActiveSlotByDeliveryKey(deliveryKey, published);
        return getAmpSlotData(ampSlotModel);

    }

    private AmpSlotData getAmpSlotData(final AmpSlotModel ampSlotModel) {
        if (ampSlotModel != null) {
            final AmpSlotData ampSlotData = new AmpSlotData();
            amplienceSlotConverter.convert(ampSlotModel, ampSlotData);
            return ampSlotData;
        } else {
            return null;
        }
    }

}
