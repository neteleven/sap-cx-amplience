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

package de.neteleven.amplience.integration.service;

public interface AmplienceAPIService {

    /**
     * Retrieves the JSON for a Slot from Amplience.
     *
     * @param slotId    ID of the Slot
     * @param api       the API to call
     * @param ampLocale the Amplience locale
     * @return the JSON
     */
    String retrieveJsonForSlot(final String slotId, final String api, final String ampLocale);

    /**
     * Retrieves the prerenderen HTML for a Slot from Amplience.
     *
     * @param slotId    ID of the Slot
     * @param api       the API to call
     * @param endpoint  the endpoint
     * @param template  the template to use for rendering
     * @param ampLocale the Amplience locale
     * @return the HTML
     */
    String retrieveHtmlForSlot(final String slotId, final String api, final String endpoint, final String template, final String ampLocale);
}
