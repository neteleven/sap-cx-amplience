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

package de.neteleven.amplience.integration.dao;

/**
 * Deals with Amplience JSON.
 */
public interface AmplienceJsonDAO {
    /**
     * Gets an element from within a JSON String. The lookup is done by a JSON Path String,
     * stored inside a configuration value.
     *
     * @param json      JSON String
     * @param configKey config key to lookup JSON Path String
     * @return result of the lookup, null when nothing is found
     */
    <T> T getElementFromJSON(String json, String configKey);
}
