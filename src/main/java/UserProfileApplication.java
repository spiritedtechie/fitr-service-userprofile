import auth.AuthTokenAuthenticator;
import auth.TokenAuthFilter;
import auth.UserRoleAuthorizer;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import model.User;
import okhttp3.OkHttpClient;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import resource.UserProfileResource;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.UnknownHostException;

import static java.util.logging.Logger.getLogger;

public class UserProfileApplication extends Application<UserProfileConfiguration> {

    public static void main(String[] args) throws Exception {
        new UserProfileApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<UserProfileConfiguration> bootstrap) {
        bootstrap.addBundle(new MultiPartBundle());
    }

    @Override
    public String getName() {
        return "user-auth-service";
    }

    public void run(UserProfileConfiguration configuration,
                    Environment environment) throws Exception {

        // Database
        DB db = setupMongoDB(configuration, environment);


        // Resources
        UserProfileResource loginResource = new UserProfileResource(db);

        environment.jersey().register(new LoggingFilter(getLogger(LoggingFilter.class.getName()), false));
        environment.jersey().register(loginResource);

        // register auth framework
        registerAuth(environment, configuration);
    }

    private DB setupMongoDB(UserProfileConfiguration configuration, Environment environment) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(configuration.getDatabase().getHost(), configuration.getDatabase().getPort());
        DB db = mongoClient.getDB(configuration.getDatabase().getName());
        MongoManaged mongoManaged = new MongoManaged(mongoClient);
        environment.lifecycle().manage(mongoManaged);
        return db;
    }

    private OkHttpClient buildClient() throws Exception {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    // suitable for dev to not check SSL peer (SSLPeerUnverifiedException)
                    // not needed if using CA signed cert (instead of self signed)
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });

        return httpClient.build();
    }

    private void registerAuth(Environment environment, UserProfileConfiguration configuration) throws Exception {

        Retrofit retrofit = new Retrofit.Builder()
                .client(buildClient())
                .baseUrl(configuration.getAuthBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();


        final TokenAuthFilter<User> tokenAuthFilter = new TokenAuthFilter.Builder<User>()
                .setAuthorizer(
                        new UserRoleAuthorizer()
                ).setAuthenticator(
                        new AuthTokenAuthenticator(retrofit)
                ).buildAuthFilter();

        environment.jersey().register(new AuthDynamicFeature(tokenAuthFilter));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }

}
