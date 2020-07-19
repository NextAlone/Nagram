/*
 * Copyright (C) 2014-2015 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openintents.openpgp.util;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

public class OpenPgpUtils {

    public static final Pattern PGP_MESSAGE = Pattern.compile(
            ".*?(-----BEGIN PGP MESSAGE-----.*?-----END PGP MESSAGE-----).*",
            Pattern.DOTALL);

    public static final Pattern PGP_SIGNED_MESSAGE = Pattern.compile(
            ".*?(-----BEGIN PGP SIGNED MESSAGE-----.*?-----BEGIN PGP SIGNATURE-----.*?-----END PGP SIGNATURE-----).*",
            Pattern.DOTALL);

    public static final int PARSE_RESULT_NO_PGP = -1;
    public static final int PARSE_RESULT_MESSAGE = 0;
    public static final int PARSE_RESULT_SIGNED_MESSAGE = 1;

    public static int parseMessage(String message) {
        Matcher matcherSigned = PGP_SIGNED_MESSAGE.matcher(message);
        Matcher matcherMessage = PGP_MESSAGE.matcher(message);

        if (matcherMessage.matches()) {
            return PARSE_RESULT_MESSAGE;
        } else if (matcherSigned.matches()) {
            return PARSE_RESULT_SIGNED_MESSAGE;
        } else {
            return PARSE_RESULT_NO_PGP;
        }
    }

    public static boolean isAvailable(Context context) {
        Intent intent = new Intent(OpenPgpApi.SERVICE_INTENT_2);
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentServices(intent, 0);
        return !resInfo.isEmpty();
    }

    public static String convertKeyIdToHex(long keyId) {
        return "0x" + convertKeyIdToHex32bit(keyId >> 32) + convertKeyIdToHex32bit(keyId);
    }

    private static String convertKeyIdToHex32bit(long keyId) {
        String hexString = Long.toHexString(keyId & 0xffffffffL).toLowerCase(Locale.ENGLISH);
        while (hexString.length() < 8) {
            hexString = "0" + hexString;
        }
        return hexString;
    }


    private static final Pattern USER_ID_PATTERN = Pattern.compile("^(.*?)(?: \\((.*)\\))?(?: <(.*)>)?$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^<?\"?([^<>\"]*@[^<>\"]*\\.[^<>\"]*)\"?>?$");

    /**
     * Splits userId string into naming part, email part, and comment part.
     * See SplitUserIdTest for examples.
     */
    public static UserId splitUserId(final String userId) {
        if (!TextUtils.isEmpty(userId)) {
            final Matcher matcher = USER_ID_PATTERN.matcher(userId);
            if (matcher.matches()) {
                String name = matcher.group(1).isEmpty() ? null : matcher.group(1);
                String comment = matcher.group(2);
                String email = matcher.group(3);
                if (email != null && name != null) {
                    final Matcher emailMatcher = EMAIL_PATTERN.matcher(name);
                    if (emailMatcher.matches() && email.equals(emailMatcher.group(1))) {
                        email = emailMatcher.group(1);
                        name = null;
                    }
                }
                if (email == null && name != null) {
                    final Matcher emailMatcher = EMAIL_PATTERN.matcher(name);
                    if (emailMatcher.matches()) {
                        email = emailMatcher.group(1);
                        name = null;
                    }
                }
                return new UserId(name, email, comment);
            }
        }
        return new UserId(null, null, null);
    }

    /**
     * Returns a composed user id. Returns null if name, email and comment are empty.
     */
    public static String createUserId(UserId userId) {
        StringBuilder userIdBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(userId.name)) {
            userIdBuilder.append(userId.name);
        }
        if (!TextUtils.isEmpty(userId.comment)) {
            userIdBuilder.append(" (");
            userIdBuilder.append(userId.comment);
            userIdBuilder.append(")");
        }
        if (!TextUtils.isEmpty(userId.email)) {
            userIdBuilder.append(" <");
            userIdBuilder.append(userId.email);
            userIdBuilder.append(">");
        }
        return userIdBuilder.length() == 0 ? null : userIdBuilder.toString();
    }

    public static class UserId implements Serializable {
        public final String name;
        public final String email;
        public final String comment;

        public UserId(String name, String email, String comment) {
            this.name = name;
            this.email = email;
            this.comment = comment;
        }
    }
}
