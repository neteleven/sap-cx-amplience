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
package de.neteleven.amplience.integration.controller;

import de.neteleven.amplience.integration.service.AmplienceWebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * Controller to deal with Amplience Webhooks.
 */
@Controller
@RequestMapping("/webhook")
public class WebhookController {

    @Autowired
    private AmplienceWebhookService webhookService;

    /**
     * Receives Webhooks by Amplience.
     *
     * @param message     message body of the Webhook
     * @param hash        hash value to check the content
     * @param webhookUri  the URI of the Webhook, used to lookup the configuration in Hybris
     * @param webhookType the type of the Webhook
     * @return resonse
     */
    @RequestMapping(value = "/{uri}/{type}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> consumeWebhook(@RequestBody final String message,
                                                 @RequestHeader("X-Amplience-Webhook-Signature") final String hash,
                                                 @PathVariable(name = "uri") final String webhookUri,
                                                 @PathVariable(name = "type") final String webhookType) {

        if (!webhookService.checkWebhookSignature(webhookUri, hash, message)) {
            return new ResponseEntity<>("Hash validation failed.\n", HttpStatus.UNAUTHORIZED);
        }

        if (!webhookService.queueWebhook(webhookUri, message, webhookType)) {
            return new ResponseEntity<>("Webhook Request failed.\n", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Webhook Request queued.\n", HttpStatus.OK);
    }
}
