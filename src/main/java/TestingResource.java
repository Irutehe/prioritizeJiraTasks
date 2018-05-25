import java.util.ArrayList;

public class TestingResource {
    private String name;
    private Integer workload;
    private ArrayList<String> stories=new ArrayList<>();

    public String getStories() {
        return String.join(",", this.stories);
    }

    public TestingResource addStory(String stories) {
        this.stories.add(stories);
        return this;
    }

    public TestingResource setName(String name) {
        this.name = name;
        return this;
    }

    public TestingResource setWorkload(Integer workload) {
        this.workload = workload;

        return this;
    }

    public String getName() {
        return name;
    }

    public Integer getWorkload() {
        return workload;
    }
}
