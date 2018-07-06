package emag.storyMuncher;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Subtask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class BestTestingPath {
    private List<JiraStory> priorityCalculatedIssues;
    private List<TestingResource> testingResources;
    private List<JiraSprint> availableSprints;
    private JiraQueries jiraQueries;
    private List<JiraStory> overflowIssues;

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

        loadConfigFromFile();

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

    private void setAvailableSprints(List<JiraSprint> availableSprints) {
        this.availableSprints = availableSprints;
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

    private void loadConfigFromFile() {
        JsonFileIO objectIO = new JsonFileIO();
        if (!objectIO.checkConfigFile()) {
            writeConfigToFile();
        } else {
            Config config = objectIO.ReadObjectFromFile();
            setAvailableSprints(config.getAvailableSprints());
            setTestingResources(config.getTestingResources());
            setJiraQueries(config.getJiraHost());
        }
    }

    private void writeConfigToFile() {
        //todo remove the code bellow when we are able to get sprints
        JiraSprint sprint1 = new JiraSprint().setName("SCM-Σ Sprint 75 (04.07-1.10)").setRemainingSeconds(2000000);
        JiraSprint sprint2 = new JiraSprint().setName("SCM-Δ Sprint 82 (02.07- 16.07)").setRemainingSeconds(200000);
        JiraSprint sprint3 = new JiraSprint().setName("SCM-Γ Sprint 135 (26.06 -9.07)").setRemainingSeconds(200000);
        availableSprints.add(sprint1);
        availableSprints.add(sprint2);
        availableSprints.add(sprint3);

        TestingResource testingResource2 = new TestingResource().setUserName("gabriela.preda");
        testingResources.add(testingResource2);
        TestingResource testingResource3 = new TestingResource().setUserName("ciprian.nitu");
        testingResources.add(testingResource3);
        TestingResource testingResource0 = new TestingResource().setUserName("bogdan.popa1");
        testingResources.add(testingResource0);
        TestingResource testingResource1 = new TestingResource().setUserName("armando.gavrila");
        testingResources.add(testingResource1);
        TestingResource testingResource4 = new TestingResource().setUserName("alina.ene");
        testingResources.add(testingResource4);

        Config config = new Config();
        config.setAvailableSprints(availableSprints);
        config.setTestingResources(testingResources);
        config.setJiraHost("https://jira.emag.network");
        setJiraQueries(config.getJiraHost());

        JsonFileIO objectIO = new JsonFileIO();
        objectIO.WriteObjectToFile(config);
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