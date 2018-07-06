package emag.storyMuncher;

import org.joda.time.DateTime;

class JiraSprint {

    private Integer id;
    private String name;
    private String goal;
    private DateTime startDate;
    private DateTime endDate;
    private Integer remainingSeconds;

    Integer getId() {
        return id;
    }
    String getName() {
        return name;
    }
    String getGoal() {
        return goal;
    }
    DateTime getStartDate() {
        return startDate;
    }
    DateTime getEndDate() {
        return endDate;
    }


    JiraSprint setId(int id) {
        this.id = id;

        return this;
    }
    JiraSprint setName(String name) {
        this.name = name;

        return this;
    }
    JiraSprint setGoal(String goal) {
        this.goal = goal;

        return this;
    }

    JiraSprint setStartDate(DateTime startDate) {
        this.startDate = startDate;

        return this;
    }
    JiraSprint setEndDate(DateTime endDate) {
        this.endDate = endDate;

        return this;
    }

    Integer getRemainingSeconds() {
        return remainingSeconds;
    }

    JiraSprint setRemainingSeconds(Integer remainingSeconds) {
        this.remainingSeconds = remainingSeconds;

        return this;
    }

    @Override
    public String toString() {
        return "name: \"" + name + "\"" +
                ", remainingSeconds: " + remainingSeconds;
    }
}
