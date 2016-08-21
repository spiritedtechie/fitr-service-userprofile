import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserProfileConfiguration extends Configuration {

    @Valid
    @NotNull
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @NotNull
    @NotEmpty
    private String authBaseUrl;

    @JsonProperty("database")
    public DatabaseConfiguration getDatabase() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DatabaseConfiguration datbaseConfiguration) {
        this.database = datbaseConfiguration;
    }

    @JsonProperty
    public String getAuthBaseUrl() {
        return authBaseUrl;
    }

    @JsonProperty
    public void setAuthBaseUrl(String url) {
        this.authBaseUrl = url;
    }

}
