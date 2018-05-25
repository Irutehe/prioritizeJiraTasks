import java.util.ArrayList;

public class TestingResource {
    private String userName;
    private Integer workload;
    private ArrayList<String> currentStories =new ArrayList<>();

    public String getCurrentStories() {
        return String.join(",", currentStories);
    }

    public TestingResource addStory(String stories) {
        currentStories.add(stories);
        return this;
    }

    public TestingResource setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public TestingResource setWorkload(Integer workload) {
        this.workload = workload;

        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getWorkload() {
        return workload;
    }
}
