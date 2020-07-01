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
import de.neteleven.amplience.integration.model.AmpContentTypeModel;
import de.neteleven.amplience.integration.model.AmpSlotModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.AmplienceAPIService;
import de.neteleven.amplience.integration.service.AmplienceContentService;
import de.neteleven.amplience.integration.service.AmplienceWebhookService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class UpdateAmplienceWebhookProcessorTest extends AmplienceWebhookProcessorTestBase {

    private static final String DATE = "1970-01-01T00:00:00Z";
    private static final String LABEL = "label";
    private static final String KEY = "key";

    @Mock
    private AmplienceJsonDAO jsonDAO;
    @Mock
    private AmplienceWebhookService webhookService;
    @Mock
    private AmplienceContentService contentService;
    @Mock
    private AmplienceAPIService apiService;
    @Mock
    private ModelService modelService;

    @Mock
    private AmplienceSlotDAO contentDAO;

    @Spy
    @InjectMocks
    private UpdateAmplienceWebhookProcessor systemUnderTest;

    @Test
    public void testProcessWebhookRequest() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        final AmpSlotModel slot = new AmpSlotModel();
        Mockito.when(webhookService.getContentTypeIfMatches(request)).thenReturn(contentType);
        Mockito.when(modelService.create(AmpSlotModel.class)).thenReturn(slot);
        mockJsonLookups();

        Mockito.doNothing().when(contentService).updateContent(slot);
        Mockito.doReturn(true).when(systemUnderTest).fillLookup(slot);

        final boolean result = systemUnderTest.processWebhookRequest(request);

        Assert.assertEquals(ID, slot.getId());
        Assert.assertEquals(LABEL, slot.getName());
        Assert.assertEquals(KEY, slot.getDeliveryKey());
        Assert.assertEquals(Integer.valueOf(1), slot.getVersion());
        Assert.assertFalse(slot.getPublished());
        Assert.assertEquals(contentType, slot.getContentType());
        Assert.assertEquals(request.getWebhook(), slot.getWebhook());
        Mockito.verify(modelService).save(slot);
        Mockito.verify(modelService).remove(request);
        Assert.assertTrue(result);
    }

    @Test
    public void testProcessWebhookRequestFillFailed() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        final AmpSlotModel slot = new AmpSlotModel();
        Mockito.when(webhookService.getContentTypeIfMatches(request)).thenReturn(contentType);
        Mockito.when(modelService.create(AmpSlotModel.class)).thenReturn(slot);
        mockJsonLookups();

        Mockito.doNothing().when(contentService).updateContent(slot);
        Mockito.doReturn(false).when(systemUnderTest).fillLookup(slot);

        final boolean result = systemUnderTest.processWebhookRequest(request);

        Mockito.verify(modelService, Mockito.never()).save(slot);
        Assert.assertFalse(result);
    }

    @Test
    public void testProcessWebhookRequestSlotNull() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        final AmpSlotModel slot = new AmpSlotModel();
        Mockito.when(webhookService.getContentTypeIfMatches(request)).thenReturn(contentType);
        Mockito.when(modelService.create(AmpSlotModel.class)).thenReturn(slot);
        mockJsonLookups();
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.ID)).thenReturn(null);

        final boolean result = systemUnderTest.processWebhookRequest(request);

        Mockito.verify(modelService, Mockito.never()).save(slot);
        Mockito.verify(modelService).remove(request);
        Assert.assertTrue(result);
    }

    @Test
    public void testProcessWebhookRequestVersionNull() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        final AmpSlotModel slot = new AmpSlotModel();
        Mockito.when(webhookService.getContentTypeIfMatches(request)).thenReturn(contentType);
        Mockito.when(modelService.create(AmpSlotModel.class)).thenReturn(slot);
        mockJsonLookups();
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.VERSION)).thenReturn(null);

        final boolean result = systemUnderTest.processWebhookRequest(request);

        Mockito.verify(modelService, Mockito.never()).save(slot);
        Mockito.verify(modelService).remove(request);
        Assert.assertTrue(result);
    }

    @Test
    public void testProcessWebhookRequestNoMatch() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(webhookService.getContentTypeIfMatches(request)).thenReturn(null);

        final boolean result = systemUnderTest.processWebhookRequest(request);

        Assert.assertTrue(result);
        Mockito.verify(modelService).remove(request);
    }

    @Test
    public void testFillEdition() {
        final AmpSlotModel slot = mockSlot();

        systemUnderTest.fillEdition(ID, DATE, DATE, slot);

        Mockito.verify(slot).setEditionId(ID);
        Mockito.verify(slot).setEditionStart((Date) Matchers.notNull());
        Mockito.verify(slot).setEditionEnd((Date) Matchers.notNull());
    }

    @Test
    public void testFillEditionStartNull() {
        final AmpSlotModel slot = mockSlot();

        systemUnderTest.fillEdition(ID, null, DATE, slot);

        Mockito.verify(slot).setEditionId(ID);
        Mockito.verify(slot).setEditionStart((Date) Matchers.isNull());
        Mockito.verify(slot).setEditionEnd((Date) Matchers.notNull());
    }

    @Test
    public void testFillEditionEndNull() {
        final AmpSlotModel slot = mockSlot();

        systemUnderTest.fillEdition(ID, DATE, null, slot);

        Mockito.verify(slot).setEditionId(ID);
        Mockito.verify(slot).setEditionStart((Date) Matchers.notNull());
        Mockito.verify(slot).setEditionEnd((Date) Matchers.isNull());
    }

    @Test
    public void testFillLookup() {
        final String value = "value";
        final AmpSlotModel slot = mockSlot();
        Mockito.when(apiService.retrieveJsonForSlot(Matchers.anyString(), Matchers.anyString(), Matchers.eq(null))).thenReturn(JSON);
        Mockito.when(jsonDAO.getElementFromJSON(Matchers.anyString(), Matchers.any())).thenReturn(value);

        final boolean result = systemUnderTest.fillLookup(slot);

        Assert.assertTrue(result);
        Mockito.verify(slot).setPosition(value);
        Mockito.verify(slot).setContext(value);
        Mockito.verify(slot).setLookup(value);
    }

    @Test
    public void testFillLookupNoJson() {
        final AmpSlotModel slot = mockSlot();
        Mockito.when(apiService.retrieveJsonForSlot(Matchers.anyString(), Matchers.anyString(), Matchers.eq(null))).thenReturn(null);

        final boolean result = systemUnderTest.fillLookup(slot);

        Assert.assertFalse(result);
    }

    @Test
    public void testFillLookupEmptyLookup() {
        final AmpSlotModel slot = mockSlot();
        Mockito.when(apiService.retrieveJsonForSlot(Matchers.anyString(), Matchers.anyString(), Matchers.eq(null))).thenReturn(JSON);
        Mockito.when(jsonDAO.getElementFromJSON(Matchers.anyString(), Matchers.any())).thenReturn(null);

        final boolean result = systemUnderTest.fillLookup(slot);

        Assert.assertFalse(result);
    }

    @Test
    public void testGetAmpSlotModel() {
        Mockito.when(contentDAO.getSlotByIdAndVersion(ID, 1)).thenReturn(null);

        systemUnderTest.getAmpSlotModel(ID, 1);

        Mockito.verify(modelService).create(AmpSlotModel.class);
    }

    @Test
    public void testGetAmpSlotModelFound() {
        final AmpSlotModel slot = getSlot(ID, false);
        Mockito.when(contentDAO.getSlotByIdAndVersion(ID, 1)).thenReturn(slot);

        final AmpSlotModel result = systemUnderTest.getAmpSlotModel(ID, 1);

        Mockito.verify(modelService, Mockito.never()).create(AmpSlotModel.class);
        Assert.assertEquals(slot, result);
    }

    private AmpSlotModel mockSlot() {
        final AmpSlotModel slot = Mockito.mock(AmpSlotModel.class);
        final AmpWebhookModel webhook = getWebhookModel("uri", "secret", false);
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        webhook.setApiVSE(API);
        contentType.setTemplate("template");
        Mockito.when(slot.getContentType()).thenReturn(contentType);
        Mockito.when(slot.getWebhook()).thenReturn(webhook);
        return slot;
    }

    private void mockJsonLookups() {
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.ID)).thenReturn(ID);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.LABEL)).thenReturn(LABEL);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.VERSION)).thenReturn(1);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.EDITION_ID)).thenReturn(null);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.EDITION_START)).thenReturn(DATE);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.EDITION_END)).thenReturn(DATE);
        Mockito.when(jsonDAO.getElementFromJSON(JSON, AmplienceintegrationConstants.Webhook.Update.DELIVERY_KEY)).thenReturn(KEY);
    }

}
