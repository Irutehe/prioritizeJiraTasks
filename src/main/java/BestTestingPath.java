import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class BestTestingPath {
    private JiraStory[] availableStories;
    private List<TestingResource> testingResources;
    private JiraSprint[] availableSprints;

    BestTestingPath(){
       testingResources = new ArrayList<>();
    }

    public List<TestingResource> getTestingResources() {
        return testingResources;
    }

    public void initialize() {
        this.setTestingResources();
        this.setAvailableStories();
    }

    private void setTestingResources() {
        //TODO: Get the testing resources from Jira and assign them
        JiraConnection jira = new JiraConnection();
        SearchResult result = null;
        String name = "adrian.bratulescu";
        try {
            result = jira.getIssues("assignee = " + name + " AND resolution = Unresolved");
        } catch (Exception ex) {
            System.out.println("URI exception " + ex.getMessage());
        }
        for (Issue issue: result.getIssues()) {
            TestingResource testingResource = new TestingResource();
            testingResource.setName(issue.getAssignee().getDisplayName())
                    .setWorkload(0)
                    .addStory(issue.getKey());

            for(IssueField field: issue.getFields()) {
                if(field.getId().equals("aggregatetimeoriginalestimate") && field.getValue() != null) {
                    testingResource.setWorkload(testingResource.getWorkload() + ((Integer)field.getValue()));
                }
            }
            testingResources.add(testingResource);
        }
    }

    private void setAvailableStories() {
        //TODO: loop through jira stories and assign them to the objects

        //TODO: assign the hours until end of each sprint
    }

    public void getNextTestingResource() {
        int minWorkload = 0;
        for (TestingResource testingResource : testingResources) {
            if (minWorkload > testingResource.getWorkload() || minWorkload == 0)
                minWorkload = testingResource.getWorkload();
        }
    }
}