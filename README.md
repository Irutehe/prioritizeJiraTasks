# prioritizeJiraTasks

This app will help you prioritize testing tasks, giving you a best story points per sprint ratiu, should be usable to teams that work in sprints and use shared QA resources.

##Premises:
- Each QA will only work on one task;
- Estimated Time (this can be altered inside the JiraQueries) should be set on the testing sub-task;
- Tersting task should have Type: Testing task (this can be altered inside the JiraQueries);
- Story should have "Ready for testing" status;
- Story should be assigned to a sprint, and the end date of the sprint should be in the future;
- TODO - Story priority will be taken into consideration in a later version


##Algorithm will take into consideration:

##Task related:
- ETT - Estimated testing time in seconds
- TP - TODO Task priority
- SP - Story points

##Resources available:
- TUSE - time until sprint end in seconds, for now managed manually in the config.json file
- TR – testing resources, for now managed manually in the config.json file

#Priority =  (TUSE - ETT) / SP
- Stories with negative numbers will be skipped, because they do not fit the current sprint.
- Stories with priorities >= 0 will be taken into consideration with 0 being the top priority.

#How to use:
1. Compile the app on your local machine, where you can access Jira
2. Run this command into the console in the folder where the app with .jar is located: java -jar testingPath-0.1-alpha-jar-with-dependencies.jar
3. On the first run, in the same folder the config file will be generated(config.json).
4. You will need to edit this(config.json) file manually for TUSE and TR.
    - example: Today is 15-06-2018
        - Field name: “SCM-Γ Sprint 134 (11.06-25.06)“
        - Field remainingSeconds: 288000 - Should be calculated like this: Remaining days 10 * 8 hours per day * 60 min * 60 sec = 288000 sec
5.  The result of the run will be displayed for each QA, with stories in work and future stories, in the order to be taken, also you will be able to see the workload. If the workload is 0 that means something is not setup correctly on the testing task.

Good luck, have fun and if you find any bugs / improvements  please let us know.
