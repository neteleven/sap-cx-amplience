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

package de.neteleven.amplience.integration.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;
import de.neteleven.amplience.integration.dao.AmplienceSlotDAO;
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.*;
import de.neteleven.amplience.integration.service.AmplienceAPIService;
import org.hamcrest.MatcherAssert;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAmplienceContentServiceTest {

    private static final String SLOT_1_ID = "1";
    private static final String SLOT_2_ID = "2";
    private static final String SLOT_3_ID = "3";
    private static final String SLOT_POSITION = "position";
    private static final String SLOT_CONTEXT = "context";
    private static final String SLOT_LOOKUP = "lookup";
    private static final String LOCALE = "en";
    private static final String JSON = "json";
    private static final String HTML = "html";
    private static final String API = "sandbox.cdn.content.amplience.net";
    private static final String ENDPOINT = "endpoint";
    private static final String AMP_LOCALE = "en-GB";
    private static final String KEY = "key";


    @Mock
    private AmplienceSlotDAO contentDAO;
    @Mock
    private ModelService modelService;
    @Mock
    private AmplienceAPIService apiService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private CMSSiteService cmsSiteService;
    @Mock
    private TimeService timeService;

    @Captor
    private ArgumentCaptor<ArrayList<AmpSlotModel>> captor;

    @InjectMocks
    private DefaultAmplienceContentService systemUnderTest;


    @Test
    public void testNoActiveSlots() {
        final List<AmpSlotModel> activeSlotList = new ArrayList<>();
        Mockito.when(contentDAO.getAllActiveSlots(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(), Mockito.any())).thenReturn(activeSlotList);

        final AmpSlotModel result = systemUnderTest.getActiveSlot(SLOT_POSITION, SLOT_CONTEXT, SLOT_LOOKUP);

        Assert.assertNull(result);
        Mockito.verify(contentDAO).getAllActiveSlots(Matchers.eq(SLOT_POSITION), Matchers.eq(SLOT_CONTEXT), Matchers.eq(SLOT_LOOKUP), Matchers.eq(true), Matchers.any(CMSSiteModel.class), Matchers.any(Date.class));
    }

    @Test
    public void testOneActiveSlot() {
        final List<AmpSlotModel> activeSlotList = new ArrayList<>();
        final AmpSlotModel ampSlotModel = getSlot(SLOT_1_ID, 1, true);
        activeSlotList.add(ampSlotModel);
        Mockito.when(contentDAO.getAllActiveSlots(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(), Mockito.any())).thenReturn(activeSlotList);

        final AmpSlotModel result = systemUnderTest.getActiveSlot(SLOT_POSITION, SLOT_CONTEXT, SLOT_LOOKUP);

        Assert.assertEquals(SLOT_1_ID, result.getId());
    }

    @Test
    public void testMultipleActiveSlot() {
        final AmpSlotModel slot1 = getSlot(SLOT_1_ID, 2, true);
        final AmpSlotModel slot2 = getSlot(SLOT_2_ID, 7, true);
        final AmpSlotModel slot3 = getSlot(SLOT_3_ID, 1, true);
        slot1.setModifiedtime(new DateTime("1970-01-01T00:00:00Z").toDate());
        slot2.setModifiedtime(new DateTime("1970-01-02T00:00:00Z").toDate());
        slot3.setModifiedtime(new DateTime("1970-01-03T00:00:00Z").toDate());

        final List<AmpSlotModel> slots = new ArrayList<>(Arrays.asList(slot1, slot2, slot3));
        Mockito.when(contentDAO.getAllActiveSlots(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(), Mockito.any())).thenReturn(slots);

        final AmpSlotModel result = systemUnderTest.getActiveSlot(SLOT_POSITION, SLOT_CONTEXT, SLOT_LOOKUP);

        Assert.assertEquals(SLOT_3_ID, result.getId());
    }

    @Test
    public void testMultipleActiveSlotByDeliveryKey() {
        final AmpSlotModel slot1 = getSlot(SLOT_1_ID, 2, true);
        final AmpSlotModel slot2 = getSlot(SLOT_2_ID, 7, true);
        final AmpSlotModel slot3 = getSlot(SLOT_3_ID, 1, true);
        slot1.setModifiedtime(new DateTime("1970-01-01T00:00:00Z").toDate());
        slot2.setModifiedtime(new DateTime("1970-01-02T00:00:00Z").toDate());
        slot3.setModifiedtime(new DateTime("1970-01-03T00:00:00Z").toDate());

        final List<AmpSlotModel> slots = new ArrayList<>(Arrays.asList(slot1, slot2, slot3));
        Mockito.when(contentDAO.getAllActiveSlotsByDeliveryKey(Mockito.anyString(), Mockito.anyBoolean(), Mockito.any(), Mockito.any())).thenReturn(slots);

        final AmpSlotModel result = systemUnderTest.getActiveSlotByDeliveryKey(KEY);

        Assert.assertEquals(SLOT_3_ID, result.getId());
    }

    @Test
    public void testPublishSlotWithModels() {
        final AmpSlotModel slot = getSlot(SLOT_1_ID, 1, false);
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);

        systemUnderTest.publishSlot(slot, webhookRequest);

        Assert.assertTrue(slot.getPublished());
        Mockito.verify(modelService).save(slot);
    }

    @Test
    public void testPublishSlotWithModelsUnpublishedNull() {
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);

        systemUnderTest.publishSlot(null, webhookRequest);

        Mockito.verify(modelService).remove(webhookRequest);
    }

    @Test
    public void testPublishSlotWithModelsWithEdition() {
        final AmpSlotModel slot1 = getSlot(SLOT_1_ID, 1, false);
        final AmpSlotModel slot2 = getSlot(SLOT_2_ID, 1, true);
        slot2.setEditionId("edition");

        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.anyString(), Matchers.eq(true))).thenReturn(null);
        Mockito.when(contentDAO.getAllSlotsById(SLOT_1_ID, Boolean.FALSE)).thenReturn(Arrays.asList(slot1, slot2));

        systemUnderTest.publishSlot(slot1, webhookRequest);

        Assert.assertTrue(slot1.getPublished());
        Mockito.verify(modelService).save(slot1);
    }

    @Test
    public void testPublishSlot() {
        final AmpSlotModel slot = getSlot(SLOT_2_ID, 1, false);
        slot.setEditionId("edition");

        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.anyString(), Matchers.eq(false))).thenReturn(slot);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.anyString(), Matchers.eq(true))).thenReturn(null);

        systemUnderTest.publishSlot(SLOT_2_ID, "edition", webhookRequest);

        Assert.assertTrue(slot.getPublished());
        Mockito.verify(modelService).save(slot);
    }

    @Test
    public void testPublishSlotNotFoundWithEdition() {
        final AmpSlotModel slot = getSlot(SLOT_2_ID, 1, false);
        slot.setEditionId("edition");
        final AmpWebhookRequestModel webhookRequest = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.anyString(), Matchers.eq(false))).thenReturn(null);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.anyString(), Matchers.eq(true))).thenReturn(null);
        Mockito.when(contentDAO.getLatestSlotById(Matchers.anyString(), Matchers.eq(false))).thenReturn(slot);

        systemUnderTest.publishSlot(SLOT_2_ID, "edition", webhookRequest);

        Assert.assertTrue(slot.getPublished());
        Mockito.verify(modelService).save(slot);
    }

    @Test
    public void testCleanup() {
        final AmpSlotModel slot1 = getSlot(SLOT_1_ID, 1, false);
        final AmpSlotModel slot2 = getSlot(SLOT_1_ID, 2, false);
        final AmpSlotModel slot3 = getSlot(SLOT_1_ID, 3, false);
        final List<AmpSlotModel> slots = Arrays.asList(slot1, slot2, slot3);
        Mockito.when(contentDAO.getAllSlotsById(SLOT_1_ID, null, true)).thenReturn(slots);

        systemUnderTest.cleanup(SLOT_1_ID, null, true);

        Mockito.verify(modelService).removeAll(captor.capture());
        MatcherAssert.assertThat(captor.getValue(), org.hamcrest.Matchers.containsInAnyOrder(slot1, slot2));
    }

    @Test
    public void testCleanupNoResults() {
        final List<AmpSlotModel> slots = Collections.emptyList();
        Mockito.when(contentDAO.getAllSlotsById(SLOT_1_ID, null, true)).thenReturn(slots);

        systemUnderTest.cleanup(SLOT_1_ID, null, true);

        Mockito.verify(modelService, Mockito.never()).removeAll(Matchers.anyCollectionOf(ItemModel.class));
    }

    @Test
    public void testCleanupExpiredEditions() {
        final AmpSlotModel slot1 = getSlot(SLOT_1_ID, 1, false);
        final AmpSlotModel slot2 = getSlot(SLOT_1_ID, 2, false);
        final AmpSlotModel slot3 = getSlot(SLOT_1_ID, 3, false);
        final List<AmpSlotModel> slots = Arrays.asList(slot1, slot2, slot3);
        Mockito.when(contentDAO.getAllSlotsWithExpiredEditions(Matchers.any())).thenReturn(slots);

        systemUnderTest.cleanupExpiredEditions();

        Mockito.verify(modelService).removeAll(captor.capture());
        MatcherAssert.assertThat(captor.getValue(), org.hamcrest.Matchers.containsInAnyOrder(slot1, slot2, slot3));
    }

    @Test
    public void testCleanupExpiredEditionsNoResults() {
        final List<AmpSlotModel> slots = Collections.emptyList();
        Mockito.when(contentDAO.getAllSlotsWithExpiredEditions(Matchers.any())).thenReturn(slots);

        systemUnderTest.cleanupExpiredEditions();

        Mockito.verify(modelService, Mockito.never()).removeAll(Matchers.anyCollectionOf(ItemModel.class));
    }

    @Test
    public void testFillContent() {
        final AmpSlotModel slot = Mockito.mock(AmpSlotModel.class);
        final AmpWebhookModel webhook = new AmpWebhookModel();
        final AmpLocaleMappingModel localeMapping = new AmpLocaleMappingModel();
        final AmpContentTypeModel contentType = new AmpContentTypeModel();
        localeMapping.setAmpLocale(AMP_LOCALE);
        localeMapping.setLocale(LOCALE);
        contentType.setUri("content-type");
        contentType.setTemplate("template");
        webhook.setApiVSE(API);
        webhook.setEndpoint(ENDPOINT);
        webhook.setLocaleMappings(Collections.singletonList(localeMapping));
        Mockito.when(slot.getWebhook()).thenReturn(webhook);
        Mockito.when(slot.getContentType()).thenReturn(contentType);
        Mockito.when(apiService.retrieveJsonForSlot(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(JSON);
        Mockito.when(apiService.retrieveHtmlForSlot(Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn(HTML);
        Mockito.when(commonI18NService.getLocaleForIsoCode(Matchers.anyString())).thenReturn(Locale.ENGLISH);

        systemUnderTest.updateContent(slot);

        Mockito.verify(slot).setJson(JSON, Locale.ENGLISH);
        Mockito.verify(slot).setHtml(HTML, Locale.ENGLISH);
    }

    private AmpSlotModel getSlot(final String id, final Integer version, final boolean published) {
        final AmpSlotModel slot = new AmpSlotModel();
        slot.setId(id);
        slot.setVersion(version);
        slot.setPublished(published);
        return slot;
    }

    private AmpWebhookModel getWebhookModel(final String uri, final String secret, final boolean validate) {
        final AmpWebhookModel webhook = new AmpWebhookModel();
        webhook.setUri(uri);
        webhook.setSecret(secret);
        webhook.setValidateSecret(validate);
        return webhook;
    }

    private AmpWebhookRequestModel getWebhookRequestModel(final WebhookType type) {
        final AmpWebhookRequestModel webhookRequestModel = new AmpWebhookRequestModel();
        webhookRequestModel.setWebhook(getWebhookModel("uri", "secret", true));
        webhookRequestModel.setType(type);
        webhookRequestModel.setJson("json");
        return webhookRequestModel;
    }

}
