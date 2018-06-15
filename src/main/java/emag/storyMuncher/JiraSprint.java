package emag.storyMuncher;

class JiraSprint {

    private String name;
    private Integer remainingMinutes;

    String getName() {
        return name;
    }

    JiraSprint setName(String name) {
        this.name = name;

        return this;
    }

    Integer getRemainingMinutes() {
        return remainingMinutes;
    }

    JiraSprint setRemainingMinutes(Integer remainingMinutes) {
        this.remainingMinutes = remainingMinutes;

        return this;
    }

    @Override
    public String toString() {
        return "name: \"" + name + "\"" +
                ", remainingMinutes: " + remainingMinutes;
    }
}
