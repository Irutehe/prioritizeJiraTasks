package emag.storyMuncher;

import java.util.List;

class Config {

    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;
    private String jiraHost;

    List<TestingResource> getTestingResources() {
        return testingResources;
    }

    void setTestingResources(List<TestingResource> testingResources) {
        this.testingResources = testingResources;
    }

    List<JiraSprint> getAvailableSprints() {
        return availableSprints;
    }

    void setAvailableSprints(List<JiraSprint> availableSprints) {
        this.availableSprints = availableSprints;
    }

    String getJiraHost() {
        return jiraHost;
    }
    
    void setJiraHost(String host) {this.jiraHost = host;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (JiraSprint item : availableSprints) {
            i++;
            sb.append("JiraSprint " + i + ": " + item + "\n");
        }
        for (TestingResource item : testingResources) {
            i++;
            sb.append("TestingResource " + i + ": " + item + "\n");
        }

        return sb.toString();
    }
}
