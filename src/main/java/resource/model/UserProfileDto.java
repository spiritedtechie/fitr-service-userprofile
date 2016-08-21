package resource.model;

import model.Goal;
import model.UserProfile;

import java.util.ArrayList;
import java.util.List;

public class UserProfileDto {

    private String favouriteActivity;

    private List<GoalDto> goals;

    public String getFavouriteActivity() {
        return favouriteActivity;
    }

    public void setFavouriteActivity(String favouriteActivity) {
        this.favouriteActivity = favouriteActivity;
    }

    public List<GoalDto> getGoals() {
        return goals;
    }

    public void setGoals(List<GoalDto> goals) {
        this.goals = goals;
    }

    public UserProfile convertToUserProfile() {

        List<Goal> goals = new ArrayList<>();
        for (GoalDto goalDto : this.goals) {
            Goal goal = new Goal();
            goal.setType(goalDto.getType());
            goal.setMetric(goalDto.getMetric());
            goals.add(goal);
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setFavouriteActivity(this.favouriteActivity);
        userProfile.setGoals(goals);

        return userProfile;
    }

    public static UserProfileDto dtoFrom(UserProfile userProfile) {

        List<GoalDto> goalDtos = new ArrayList<>();
        for (Goal g : userProfile.getGoals()) {
            GoalDto goalDto = new GoalDto();
            goalDto.setType(g.getType());
            goalDto.setMetric(g.getMetric());
            goalDtos.add(goalDto);
        }

        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setFavouriteActivity(userProfile.getFavouriteActivity());
        userProfileDto.setGoals(goalDtos);

        return userProfileDto;
    }
}