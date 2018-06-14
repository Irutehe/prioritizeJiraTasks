package emag.storyMuncher;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class BestTestingPath {
    private static final Long ISSUE_TYPE_TESTING_TASK = Long.valueOf("15");
    private static final String ISSUE_FIELD_REMAINING_ESTIMATE = "timeestimate";
    private static final String ISSUE_FIELD_STORY_POINTS = "customfield_10023";
    private List<JiraStory> priorityCalculatedIssues;
    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;
    private JiraConnection jira = new JiraConnection();

    BestTestingPath() {
        testingResources = new ArrayList<>();
        availableSprints = new ArrayList<>();
        priorityCalculatedIssues = new ArrayList<>();
    }

    List<TestingResource> getTestingResources() {
        return testingResources;
    }

    private void setTestingResources() {
        TestingResource testingResource2 = new TestingResource().setUserName("gabriela.preda");
        testingResources.add(testingResource2);
        TestingResource testingResource3 = new TestingResource().setUserName("ciprian.nitu");
        testingResources.add(testingResource3);
        TestingResource testingResource0 = new TestingResource().setUserName("bogdan.popa1");
        testingResources.add(testingResource0);
        TestingResource testingResource1 = new TestingResource().setUserName("armando.gavrila");
        testingResources.add(testingResource1);
    }

    void initialize() {
        setTestingResources();
        for (TestingResource qaEngineer : testingResources) {
            SearchResult result = getSearchResultForOpenTestingIssues(qaEngineer);
            if (result != null) {
                setDetailsForTestingResource(qaEngineer, result);
            }
        }
        setAvailableSprints();
        for (JiraSprint sprint : availableSprints) {
            populatePriorityCalculatedIssues(sprint);
        }
        assignIssuesToEngineers();
    }

    private void assignIssuesToEngineers() {
        while (!priorityCalculatedIssues.isEmpty()) {
            sortPriorityCalculatedIssues();
            JiraStory jiraStory = priorityCalculatedIssues.get(0);
            sortTestingResourcesByWorkload();
            testingResources.get(0).addWorkload(jiraStory.getEstimatedTestingMinutes()).addFutureStory(jiraStory.getStoryId());
            priorityCalculatedIssues.remove(0);
        }
    }

    private void sortPriorityCalculatedIssues() {
        priorityCalculatedIssues.sort(Comparator.comparingDouble(JiraStory::getPriority));
    }

    private void setAvailableSprints() {
        //todo remove the code bellow when we are able to get sprints
        JiraSprint sprint1 = new JiraSprint().setName("SCM-Σ Sprint 74 (07.05-30.06)").setRemainingMinutes(30000);
        JiraSprint sprint2 = new JiraSprint().setName("SCM-Δ Sprint 80 (04.06-18.06)").setRemainingMinutes(40000);
        JiraSprint sprint3 = new JiraSprint().setName("SCM-Γ Sprint 134 (11.06-25.06)").setRemainingMinutes(50000);
        availableSprints.add(sprint1);
        availableSprints.add(sprint2);
        availableSprints.add(sprint3);
    }

    private void populatePriorityCalculatedIssues(JiraSprint sprint) {
        SearchResult result = getSearchResultReadyForTestingIssues(sprint);
        if (result != null) {
            for (Issue issue : result.getIssues()) {
                int storyEstimatedMinutes = getStoryEstimatedMinutes(issue);
                Double storyPoints = getStoryPoints(issue);
                Double priority = calculatePriority(sprint.getRemainingMinutes(), storyPoints, storyEstimatedMinutes);
                //skipping stories that do not fit the current sprint TODO: make another list in order to show them
                if (priority >= 0) {
                    JiraStory jiraStory = new JiraStory();
                    jiraStory.setStoryId(issue.getKey())
                            .setSprint(sprint.getName())
                            .setPriority(priority)
                            .setEstimatedTestingMinutes(storyEstimatedMinutes);
                    priorityCalculatedIssues.add(jiraStory);
                }
            }
        }
    }

    private Double calculatePriority(int remainingMinutes, Double storyPoints, int storyEstimatedMinutes) {
        return (remainingMinutes - storyEstimatedMinutes) / storyPoints;
    }

    private int getStoryEstimatedMinutes(Issue issue) {
        int storyEstimatedMinutes = 0;
        Iterable<Issue> testingIssueList = getSubtaskTestingIssue(issue);
        for (Issue testingIssue : testingIssueList) {
            storyEstimatedMinutes = getTaskEstimatedMinutes(storyEstimatedMinutes, testingIssue);
        }
        return storyEstimatedMinutes;
    }

    private int getTaskEstimatedMinutes(int storyEstimatedMinutes, Issue testingIssue) {
        IssueField field = testingIssue.getField(ISSUE_FIELD_REMAINING_ESTIMATE);
        if (field != null && field.getValue() != null) {
            storyEstimatedMinutes += ((Integer) field.getValue());
        }
        return storyEstimatedMinutes;
    }

    private Iterable<Issue> getSubtaskTestingIssue(Issue issue) {
        Iterable<Subtask> subtasks = issue.getSubtasks();
        List<String> issueKeys = new ArrayList<>();
        if (subtasks != null) {
            for (Subtask subtask : subtasks) {
                if (subtask.getIssueType().getId().equals(ISSUE_TYPE_TESTING_TASK)) {
                    issueKeys.add(subtask.getIssueKey());
                }
            }
        }

        SearchResult result = getIssuesByKey(issueKeys);
        return result.getIssues();
    }

    private Double getStoryPoints(Issue issue) {
        Double storyPoints = 0.00;
        IssueField field = issue.getField(ISSUE_FIELD_STORY_POINTS);
        if (field != null && field.getValue() != null) {
            storyPoints = ((Double) field.getValue());
        }
        return storyPoints;
    }

    private SearchResult getSearchResultReadyForTestingIssues(JiraSprint sprint) {
        SearchResult result = null;
        try {
            result = jira.getIssues(
                    "status = \"Ready for Testing\" " +
                            "AND project = \"Supply Chain Management\" " +
                            "AND sprint = \"" + sprint.getName() + "\""
            );
        } catch (Exception ex) {
            System.out.println("URI exception " + ex.getMessage());
        }
        return result;
    }

    private SearchResult getIssuesByKey(List<String> issueKeys) {
        SearchResult result = null;
        try {
            result = jira.getIssues(
                    "key = " + String.join(" OR key = ", issueKeys)
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
            qaEngineer.addCurrentStory(issue.getKey());
            IssueField field = issue.getField(ISSUE_FIELD_REMAINING_ESTIMATE);
            if (field != null && field.getValue() != null) {
                qaEngineer.addWorkload(qaEngineer.getWorkload() + ((Integer) field.getValue()));
            }
        }
    }

    private void sortTestingResourcesByWorkload() {
        testingResources.sort(Comparator.comparingInt(TestingResource::getWorkload));
    }
}