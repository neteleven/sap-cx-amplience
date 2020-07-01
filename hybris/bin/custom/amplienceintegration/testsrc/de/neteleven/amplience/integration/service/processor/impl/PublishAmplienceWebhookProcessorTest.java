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
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PublishAmplienceWebhookProcessorTest extends AmplienceWebhookProcessorTestBase {

    @Mock
    private AmplienceJsonDAO jsonDAO;
    @Mock
    private ModelService modelService;
    @Mock
    private AmplienceContentService contentService;

    @InjectMocks
    private PublishAmplienceWebhookProcessor systemUnderTest;

    @Test
    public void testProcessWebhookRequest() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.PUBLISH);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Publish.ID)).thenReturn(ID);
        systemUnderTest.processWebhookRequest(webhookRequest);

        Mockito.verify(contentService).publishSlot(ID, null, webhookRequest);
        Mockito.verify(modelService).remove(webhookRequest);
    }

    @Test
    public void testProcessWebhookRequestNoID() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.PUBLISH);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Publish.ID)).thenReturn(null);
        systemUnderTest.processWebhookRequest(webhookRequest);

        Mockito.verify(contentService, Mockito.never()).publishSlot(ID, null, webhookRequest);
        Mockito.verify(modelService).remove(webhookRequest);
    }

}
