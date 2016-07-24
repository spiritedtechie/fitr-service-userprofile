package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class UserProfile {

    private String id;

    private String favouriteActivity;

    private List<Goal> goals;

    @JsonProperty(value = "_id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public void setFavouriteActivity(String favouriteActivity) {
        this.favouriteActivity = favouriteActivity;
    }

    public String getFavouriteActivity() {
        return favouriteActivity;
    }
}
