import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import java.util.ArrayList;
import java.util.List;

public class BestTestingPath {
    private static final String ISSUE_TYPE_TESTING_TASK = "15";
    private static final String ISSUE_FIELD_REMAINING_ESTIMATE = "timeestimate";
    private JiraStory[] availableStories;
    private List<TestingResource> testingResources;
    private JiraSprint[] availableSprints;

    BestTestingPath() {
        testingResources = new ArrayList<>();
    }

    public List<TestingResource> getTestingResources() {
        return testingResources;
    }

    private void setTestingResources() {
        TestingResource testingResource0 = new TestingResource().setUserName("bogdan.popa1").setWorkload(0);
        testingResources.add(testingResource0);
        TestingResource testingResource1 = new TestingResource().setUserName("armando.gavrila").setWorkload(0);
        testingResources.add(testingResource1);
        TestingResource testingResource2 = new TestingResource().setUserName("gabriela.preda").setWorkload(0);
        testingResources.add(testingResource2);
        TestingResource testingResource3 = new TestingResource().setUserName("ciprian.nitu").setWorkload(0);
        testingResources.add(testingResource3);
    }

    public void initialize() {
        setTestingResources();
        getJiraDetailsForTestingResources();
    }

    private void getJiraDetailsForTestingResources() {
        JiraConnection jira = new JiraConnection();

        for (TestingResource qaEngineer : testingResources) {
            SearchResult result = getSearchResult(jira, qaEngineer);
            if (result != null) {
                setDetailsForTestingResource(qaEngineer, result);
            }
        }
    }

    private SearchResult getSearchResult(JiraConnection jira, TestingResource qaEngineer) {
        SearchResult result = null;
        try {
            result = jira.getIssues(
                    "assignee = "
                            + qaEngineer.getUserName()
                            + " AND resolution = Unresolved AND issueType = "
                            + ISSUE_TYPE_TESTING_TASK
            );// AND  Testing
        } catch (Exception ex) {
            System.out.println("URI exception " + ex.getMessage());
        }
        return result;
    }

    private void setDetailsForTestingResource(TestingResource qaEngineer, SearchResult result) {
        for (Issue issue : result.getIssues()) {
            qaEngineer.addStory(issue.getKey());
            for (IssueField field : issue.getFields()) {
                if (field.getId().equals(ISSUE_FIELD_REMAINING_ESTIMATE) && field.getValue() != null) {
                    qaEngineer.setWorkload(qaEngineer.getWorkload() + ((Integer) field.getValue()));
                }
            }
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