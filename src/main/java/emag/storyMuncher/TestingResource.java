package emag.storyMuncher;

import java.util.ArrayList;

class TestingResource {
    private String userName;
    private Integer workload = 0;
    private ArrayList<String> currentStories = new ArrayList<>();
    private ArrayList<String> futureStories = new ArrayList<>();

    String getCurrentStories() {
        return String.join(",", currentStories);
    }

    String getFutureStories() {
        return String.join(",", futureStories);
    }

    void addCurrentStory(String stories) {
        currentStories.add(stories);
    }

    void addFutureStory(String stories) {
        futureStories.add(stories);
    }

    TestingResource setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    TestingResource addWorkload(Integer workload) {
        this.workload += workload;

        return this;
    }

    String getUserName() {
        return userName;
    }

    Integer getWorkload() {
        return workload;
    }
}
