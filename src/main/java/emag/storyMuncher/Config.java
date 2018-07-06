package emag.storyMuncher;

import java.util.ArrayList;
import java.util.List;

class Config {

    private List<TestingResource> testingResources;
    private String jiraHost;
    private String jiraUser;
    private String jiraPassword;
    private Integer jiraBoardId;

    List<TestingResource> getTestingResources() {
        return testingResources;
    }

    void setTestingResources(List<TestingResource> testingResources) {
        this.testingResources = testingResources;
    }

    String getJiraHost() {
        return jiraHost;
    }
    
    void setJiraHost(String host) {this.jiraHost = host;}

    String getJiraUser() {
        return jiraUser;
    }

    void setJiraUser(String jiraUser) {this.jiraUser = jiraUser;}

    String getJiraPassword() { return jiraPassword; }

    void setJiraPassword(String jiraPassword) {this.jiraPassword = jiraPassword;}

    Integer getJiraBoardId() {
        return jiraBoardId;
    }

    void setJiraBoardId(int jiraBoardId) {this.jiraBoardId = jiraBoardId;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        for (TestingResource item : testingResources) {
            i++;
            sb.append("TestingResource " + i + ": " + item + "\n");
        }

        return sb.toString();
    }

    static Config getConfig(){
        JsonFileIO objectIO = new JsonFileIO();
        Config config = new Config();
        if (!objectIO.checkConfigFile()) {
            Config.writeDefaultConfigToFile();
        } else {
            config = objectIO.ReadObjectFromFile();
        }
        return config;
    }

    private static void writeDefaultConfigToFile() {

        TestingResource testingResource2 = new TestingResource().setUserName("user.name");
        List<TestingResource> testingResources = new ArrayList<>();
        testingResources.add(testingResource2);

        Config config = new Config();
        config.setJiraHost("https://jira.host");
        config.setJiraUser("jiraUser");
        config.setJiraPassword("jiraPassword");
        config.setJiraBoardId(8);
        config.setTestingResources(testingResources);

        JsonFileIO objectIO = new JsonFileIO();
        objectIO.WriteObjectToFile(config);
    }
}
