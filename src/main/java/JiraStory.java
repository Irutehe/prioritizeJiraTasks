public class JiraStory {
    private String storyId;
    private Integer estimatedTestingMinutes;
    private String sprint;
    private float priority;

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public Integer getEstimatedTestingMinutes() {
        return estimatedTestingMinutes;
    }

    public void setEstimatedTestingMinutes(Integer estimatedTestingMinutes) {
        this.estimatedTestingMinutes = estimatedTestingMinutes;
    }

    public String getSprint() {
        return sprint;
    }

    public void setSprint(String sprint) {
        this.sprint = sprint;
    }

    public float getPriority() {
        return priority;
    }

    public void setPriority(float priority) {
        this.priority = priority;
    }
}
