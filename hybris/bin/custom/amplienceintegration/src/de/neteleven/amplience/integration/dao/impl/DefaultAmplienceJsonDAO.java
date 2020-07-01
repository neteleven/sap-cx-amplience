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

package de.neteleven.amplience.integration.dao.impl;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.neteleven.amplience.integration.dao.AmplienceJsonDAO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefaultAmplienceJsonDAO implements AmplienceJsonDAO {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultAmplienceJsonDAO.class);

    private final ConfigurationService configurationService;

    public DefaultAmplienceJsonDAO(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public <T> T getElementFromJSON(final String json, final String configKey) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        final String path = configurationService.getConfiguration().getString(configKey);
        try {
            return JsonPath.read(json, path);
        } catch (final PathNotFoundException pathNotFoundException) {
            LOG.info("Path \"" + path + "\" not found in JSON.");
            return null;
        }
    }
}
