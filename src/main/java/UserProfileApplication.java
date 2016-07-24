import com.mongodb.DB;
import com.mongodb.MongoClient;
import io.dropwizard.Application;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.filter.LoggingFilter;
import resource.UserProfileResource;

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

        DB db = setupMongoDB(configuration, environment);

        UserProfileResource loginResource = new UserProfileResource(db);

        environment.jersey().register(new LoggingFilter(getLogger(LoggingFilter.class.getName()), true));
        environment.jersey().register(loginResource);
    }

    private DB setupMongoDB(UserProfileConfiguration configuration, Environment environment) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient(configuration.getDatabase().getHost(), configuration.getDatabase().getPort());
        DB db = mongoClient.getDB(configuration.getDatabase().getName());
        MongoManaged mongoManaged = new MongoManaged(mongoClient);
        environment.lifecycle().manage(mongoManaged);
        return db;
    }
}
