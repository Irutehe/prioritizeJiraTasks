public class MainApp {
    public static void main(String[] args) {
        BestTestingPath bestTestingPath = new BestTestingPath();
        bestTestingPath.initialize();
        for (TestingResource resource:bestTestingPath.getTestingResources()) {
            System.out.println("Name: " + resource.getName() + " Stories: " + resource.getStories());
        }
    }
}
