public class JiraSprint {
    private String name;
    private Integer remainingHours;

    public String getName() {
        return name;
    }

    public JiraSprint setName(String name) {
        this.name = name;

        return this;
    }

    public Integer getRemainingHours() {
        return remainingHours;
    }

    public JiraSprint setRemainingHours(Integer remainingHours) {
        this.remainingHours = remainingHours;

        return this;
    }
}
