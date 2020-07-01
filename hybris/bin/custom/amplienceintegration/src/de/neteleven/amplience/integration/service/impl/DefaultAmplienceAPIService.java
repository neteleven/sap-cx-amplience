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

import de.neteleven.amplience.integration.service.AmplienceAPIService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class DefaultAmplienceAPIService implements AmplienceAPIService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceAPIService.class);
    private final RestTemplate restTemplate;

    public DefaultAmplienceAPIService(@Qualifier("amplienceRestTemplate") final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String retrieveJsonForSlot(final String slotId, final String api, final String ampLocale) {

        if (StringUtils.isEmpty(api) || StringUtils.isEmpty(slotId)) {
            LOG.error("Not all method parameters set! Aborting...");
            return StringUtils.EMPTY;
        }

        try {
            if (StringUtils.isEmpty(ampLocale)) {
                return restTemplate.getForObject("https://{api}/content/id/{slotId}?depth=all&format=inlined", String.class, api, slotId);
            }
            return restTemplate.getForObject("https://{api}/content/id/{slotId}?depth=all&format=inlined&locale={ampLocale}", String.class, api, slotId, ampLocale);
        } catch (final HttpClientErrorException ex) {
            LOG.error("Get JSON from amplience threw an exception: " + ex.getMessage());
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String retrieveHtmlForSlot(final String slotId, final String api, final String endpoint, final String template, final String ampLocale) {

        if (StringUtils.isEmpty(api) || StringUtils.isEmpty(slotId) || StringUtils.isEmpty(endpoint) || StringUtils.isEmpty(template) || StringUtils.isEmpty(ampLocale)) {
            LOG.error("Not all method parameters set! Aborting...");
            return StringUtils.EMPTY;
        }

        try {
            return restTemplate.getForObject("https://{api}/v1/content/{endpoint}/content-item/{slotId}?template={template}&locale={ampLocale}", String.class, api, endpoint, slotId, template, ampLocale);
        } catch (final HttpClientErrorException ex) {
            LOG.error("Get HTML from amplience threw an exception: " + ex.getMessage());
            return StringUtils.EMPTY;
        }
    }
}
