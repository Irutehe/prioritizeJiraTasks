import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BestTestingPath {
    private static final String ISSUE_TYPE_TESTING_TASK = "15";
    private static final String ISSUE_FIELD_REMAINING_ESTIMATE = "timeestimate";
    private List<JiraStory> priorityCalculatedIssues;
    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;
    private JiraConnection jira = new JiraConnection();

    BestTestingPath() {
        testingResources = new ArrayList<>();
        availableSprints = new ArrayList<>();
    }

    public List<TestingResource> getTestingResources() {
        return testingResources;
    }

    private void setTestingResources() {
        TestingResource testingResource2 = new TestingResource().setUserName("gabriela.preda").setWorkload(0);
        testingResources.add(testingResource2);
        TestingResource testingResource3 = new TestingResource().setUserName("ciprian.nitu").setWorkload(0);
        testingResources.add(testingResource3);
        TestingResource testingResource0 = new TestingResource().setUserName("bogdan.popa1").setWorkload(0);
        testingResources.add(testingResource0);
        TestingResource testingResource1 = new TestingResource().setUserName("armando.gavrila").setWorkload(0);
        testingResources.add(testingResource1);
    }

    public void initialize() {
        setTestingResources();
        getJiraDetailsForTestingResources();
        sortTestingResourcesByWorkload();
        getReadyForTestingIssues();
    }

    private void getJiraDetailsForTestingResources() {
        for (TestingResource qaEngineer : testingResources) {
            SearchResult result = getSearchResultForOpenTestingIssues(qaEngineer);
            if (result != null) {
                setDetailsForTestingResource(qaEngineer, result);
            }
        }
    }

    private void getReadyForTestingIssues() {
        //todo remove the code bellow when we are able to get sprints
        JiraSprint sprint1 = new JiraSprint().setName("SCM-Γ Sprint 132 (14.05-28.05)").setRemainingMinutes(20000);
        availableSprints.add(sprint1);

        for (JiraSprint sprint: availableSprints) {
            SearchResult result = getSearchResultReadyForTestingIssues(sprint);
            if (result != null) {
                for (Issue issue : result.getIssues()) {
                    calculatePriority(sprint.getRemainingMinutes(), issue);
                }
            }
        }
    }

    private float calculatePriority(int remainingMinutes, Issue issue) {
        int storyEstimatedMinutes = 0;
        IssueField field = issue.getField(ISSUE_FIELD_REMAINING_ESTIMATE);
        if(field != null) {
            storyEstimatedMinutes = ((Integer) field.getValue());
        }
        return (remainingMinutes - storyEstimatedMinutes);//todo get the storypoints from story
    }

    private SearchResult getSearchResultReadyForTestingIssues(JiraSprint sprint) {
        SearchResult result = null;
        try {
            result = jira.getIssues(
                    "status = \"Ready for Testing\" " +
                            "AND project = \"Supply Chain Management\" " +
                            "AND sprint = \"SCM-Γ Sprint 132 (14.05-28.05)\" "
            );
        } catch (Exception ex) {
            System.out.println("URI exception " + ex.getMessage());
        }
        return result;
    }

    private SearchResult getSearchResultForOpenTestingIssues(TestingResource qaEngineer) {
        SearchResult result = null;
        try {
            result = jira.getIssues(
                    "assignee = "
                            + qaEngineer.getUserName()
                            + " AND resolution = Unresolved AND issueType = "
                            + ISSUE_TYPE_TESTING_TASK
                            + " AND project = \"Supply Chain Management\" "
            );
        } catch (Exception ex) {
            System.out.println("URI exception " + ex.getMessage());
        }
        return result;
    }

    private void setDetailsForTestingResource(TestingResource qaEngineer, SearchResult result) {
        for (Issue issue : result.getIssues()) {
            qaEngineer.addStory(issue.getKey());
            IssueField field = issue.getField(ISSUE_FIELD_REMAINING_ESTIMATE);
            if (field != null && field.getValue() != null) {
                qaEngineer.setWorkload(qaEngineer.getWorkload() + ((Integer) field.getValue()));
            }
//            for (IssueField field : issue.getFields()) {
//                if (field.getId().equals(ISSUE_FIELD_REMAINING_ESTIMATE) && field.getValue() != null) {
//                    qaEngineer.setWorkload(qaEngineer.getWorkload() + ((Integer) field.getValue()));
//                }
//            }
        }
    }

    private void sortTestingResourcesByWorkload() {
        testingResources.sort(Comparator.comparingInt(TestingResource::getWorkload));
    }
}