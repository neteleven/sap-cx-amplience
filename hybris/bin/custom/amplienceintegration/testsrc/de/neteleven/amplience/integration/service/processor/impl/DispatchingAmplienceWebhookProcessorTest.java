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
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DispatchingAmplienceWebhookProcessorTest extends AmplienceWebhookProcessorTestBase {

    @Mock
    private AmplienceWebhookProcessor updateAmplienceWebhookProcessor;
    @Mock
    private AmplienceWebhookProcessor publishAmplienceWebhookProcessor;
    @Mock
    private AmplienceWebhookProcessor deleteAmplienceWebhookProcessor;
    @Mock
    private AmplienceWebhookProcessor editionAmplienceWebhookProcessor;

    private DispatchingAmplienceWebhookProcessor systemUnderTest;

    @Before
    public void setUp() {
        systemUnderTest = new DispatchingAmplienceWebhookProcessor(updateAmplienceWebhookProcessor,
                publishAmplienceWebhookProcessor,
                deleteAmplienceWebhookProcessor,
                editionAmplienceWebhookProcessor);
    }

    @Test
    public void testProcessWebhookRequestUpdate() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);
        systemUnderTest.processWebhookRequest(webhookRequest);
        Mockito.verify(updateAmplienceWebhookProcessor).processWebhookRequest(webhookRequest);
    }

    @Test
    public void testProcessWebhookRequestPublish() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.PUBLISH);
        systemUnderTest.processWebhookRequest(webhookRequest);
        Mockito.verify(publishAmplienceWebhookProcessor).processWebhookRequest(webhookRequest);
    }

    @Test
    public void testProcessWebhookRequestDelete() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.DELETE);
        systemUnderTest.processWebhookRequest(webhookRequest);
        Mockito.verify(deleteAmplienceWebhookProcessor).processWebhookRequest(webhookRequest);
    }

    @Test
    public void testProcessWebhookRequestEdition() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.EDITION);
        systemUnderTest.processWebhookRequest(webhookRequest);
        Mockito.verify(editionAmplienceWebhookProcessor).processWebhookRequest(webhookRequest);
    }
}
