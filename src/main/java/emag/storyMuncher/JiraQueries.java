package emag.storyMuncher;

import com.atlassian.jira.rest.client.api.domain.SearchResult;

import java.util.List;

class JiraQueries {

    static final String ISSUE_FIELD_REMAINING_ESTIMATE = "timeestimate";
    static final String ISSUE_FIELD_STORY_POINTS = "customfield_10023";
    static final Long ISSUE_TYPE_TESTING_TASK = Long.valueOf("15");
    private JiraConnection jira = new JiraConnection();

    SearchResult getSearchResultReadyForTestingIssues(JiraSprint sprint) {
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

    SearchResult getIssuesByKey(List<String> issueKeys) {
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

    SearchResult getSearchResultForOpenTestingIssues(TestingResource qaEngineer) {
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
}
