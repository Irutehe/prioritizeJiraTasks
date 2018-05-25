public class MainApp {
    public static void main(String[] args) {
        BestTestingPath bestTestingPath = new BestTestingPath();
        bestTestingPath.initialize();
        for (TestingResource resource:bestTestingPath.getTestingResources()) {
            System.out.println(
                    "Current Workload for Name: " + resource.getUserName()
                            + " Current Stories: " + resource.getCurrentStories()
                            + " Workload: " + resource.getWorkload());
        }
    }
}
