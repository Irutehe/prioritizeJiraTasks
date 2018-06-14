package emag.storyMuncher;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

class JiraConnection {
    private JiraRestClient restClient;

    JiraConnection() {
        URI jiraServerUri = null;
        try {
            jiraServerUri = new URI("https://jira.emag.network");
        } catch (URISyntaxException ex) {
            System.out.println("URI exception " + ex.getMessage() + ex.getReason());
        }
        AnonymousAuthenticationHandler handler = new AnonymousAuthenticationHandler();
        restClient = new AsynchronousJiraRestClientFactory().create(jiraServerUri, handler);
    }

    SearchResult getIssues(String jql) throws Exception {
        Promise searchResultPromise = restClient.getSearchClient().searchJql(jql);
        return Optional.ofNullable((SearchResult) searchResultPromise.claim()).orElseThrow(() -> new Exception("No such issue"));
    }

}

