package emag.storyMuncher;

public class MainApp {
    public static void main(String[] args) {
        BestTestingPath bestTestingPath = new BestTestingPath();
        bestTestingPath.initialize();

        for (TestingResource resource:bestTestingPath.getTestingResources()) {
            System.out.println(
                    "Current Workload for Name: " + resource.getUserName()
                            + " Current Stories: " + resource.getCurrentStories()
                            + " Future Stories: " + resource.getFutureStories()
                            + " Workload: " + getHoursFromSeconds(resource.getWorkload()) + "h");
        }

        if(!bestTestingPath.getOverflowIssues().isEmpty()){
            System.out.println("Stories skipped because it takes more time to test than available time in sprint:");
            for (JiraStory jiraStory:bestTestingPath.getOverflowIssues()) {
                System.out.println(
                        jiraStory.getStoryId()
                                + " [" + jiraStory.getSprint()
                                + "], Workload: " + getHoursFromSeconds(jiraStory.getEstimatedTestingMinutes()) + "h");
            }
        }

        System.exit(0);
    }

    private static double getHoursFromSeconds(Integer workload) {
        return workload / 60 / 60;
    }
}
