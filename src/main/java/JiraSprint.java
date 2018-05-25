import com.atlassian.jira.rest.client.api.domain.Issue;

import java.util.List;

public class JiraSprint {
    private String name;
    private Integer remainingMinutes;
    private List<Issue> readyForTestingIssuesList;

    public String getName() {
        return name;
    }

    public JiraSprint setName(String name) {
        this.name = name;

        return this;
    }

    public Integer getRemainingMinutes() {
        return remainingMinutes;
    }

    public JiraSprint setRemainingMinutes(Integer remainingMinutes) {
        this.remainingMinutes = remainingMinutes;

        return this;
    }

    public List<Issue> getReadyForTestingIssuesList() {
        return readyForTestingIssuesList;
    }

    public void setReadyForTestingIssuesList(List<Issue> readyForTestingIssuesList) {
        this.readyForTestingIssuesList = readyForTestingIssuesList;
    }
}
