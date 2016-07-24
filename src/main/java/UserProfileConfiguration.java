import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class UserProfileConfiguration extends Configuration {

    @Valid
    @NotNull
    private DatabaseConfiguration database = new DatabaseConfiguration();

    @JsonProperty("database")
    public DatabaseConfiguration getDatabase() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DatabaseConfiguration datbaseConfiguration) {
        this.database = datbaseConfiguration;
    }
}
