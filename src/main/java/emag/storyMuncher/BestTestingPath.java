package emag.storyMuncher;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;
import com.google.gson.JsonObject;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.swing.*;
import java.io.IOException;
import java.util.*;

class BestTestingPath {
    private List<JiraStory> priorityCalculatedIssues;
    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;
    private JiraQueries jiraQueries;
    private List<JiraStory> overflowIssues;
    private Config config;

    BestTestingPath() {
        testingResources = new ArrayList<>();
        availableSprints = new ArrayList<>();
        priorityCalculatedIssues = new ArrayList<>();
        overflowIssues = new ArrayList<>();
    }

    List<TestingResource> getTestingResources() {
        return testingResources;
    }

    List<JiraStory> getOverflowIssues() {
        return overflowIssues;
    }

    private void setTestingResources(List<TestingResource> testingResources) {
        this.testingResources = testingResources;
    }

    void initialize() {

        config = Config.getConfig();
        setTestingResources(config.getTestingResources());
        setJiraQueries(config.getJiraHost());

        loadSprintsFromApi();

        for (TestingResource qaEngineer : testingResources) {
            SearchResult result = jiraQueries.getSearchResultForOpenTestingIssues(qaEngineer);
            if (result != null) {
                setDetailsForTestingResource(qaEngineer, result);
            }
        }

        assignIssuesToEngineers();
    }

    private void assignIssuesToEngineers() {
        for (JiraSprint sprint : availableSprints) {
            populatePriorityCalculatedIssues(sprint);
        }

        while (!priorityCalculatedIssues.isEmpty()) {
            JiraStory jiraStory = getTopPriorityJiraStory();
            TestingResource testingResource = getFirstAvailableTestingResource();
            testingResource.addWorkload(jiraStory.getEstimatedTestingMinutes()).addFutureStory(jiraStory.getStoryId());
            removeTopPriorityJiraStory();
            recalculatePriority();
        }
    }

    private void removeTopPriorityJiraStory() {
        priorityCalculatedIssues.remove(0);
    }

    private JiraStory getTopPriorityJiraStory() {
        sortPriorityCalculatedIssues();
        return priorityCalculatedIssues.get(0);
    }

    private TestingResource getFirstAvailableTestingResource() {
        sortTestingResourcesByWorkload();
        return testingResources.get(0);
    }

    private void sortPriorityCalculatedIssues() {
        priorityCalculatedIssues.sort(Comparator.comparingDouble(JiraStory::getPriority));
    }

    private void loadSprintsFromApi(){

        ApiClient apiClient = new ApiClient(config);
        this.availableSprints = apiClient.getSprints();
    }

    private void populatePriorityCalculatedIssues(JiraSprint sprint) {
        SearchResult result = jiraQueries.getSearchResultReadyForTestingIssues(sprint);
        if (result != null) {
            for (Issue issue : result.getIssues()) {
                int storyEstimatedMinutes = getStoryEstimatedMinutes(issue);
                Double storyPoints = getStoryPoints(issue);
                Double priority = calculatePriority(sprint.getRemainingSeconds(), storyPoints, storyEstimatedMinutes);

                JiraStory jiraStory = new JiraStory();
                jiraStory.setStoryId(issue.getKey())
                        .setSprint(sprint)
                        .setPriority(priority)
                        .setEstimatedTestingMinutes(storyEstimatedMinutes)
                        .setStoryPoints(storyPoints);

                if (priority >= 0) {
                    priorityCalculatedIssues.add(jiraStory);
                } else {
                    overflowIssues.add(jiraStory);
                }
            }
        }
    }

    private Double calculatePriority(int remainingSeconds, Double storyPoints, int storyEstimatedMinutes) {
        return (remainingSeconds - storyEstimatedMinutes) / storyPoints;
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
        IssueField field = testingIssue.getField(JiraQueries.ISSUE_FIELD_REMAINING_ESTIMATE);
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
                if (subtask.getIssueType().getId().equals(JiraQueries.ISSUE_TYPE_TESTING_TASK)) {
                    issueKeys.add(subtask.getIssueKey());
                }
            }
        }

        SearchResult result = jiraQueries.getIssuesByKey(issueKeys);
        return result.getIssues();
    }

    private Double getStoryPoints(Issue issue) {
        Double storyPoints = 0.00;
        IssueField field = issue.getField(JiraQueries.ISSUE_FIELD_STORY_POINTS);
        if (field != null && field.getValue() != null) {
            storyPoints = ((Double) field.getValue());
        }
        return storyPoints;
    }

    private void setDetailsForTestingResource(TestingResource qaEngineer, SearchResult result) {
        for (Issue issue : result.getIssues()) {
            qaEngineer.addCurrentStory(issue.getKey());
            IssueField field = issue.getField(JiraQueries.ISSUE_FIELD_REMAINING_ESTIMATE);
            if (field != null && field.getValue() != null) {
                qaEngineer.addWorkload(qaEngineer.getWorkload() + ((Integer) field.getValue()));
            }
        }
    }

    private void sortTestingResourcesByWorkload() {
        testingResources.sort(Comparator.comparingInt(TestingResource::getWorkload));
    }

    private void setJiraQueries(String jiraHost) {
        this.jiraQueries = new JiraQueries(jiraHost);
    }

    private void recalculatePriority() {
        TestingResource testingResource = getFirstAvailableTestingResource();
        for (JiraStory jiraStory : priorityCalculatedIssues) {
            Double priority = calculatePriority(
                    jiraStory.getSprint().getRemainingSeconds() - testingResource.getWorkload(),
                    jiraStory.getStoryPoints(),
                    jiraStory.getEstimatedTestingMinutes()
            );

            jiraStory.setPriority(priority);

            if (priority < 0) {
                priorityCalculatedIssues.remove(jiraStory);
                overflowIssues.add(jiraStory);
            }
        }
    }
}