package auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;

public class AuthTokenAuthenticator implements Authenticator<String, User> {

    private final Base64 BASE64 = new Base64();

    private AuthService authService;

    public AuthTokenAuthenticator(Retrofit retrofit) {
        authService = retrofit.create(AuthService.class);
    }

    @Override
    public Optional<User> authenticate(String authToken) throws AuthenticationException {

        if (StringUtils.isBlank(authToken)) {
            throw new AuthenticationException("Token is not supplied");
        }

        if (isTokenValid(authToken)) {
            return extractUserFromToken(authToken);
        } else {
            throw new AuthenticationException("Token is not valid");
        }
    }

    private Optional<User> extractUserFromToken(String authToken) throws AuthenticationException {

        final String[] base64EncodedSegments = authToken.split("\\.");
        if (base64EncodedSegments.length != 3) {
            throw new AuthenticationException("Token is not a valid JWT token");
        }
        final String claimsEncoded = base64EncodedSegments[1];

        // decode base64
        final byte[] bytes = BASE64.decode(claimsEncoded);
        final String claimsDecoded = new String(bytes);

        // create json
        final ObjectMapper mapper = new ObjectMapper();
        final JsonNode claimJson;
        try {
            claimJson = mapper.readTree(claimsDecoded);
        } catch (IOException e) {
            throw new AuthenticationException("Token cannot be read", e);
        }

        if (claimJson == null) {
            throw new AuthenticationException("Token cannot be read");
        }

        // extract properties from claim json
        final String id = getMandatoryValue(claimJson, "id");
        final String email = getMandatoryValue(claimJson, "sub");
        final String role = getMandatoryValue(claimJson, "role");

        // return user
        User user = new User(id, email, role);

        return Optional.fromNullable(user);
    }

    private String getMandatoryValue(JsonNode jsonNode, String key) throws AuthenticationException {
        final JsonNode node = jsonNode.get(key);

        if (node == null) {
            throw new AuthenticationException("Token does not contain required data");
        }

        return node.asText();
    }

    private boolean isTokenValid(String authToken) throws AuthenticationException {
        Call<Void> call = authService.validateToken(authToken);

        Response<Void> result;
        try {
            result = call.execute();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to auth service", e);
        }

        return result.isSuccessful();
    }
}
