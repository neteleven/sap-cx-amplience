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
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.neteleven.amplience.integration.service.AmplienceAPIService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAmplienceAPIServiceTest {

    private static final String SLOT_ID = "12345";
    private static final String API = "sandbox.cdn.content.amplience.net";
    private static final String ENDPOINT = "endpoint";
    private static final String TEMPLATE = "template";
    private static final String AMP_LOCALE = "en-GB";
    private static final String JSON_URL = "https://{api}/content/id/{slotId}?depth=all&format=inlined&locale={ampLocale}";
    private static final String JSON_URL_NO_LOCALE = "https://{api}/content/id/{slotId}?depth=all&format=inlined";
    private static final String HTML_URL = "https://{api}/v1/content/{endpoint}/content-item/{slotId}?template={template}&locale={ampLocale}";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private AmplienceAPIService apiService;
    @Mock
    private CommonI18NService commonI18NService;
    @Mock
    private ModelService modelService;

    @Spy
    @InjectMocks
    private DefaultAmplienceAPIService systemUnderTest;

    @Before
    public void setUp() {
        Mockito.when(restTemplate.getForObject(JSON_URL, String.class, API, SLOT_ID, AMP_LOCALE)).thenReturn("json");
        Mockito.when(restTemplate.getForObject(JSON_URL_NO_LOCALE, String.class, API, SLOT_ID)).thenReturn("json");
        Mockito.when(restTemplate.getForObject(HTML_URL, String.class, API, ENDPOINT, SLOT_ID, TEMPLATE, AMP_LOCALE)).thenReturn("html");
    }

    @Test
    public void testRetrieveJsonForSlot() {
        final String result = systemUnderTest.retrieveJsonForSlot(SLOT_ID, API, AMP_LOCALE);
        Assert.assertEquals("json", result);
    }

    @Test
    public void testRetrieveJsonForSlotNoLocale() {
        final String result = systemUnderTest.retrieveJsonForSlot(SLOT_ID, API, null);
        Assert.assertEquals("json", result);
    }

    @Test
    public void testRetrieveJsonForSlotNullSlot() {
        final String result = systemUnderTest.retrieveJsonForSlot(null, API, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveJsonForSlotNullApi() {
        final String result = systemUnderTest.retrieveJsonForSlot(SLOT_ID, null, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveJsonForSlotNotFound() {
        Mockito.when(restTemplate.getForObject(JSON_URL, String.class, API, SLOT_ID, AMP_LOCALE)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final String result = systemUnderTest.retrieveJsonForSlot(SLOT_ID, API, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
    }


    @Test
    public void testRetrieveHtmlForSlot() {
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, API, ENDPOINT, TEMPLATE, AMP_LOCALE);
        Assert.assertEquals("html", result);
    }

    @Test
    public void testRetrieveHtmlForSlotNullSlot() {
        final String result = systemUnderTest.retrieveHtmlForSlot(null, API, ENDPOINT, TEMPLATE, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveHtmlForSlotNullAPI() {
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, null, ENDPOINT, TEMPLATE, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveHtmlForSlotNullEndpoint() {
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, API, null, TEMPLATE, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveHtmlForSlotNullTemplate() {
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, API, ENDPOINT, null, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveHtmlForSlotNullLocale() {
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, API, ENDPOINT, TEMPLATE, null);
        Assert.assertEquals(StringUtils.EMPTY, result);
        Mockito.verifyZeroInteractions(restTemplate);
    }

    @Test
    public void testRetrieveHTMLForSlotNotFound() {
        Mockito.when(restTemplate.getForObject(HTML_URL, String.class, API, ENDPOINT, SLOT_ID, TEMPLATE, AMP_LOCALE)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final String result = systemUnderTest.retrieveHtmlForSlot(SLOT_ID, API, ENDPOINT, TEMPLATE, AMP_LOCALE);
        Assert.assertEquals(StringUtils.EMPTY, result);
    }
}
