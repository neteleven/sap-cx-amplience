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

package de.neteleven.amplience.integration.job;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.neteleven.amplience.integration.dao.AmplienceWebhookDAO;
import de.neteleven.amplience.integration.model.AmpWebhookRequestModel;
import de.neteleven.amplience.integration.service.processor.AmplienceWebhookProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Processes Amplience Webhook Requests from Queue
 */
public class ProcessAmplienceWebhookRequestJob extends AbstractJobPerformable<CronJobModel> {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessAmplienceWebhookRequestJob.class);

    private AmplienceWebhookDAO webhookDAO;
    private AmplienceWebhookProcessor webhookProcessor;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel) {

        for (final AmpWebhookRequestModel webhookRequest : webhookDAO.getWebhookRequests()) {
            try {
                webhookProcessor.processWebhookRequest(webhookRequest);
            } catch (final IllegalArgumentException e) {
                LOG.error("Error during consumption of Webhook Request \"" + webhookRequest.getId() + "\": " + e.getMessage());
            }
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }


    public AmplienceWebhookDAO getWebhookDAO() {
        return webhookDAO;
    }

    @Required
    public void setWebhookDAO(final AmplienceWebhookDAO webhookDAO) {
        this.webhookDAO = webhookDAO;
    }

    public AmplienceWebhookProcessor getWebhookProcessor() {
        return webhookProcessor;
    }

    public void setWebhookProcessor(final AmplienceWebhookProcessor webhookProcessor) {
        this.webhookProcessor = webhookProcessor;
    }
}
