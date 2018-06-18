package emag.storyMuncher;

class JiraSprint {

    private String name;
    private Integer remainingSeconds;

    String getName() {
        return name;
    }

    JiraSprint setName(String name) {
        this.name = name;

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
