package auth;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.security.Principal;

@Priority(Priorities.AUTHENTICATION)
public class TokenAuthFilter<P extends Principal> extends AuthFilter<String, P> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthFilter.class);

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {

        // Get auth token from header
        final String authToken = requestContext.getHeaders().getFirst("authToken");

        if (StringUtils.isBlank(authToken)) {
            throw unauthorizedWebException();
        }

        try {
            // authenticate using token to get principle
            final Optional<P> principal = authenticator.authenticate(authToken);

            // set security context on request
            if (principal.isPresent()) {
                setSecurityContextOnRequest(requestContext, principal);
                return;
            }
        } catch (AuthenticationException e) {
            LOGGER.warn("Error authenticating credentials", e);
            throw unauthorizedWebException();
        }

        throw unauthorizedWebException();
    }

    private void setSecurityContextOnRequest(final ContainerRequestContext requestContext, final Optional<P> principal) {
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return principal.get();
            }

            @Override
            public boolean isUserInRole(String role) {
                return authorizer.authorize(principal.get(), role);
            }

            @Override
            public boolean isSecure() {
                return requestContext.getSecurityContext().isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "TOKEN";
            }
        });
    }

    private WebApplicationException unauthorizedWebException() {
        return new WebApplicationException(unauthorizedHandler.buildResponse(prefix, realm));
    }

    public static class Builder<P extends Principal> extends AuthFilterBuilder<String, P, TokenAuthFilter<P>> {
        @Override
        protected TokenAuthFilter<P> newInstance() {
            return new TokenAuthFilter<>();
        }
    }
}
