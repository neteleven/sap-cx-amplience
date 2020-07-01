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

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neteleven.amplience.integration.constants.AmplienceintegrationConstants;
import de.neteleven.amplience.integration.dao.AmplienceJsonDAO;
import de.neteleven.amplience.integration.dao.AmplienceSlotDAO;
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DeleteAmplienceWebhookProcessorTest extends AmplienceWebhookProcessorTestBase {

    @Mock
    private AmplienceJsonDAO jsonDAO;
    @Mock
    private AmplienceSlotDAO contentDAO;
    @Mock
    private ModelService modelService;

    @InjectMocks
    private DeleteAmplienceWebhookProcessor systemUnderTest;


    @Before
    public void setUp() {
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Delete.ID)).thenReturn(ID);
    }

    @Test
    public void testProcessWebhookRequest() {
        final AmpSlotModel publishedSlot = getSlot(ID, true);
        final AmpSlotModel unpublishedSlot = getSlot(ID, false);
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.DELETE);
        Mockito.when(contentDAO.getAllSlotsById(ID, true)).thenReturn(Arrays.asList(publishedSlot));
        Mockito.when(contentDAO.getAllSlotsById(ID, false)).thenReturn(Arrays.asList(unpublishedSlot));

        systemUnderTest.processWebhookRequest(webhookRequest);

        Mockito.verify(modelService).remove(publishedSlot);
        Mockito.verify(modelService).remove(unpublishedSlot);
        Mockito.verify(modelService).remove(webhookRequest);
    }

    @Test
    public void testProcessWebhookRequestEmptyID() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.DELETE);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Delete.ID)).thenReturn(null);

        systemUnderTest.processWebhookRequest(webhookRequest);

        Mockito.verify(modelService).remove(webhookRequest);
    }

}
