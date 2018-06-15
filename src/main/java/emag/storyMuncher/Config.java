package emag.storyMuncher;

import java.util.List;

class Config {

    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;

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
