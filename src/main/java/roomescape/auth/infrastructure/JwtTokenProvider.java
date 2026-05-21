package roomescape.auth.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import roomescape.auth.domain.AuthErrorCode;
import roomescape.auth.domain.TokenPayload;
import roomescape.auth.domain.TokenProvider;
import roomescape.global.exception.BusinessException;
import roomescape.member.domain.Member;

@Component
public class JwtTokenProvider implements TokenProvider {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final String secret;
    private final long expirationSeconds;

    public JwtTokenProvider(
            ObjectMapper objectMapper,
            Clock clock,
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.objectMapper = objectMapper;
        this.clock = clock;
        this.secret = secret;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String createToken(Member member) {
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", member.id());
        payload.put("email", member.email());
        payload.put("name", member.name());
        payload.put("exp", now() + expirationSeconds);

        String unsignedToken = encode(header) + "." + encode(payload);
        return unsignedToken + "." + sign(unsignedToken);
    }

    @Override
    public TokenPayload parse(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
        validateSignature(parts);
        Map<String, Object> payload = decode(parts[1]);
        validateExpiration(payload);
        return new TokenPayload(
                ((Number) payload.get("sub")).longValue(),
                (String) payload.get("email"),
                (String) payload.get("name")
        );
    }

    private String encode(Map<String, Object> value) {
        try {
            byte[] json = objectMapper.writeValueAsBytes(value);
            return URL_ENCODER.encodeToString(json);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("JWT를 생성할 수 없습니다.", exception);
        }
    }

    private Map<String, Object> decode(String value) {
        try {
            return objectMapper.readValue(URL_DECODER.decode(value), MAP_TYPE);
        } catch (Exception exception) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void validateSignature(String[] parts) {
        String unsignedToken = parts[0] + "." + parts[1];
        String expected = sign(unsignedToken);
        if (!constantTimeEquals(expected, parts[2])) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private void validateExpiration(Map<String, Object> payload) {
        Object expiration = payload.get("exp");
        if (!(expiration instanceof Number) || ((Number) expiration).longValue() <= now()) {
            throw new BusinessException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("JWT에 서명할 수 없습니다.", exception);
        }
    }

    private boolean constantTimeEquals(String expected, String actual) {
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);
        if (expectedBytes.length != actualBytes.length) {
            return false;
        }
        int result = 0;
        for (int index = 0; index < expectedBytes.length; index++) {
            result |= expectedBytes[index] ^ actualBytes[index];
        }
        return result == 0;
    }

    private long now() {
        return clock.instant().getEpochSecond();
    }
}
