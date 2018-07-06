package emag.storyMuncher;

class JiraStory {

    private String storyId;
    private Integer estimatedTestingMinutes;
    private JiraSprint sprint;
    private Double priority;
    private Double storyPoints;

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

    JiraSprint getSprint() {
        return sprint;
    }

    JiraStory setSprint(JiraSprint sprint) {
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

    public Double getStoryPoints() {
        return this.storyPoints;
    }

    public JiraStory setStoryPoints(Double storyPoints) {
        this.storyPoints = storyPoints;

        return this;
    }
}
