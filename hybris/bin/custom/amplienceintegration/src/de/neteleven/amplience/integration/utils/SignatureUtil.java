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

package de.neteleven.amplience.integration.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class to validate the signature of a request against a given secret.
 */
public final class SignatureUtil {

    /**
     * Validate the signature of a request against a given secret.
     *
     * @param secret  Secret for the hash
     * @param hash    Expected hash value
     * @param message Message payload to check
     * @return true when the hash is equals to the expected hash
     */
    public static boolean checkSignature(final String secret, final String hash, final String message) {
        if (secret == null || message == null) {
            return false;
        }

        final byte[] secretBytes = secret.getBytes(Charsets.UTF_8);
        final byte[] messageBytes = message.getBytes(Charsets.UTF_8);

        try {
            final Mac sha256 = Mac.getInstance("HmacSHA256");
            final SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA256");
            sha256.init(secretKey);

            final String encodedMessage = Base64.encodeBase64String(sha256.doFinal(messageBytes));

            return StringUtils.equals(hash, encodedMessage);

        } catch (final Exception e) {
            return false;
        }
    }
}
