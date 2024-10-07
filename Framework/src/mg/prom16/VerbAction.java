package mg.prom16;

public class VerbAction {
    private String verb;
    private String action;

    public VerbAction(String verb, String action) {
        this.verb = verb;
        this.action = action;
    }

    public String getVerb() {
        return verb;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "VerbAction{" +
                "verb='" + verb + '\'' +
                ", action='" + action + '\'' +
                '}';
    }
}
