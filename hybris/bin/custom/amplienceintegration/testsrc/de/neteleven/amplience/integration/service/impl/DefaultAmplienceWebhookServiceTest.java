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
import de.hybris.platform.servicelayer.exceptions.ModelCreationException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neteleven.amplience.integration.constants.AmplienceintegrationConstants;
import de.neteleven.amplience.integration.dao.AmplienceJsonDAO;
import de.neteleven.amplience.integration.dao.AmplienceWebhookDAO;
import de.neteleven.amplience.integration.enums.WebhookType;
import de.neteleven.amplience.integration.model.AmpContentTypeModel;
import de.neteleven.amplience.integration.model.AmpWebhookModel;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAmplienceWebhookServiceTest {

    @Mock
    private ModelService modelService;
    @Mock
    private AmplienceWebhookDAO webhookDAO;
    @Mock
    private AmplienceJsonDAO jsonDAO;

    @InjectMocks
    private DefaultAmplienceWebhookService systemUnderTest;

    @Test
    public void testGetWebhookSecretByUri() {
        mockWebhookDAO("uri", "secret", true);
        final String secret = systemUnderTest.getWebhookSecretByUri("uri");
        Assert.assertEquals("secret", secret);
    }

    @Test
    public void testGetWebhookSecretByUriNotFound() {
        Mockito.when(webhookDAO.getWebhookByUri(Matchers.anyString())).thenReturn(null);
        final String secret = systemUnderTest.getWebhookSecretByUri("uri");
        Assert.assertEquals(StringUtils.EMPTY, secret);
    }

    @Test
    public void testCheckWebhookSignature() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        final boolean result = systemUnderTest.checkWebhookSignature("uri", "0uD9U0hFYdowxDkKvLQtP45lcXs8q2EFvXgh1ioWfkU=", "lorem ipsum dolor sit amet");
        Assert.assertTrue(result);
    }

    @Test
    public void testCheckWebhookSignatureWrong() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        final boolean result = systemUnderTest.checkWebhookSignature("uri", "1234", "lorem ipsum dolor sit amet");
        Assert.assertFalse(result);
    }

    @Test
    public void testCheckWebhookSignatureIgnore() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", false);
        final boolean result = systemUnderTest.checkWebhookSignature("uri", "1234", "lorem ipsum dolor sit amet");
        Assert.assertTrue(result);
    }

    @Test
    public void testQueueWebhook() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        Mockito.when(jsonDAO.getElementFromJSON("message", AmplienceintegrationConstants.Webhook.Request.ID)).thenReturn("id");
        Mockito.when(webhookDAO.getWebhookRequestById("id")).thenReturn(null);
        Mockito.when(modelService.create(AmpWebhookRequestModel.class)).thenReturn(new AmpWebhookRequestModel());
        Mockito.doNothing().when(modelService).save(Matchers.any(AmpWebhookRequestModel.class));

        final boolean result = systemUnderTest.queueWebhook("uri", "message", "update");

        Mockito.verify(modelService).create(AmpWebhookRequestModel.class);
        Mockito.verify(modelService).save(Matchers.any(AmpWebhookRequestModel.class));
        Assert.assertTrue(result);
    }

    @Test
    public void testQueueWebhookUpdate() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        Mockito.when(jsonDAO.getElementFromJSON("message", AmplienceintegrationConstants.Webhook.Request.ID)).thenReturn("id");
        Mockito.when(webhookDAO.getWebhookRequestById("id")).thenReturn(new AmpWebhookRequestModel());
        Mockito.doNothing().when(modelService).save(Matchers.any(AmpWebhookRequestModel.class));

        final boolean result = systemUnderTest.queueWebhook("uri", "message", "update");

        Mockito.verify(modelService, Mockito.never()).create(AmpWebhookRequestModel.class);
        Mockito.verify(modelService).save(Matchers.any(AmpWebhookRequestModel.class));
        Assert.assertTrue(result);
    }

    @Test
    public void testQueueWebhookNotFound() {
        Mockito.when(webhookDAO.getWebhookByUri("uri")).thenReturn(null);
        final boolean result = systemUnderTest.queueWebhook("uri", "message", "update");
        Assert.assertFalse(result);
    }

    @Test
    public void testQueueWebhookNoUri() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        final boolean result = systemUnderTest.queueWebhook(null, "message", "update");
        Assert.assertFalse(result);
    }

    @Test
    public void testQueueWebhookNoMessage() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        final boolean result = systemUnderTest.queueWebhook("uri", null, "update");
        Assert.assertFalse(result);
    }

    @Test
    public void testQueueWebhookNoType() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        final boolean result = systemUnderTest.queueWebhook("uri", "message", null);
        Assert.assertFalse(result);
    }

    @Test
    public void testQueueWebhookException() {
        mockWebhookDAO("uri", "0123456789abcdefghijklmnopqrstvwxyz", true);
        Mockito.when(jsonDAO.getElementFromJSON("message", AmplienceintegrationConstants.Webhook.Request.ID)).thenReturn("id");
        Mockito.when(webhookDAO.getWebhookRequestById("id")).thenReturn(null);
        Mockito.when(modelService.create(AmpWebhookRequestModel.class)).thenThrow(ModelCreationException.class);

        final boolean result = systemUnderTest.queueWebhook("uri", "message", "update");

        Assert.assertFalse(result);
    }

    @Test
    public void testGetContentTypeIfMatches() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(jsonDAO.getElementFromJSON(Matchers.anyString(), Matchers.eq(AmplienceintegrationConstants.Webhook.Update.CONTENTTYPE))).thenReturn("content-type");
        final AmpContentTypeModel contentType = getContentTypeModel("content-type");
        request.getWebhook().setContentTypes(Arrays.asList(contentType));

        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(request);

        Assert.assertEquals(contentType.getUri(), result.getUri());
    }

    @Test
    public void testGetContentTypeIfMatchesNoMatch() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(jsonDAO.getElementFromJSON(Matchers.anyString(), Matchers.eq(AmplienceintegrationConstants.Webhook.Update.CONTENTTYPE))).thenReturn("content-type");
        final AmpContentTypeModel contentType = getContentTypeModel("other-content-type");
        request.getWebhook().setContentTypes(Arrays.asList(contentType));

        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(request);

        Assert.assertNull(result);
    }

    @Test
    public void testGetContentTypeIfMatchesMultiple() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        Mockito.when(jsonDAO.getElementFromJSON(Matchers.anyString(), Matchers.eq(AmplienceintegrationConstants.Webhook.Update.CONTENTTYPE))).thenReturn("content-type-2");
        final AmpContentTypeModel contentType = getContentTypeModel("content-type");
        final AmpContentTypeModel contentType2 = getContentTypeModel("content-type-2");
        request.getWebhook().setContentTypes(Arrays.asList(contentType, contentType2));

        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(request);

        Assert.assertEquals(contentType2.getUri(), result.getUri());
    }

    @Test
    public void testGetContentTypeIfMatchesRequestNull() {
        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(null);

        Assert.assertNull(result);
    }

    @Test
    public void testGetContentTypeIfMatchesWebhookNull() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(WebhookType.UPDATE);
        request.setWebhook(null);

        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(request);

        Assert.assertNull(result);
    }

    @Test
    public void testGetContentTypeIfMatchesRequestTypeNull() {
        final AmpWebhookRequestModel request = getWebhookRequestModel(null);

        final AmpContentTypeModel result = systemUnderTest.getContentTypeIfMatches(request);

        Assert.assertNull(result);
    }


    private void mockWebhookDAO(final String uri, final String secret, final boolean validate) {
        final AmpWebhookModel webhook = geWebhookModel(uri, secret, validate);
        Mockito.when(webhookDAO.getWebhookByUri(uri)).thenReturn(webhook);
    }

    private AmpWebhookModel geWebhookModel(final String uri, final String secret, final boolean validate) {
        final AmpWebhookModel webhook = new AmpWebhookModel();
        webhook.setUri(uri);
        webhook.setSecret(secret);
        webhook.setValidateSecret(validate);
        return webhook;
    }

    private AmpWebhookRequestModel getWebhookRequestModel(final WebhookType type) {
        final AmpWebhookRequestModel webhookRequestModel = new AmpWebhookRequestModel();
        webhookRequestModel.setWebhook(geWebhookModel("uri", "secret", true));
        webhookRequestModel.setType(type);
        webhookRequestModel.setJson("json");
        return webhookRequestModel;
    }

    private AmpContentTypeModel getContentTypeModel(final String uri) {
        final AmpContentTypeModel contentTypeModel = new AmpContentTypeModel();
        contentTypeModel.setUri(uri);
        return contentTypeModel;
    }
}
