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

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Assert;
import org.junit.Test;

@UnitTest
public class SignatureUtilTest {

    @Test
    public void testCheckSignatureTrue() {
        final String secret = "0123456789abcdefghijklmnopqrstvwxyz";
        final String hash = "0uD9U0hFYdowxDkKvLQtP45lcXs8q2EFvXgh1ioWfkU=";
        final String message = "lorem ipsum dolor sit amet";

        final boolean result = SignatureUtil.checkSignature(secret, hash, message);

        Assert.assertTrue(result);
    }

    @Test
    public void testCheckSignatureFalse() {
        final String secret = "0123456789abcdefghijklmnopqrstvwxyz";
        final String hash = "";
        final String message = "lorem ipsum dolor sit amet";

        final boolean result = SignatureUtil.checkSignature(secret, hash, message);

        Assert.assertFalse(result);
    }

    @Test
    public void testCheckSignatureEmptySecret() {
        final String secret = "";
        final String hash = "";
        final String message = "lorem ipsum dolor sit amet";

        final boolean result = SignatureUtil.checkSignature(secret, hash, message);

        Assert.assertFalse(result);
    }

    @Test
    public void testCheckSignatureNullSecret() {
        final String secret = null;
        final String hash = "";
        final String message = "lorem ipsum dolor sit amet";

        final boolean result = SignatureUtil.checkSignature(secret, hash, message);

        Assert.assertFalse(result);
    }

    @Test
    public void testCheckSignatureNullMessage() {
        final String secret = "0123456789abcdefghijklmnopqrstvwxyz";
        final String hash = "";
        final String message = null;

        final boolean result = SignatureUtil.checkSignature(secret, hash, message);

        Assert.assertFalse(result);
    }

}
