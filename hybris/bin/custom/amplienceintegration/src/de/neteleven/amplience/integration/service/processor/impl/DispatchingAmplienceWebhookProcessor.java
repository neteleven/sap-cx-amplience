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

import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service("dispatchingAmplienceWebhookProcessor")
public class DispatchingAmplienceWebhookProcessor implements AmplienceWebhookProcessor {

    private final AmplienceWebhookProcessor updateAmplienceWebhookProcessor;
    private final AmplienceWebhookProcessor publishAmplienceWebhookProcessor;
    private final AmplienceWebhookProcessor deleteAmplienceWebhookProcessor;
    private final AmplienceWebhookProcessor editionAmplienceWebhookProcessor;

    public DispatchingAmplienceWebhookProcessor(final AmplienceWebhookProcessor updateAmplienceWebhookProcessor,
                                                final AmplienceWebhookProcessor publishAmplienceWebhookProcessor,
                                                final AmplienceWebhookProcessor deleteAmplienceWebhookProcessor,
                                                final AmplienceWebhookProcessor editionAmplienceWebhookProcessor) {
        this.updateAmplienceWebhookProcessor = updateAmplienceWebhookProcessor;
        this.publishAmplienceWebhookProcessor = publishAmplienceWebhookProcessor;
        this.deleteAmplienceWebhookProcessor = deleteAmplienceWebhookProcessor;
        this.editionAmplienceWebhookProcessor = editionAmplienceWebhookProcessor;
    }

    @Override
    public boolean processWebhookRequest(final AmpWebhookRequestModel webhookRequest) {
        Assert.notNull(webhookRequest, "Request must not be null!");
        Assert.notNull(webhookRequest.getWebhook(), "Webhook must not be null!");
        Assert.notNull(webhookRequest.getType(), "Webhook Type must not be null!");

        switch (webhookRequest.getType()) {
            case UPDATE:
                return updateAmplienceWebhookProcessor.processWebhookRequest(webhookRequest);
            case PUBLISH:
                return publishAmplienceWebhookProcessor.processWebhookRequest(webhookRequest);
            case EDITION:
                return editionAmplienceWebhookProcessor.processWebhookRequest(webhookRequest);
            case DELETE:
                return deleteAmplienceWebhookProcessor.processWebhookRequest(webhookRequest);
            default:
                return false;
        }
    }
}
