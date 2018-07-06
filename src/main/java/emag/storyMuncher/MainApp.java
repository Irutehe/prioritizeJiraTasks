package emag.storyMuncher;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        BestTestingPath bestTestingPath = new BestTestingPath();
        bestTestingPath.initialize();
        DisplayResult displayResult = new DisplayResult();
        displayResult.setTable(createTable(bestTestingPath))
                .setSkippedText(skippedTextArea(bestTestingPath.getOverflowIssues()))
                .show();
    }

    private static JTextArea skippedTextArea(List<JiraStory> skippedStories) {
        String storiesIgnored = "Stories skipped because it takes more time to test than available time in sprint:";

        for (JiraStory jiraStory : skippedStories) {
            storiesIgnored = storiesIgnored.concat(
                    jiraStory.getStoryId()
                            + " [" + jiraStory.getSprint().getName()
                            + "], Workload: " + getHoursFromSeconds(jiraStory.getEstimatedTestingMinutes()) + "h");
        }

        return new JTextArea(storiesIgnored);
    }

    private static JTable createTable(BestTestingPath bestTestingPath) {
        String[] columnNames = {"Username", "Current Stories", "Future Stories", "Workload(h)"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        for (TestingResource resource : bestTestingPath.getTestingResources()) {
            Object[] row = {
                    resource.getUserName(),
                    resource.getCurrentStories(),
                    resource.getFutureStories(),
                    String.valueOf(getHoursFromSeconds(resource.getWorkload()))
            };
            model.addRow(row);
        }

        return table;
    }

    private static double getHoursFromSeconds(Integer workload) {
        return workload / 60 / 60;
    }
}
