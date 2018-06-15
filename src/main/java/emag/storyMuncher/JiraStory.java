package emag.storyMuncher;

class JiraStory {

    private String storyId;
    private Integer estimatedTestingMinutes;
    private String sprint;
    private Double priority;

    String getStoryId() {
        return storyId;
    }

    JiraStory setStoryId(String storyId) {
        this.storyId = storyId;

        return this;
    }

    Integer getEstimatedTestingMinutes() {
        return estimatedTestingMinutes;
    }

    JiraStory setEstimatedTestingMinutes(Integer estimatedTestingMinutes) {
        this.estimatedTestingMinutes = estimatedTestingMinutes;

        return this;
    }

    String getSprint() {
        return sprint;
    }

    JiraStory setSprint(String sprint) {
        this.sprint = sprint;

        return this;
    }

    Double getPriority() {
        return priority;
    }

    JiraStory setPriority(Double priority) {
        this.priority = priority;

        return this;
    }

    @Override
    public String toString() {
        return "storyId: " + storyId +
                ", estimatedTestingMinutes: " + estimatedTestingMinutes +
                ", sprint: \"" + sprint + '\"' +
                ", priority: " + priority;
    }
}
