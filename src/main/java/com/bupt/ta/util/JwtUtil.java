package com.bupt.ta.util;

import com.bupt.ta.model.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

public final class JwtUtil {
    private static final Gson GSON = new Gson();
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String SECRET_PROPERTY = "ta.rememberMe.secret";
    private static final String SECRET_ENV = "TA_REMEMBER_ME_SECRET";
    private static final String DEFAULT_SECRET = "TA-Recruitment-System-Remember-Me-Secret-v1";

    private JwtUtil() {
    }

    public static String createRememberMeToken(User user, int maxAgeSeconds) {
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + maxAgeSeconds;

        JsonObject header = new JsonObject();
        header.addProperty("alg", "HS256");
        header.addProperty("typ", "JWT");

        JsonObject payload = new JsonObject();
        payload.addProperty("sub", user.getUserId());
        payload.addProperty("role", user.getRole().name());
        payload.addProperty("tokenVersion", user.getTokenVersion());
        payload.addProperty("iat", issuedAt);
        payload.addProperty("exp", expiresAt);

        String encodedHeader = base64UrlEncode(GSON.toJson(header));
        String encodedPayload = base64UrlEncode(GSON.toJson(payload));
        String unsignedToken = encodedHeader + "." + encodedPayload;
        return unsignedToken + "." + sign(unsignedToken);
    }

    public static JwtClaims verifyRememberMeToken(String token) {
        if (ValidationUtil.isBlank(token)) {
            return null;
        }

        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String unsignedToken = parts[0] + "." + parts[1];
            byte[] expectedSignature = parts[2].getBytes(StandardCharsets.UTF_8);
            byte[] actualSignature = sign(unsignedToken).getBytes(StandardCharsets.UTF_8);
            if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
                return null;
            }

            JsonObject header = JsonParser.parseString(base64UrlDecode(parts[0])).getAsJsonObject();
            if (!"HS256".equals(getString(header, "alg"))) {
                return null;
            }

            JsonObject payload = JsonParser.parseString(base64UrlDecode(parts[1])).getAsJsonObject();
            long expiresAt = getLong(payload, "exp", 0L);
            if (expiresAt <= Instant.now().getEpochSecond()) {
                return null;
            }

            String userId = getString(payload, "sub");
            String roleName = getString(payload, "role");
            int tokenVersion = (int) getLong(payload, "tokenVersion", 0L);
            if (ValidationUtil.isBlank(userId) || ValidationUtil.isBlank(roleName)) {
                return null;
            }
            return new JwtClaims(userId, roleName, tokenVersion, expiresAt);
        } catch (Exception exception) {
            return null;
        }
    }

    private static String sign(String unsignedToken) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(resolveSecret().getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            byte[] signature = mac.doFinal(unsignedToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign remember-me JWT.", exception);
        }
    }

    private static String resolveSecret() {
        String secret = System.getProperty(SECRET_PROPERTY);
        if (!ValidationUtil.isBlank(secret)) {
            return secret;
        }
        secret = System.getenv(SECRET_ENV);
        if (!ValidationUtil.isBlank(secret)) {
            return secret;
        }
        // 课程项目默认可直接运行；生产环境应通过系统属性或环境变量替换密钥。
        return DEFAULT_SECRET;
    }

    private static String base64UrlEncode(String value) {
        return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String base64UrlDecode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }

    private static String getString(JsonObject object, String memberName) {
        return object.has(memberName) && !object.get(memberName).isJsonNull()
            ? object.get(memberName).getAsString()
            : null;
    }

    private static long getLong(JsonObject object, String memberName, long defaultValue) {
        return object.has(memberName) && !object.get(memberName).isJsonNull()
            ? object.get(memberName).getAsLong()
            : defaultValue;
    }

    public static final class JwtClaims {
        private final String userId;
        private final String roleName;
        private final int tokenVersion;
        private final long expiresAt;

        private JwtClaims(String userId, String roleName, int tokenVersion, long expiresAt) {
            this.userId = userId;
            this.roleName = roleName;
            this.tokenVersion = tokenVersion;
            this.expiresAt = expiresAt;
        }

        public String getUserId() {
            return userId;
        }

        public String getRoleName() {
            return roleName;
        }

        public int getTokenVersion() {
            return tokenVersion;
        }

        public long getExpiresAt() {
            return expiresAt;
        }
    }
}
