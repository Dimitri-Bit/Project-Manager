package me.dimitri.project_manager.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import me.dimitri.project_manager.model.User;
import me.dimitri.project_manager.service.UserService;
import me.dimitri.project_manager.util.HashUtil;
import me.dimitri.project_manager.util.StringValidationUtil;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Singleton
public class AuthenticationProviderUserPassword implements AuthenticationProvider {

    /*
    This is Micronaut JWT security implementation. Here we can actually do the logic of
    logging in and let Micronaut handle all the token generation and cookie storage. Now it is a bit
    weird working with this, and I'm not too sure how to change some redirect stuff so the front-end
    of the login page is probably going to work differently then the rest of the app.
     */

    private final UserService userService;

    @Inject
    public AuthenticationProviderUserPassword(UserService userService) {
        this.userService = userService;
    }


    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest, AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            String identity = authenticationRequest.getIdentity().toString();
            String secret = authenticationRequest.getSecret().toString();

            if (StringValidationUtil.isValidEmail(identity)) {
                User user = userService.findUserByEmail(identity);

                if (user != null) {
                    if (identity.equals(user.getEmail())) {
                        if (HashUtil.verifyHash(secret, user.getPassword())) {
                            emitter.next(AuthenticationResponse.success((String) authenticationRequest.getIdentity())); // All Good
                            emitter.complete();
                        } else {
                            reject("Neuspesan login", emitter);
                        }
                    } else {
                        reject("Kako?", emitter);
                    }
                } else {
                    reject("Korisnik pod tom Email adresom ne postoji", emitter);
                }
            } else {
                reject("Invalidan format Email adrese", emitter);
            }
        });
    }

    private void reject(String message, FluxSink<AuthenticationResponse> emitter) {
        emitter.next(AuthenticationResponse.failure(message));
        emitter.complete();
    }
}
