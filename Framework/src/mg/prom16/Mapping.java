package mg.prom16;

import java.util.HashSet;
import java.util.Set;
// import com.thoughtworks.paranamer.AdaptiveParanamer;
// import com.thoughtworks.paranamer.Paranamer;

public class Mapping {
    Set<Verb> verbs;

    public Mapping(){
        this.verbs = new HashSet<Verb>();
    private String className;
    private Method method;
    private List<String> verbActions;

    public Mapping(String className, Method method, List<String> verbActions) {
        this.className = className;
        this.method = method;
        this.verbActions = verbActions;
    }

    public void setVerbs(Set<Verb> verbs) {
        this.verbs = verbs;
    }

    public Set<Verb> getVerbs() {
        return verbs;
    }

    public void addVerb(Verb verb) throws Exception {
        for (Verb v : verbs) {
            if (v.getVerbAction().equalsIgnoreCase(verb.getVerbAction())) {
                throw new Exception("Conflit de verbe.");

              public List<String> getVerbActions() {
        return verbActions;
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "className='" + className + '\'' +
                ", methodName='" + method.getName() + '\'' +
                ", verbActions=" + verbActions + 
                '}';
    }

    public String method_to_string() {
        StringBuilder methodString = new StringBuilder();
        methodString.append(method.getName()).append("(");

        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            methodString.append(parameters[i].getType().getSimpleName() + " " + parameters[i].getName());
            if (i < parameters.length - 1) {
                methodString.append(", ");
            }
        }
        verbs.add(verb);
    }

    public Verb getByAction(String verbAction) {
        for (Verb v : verbs) {
            if (v.getVerbAction().equalsIgnoreCase(verbAction)) {
                return v;
            }
    public void addVerbAction(String verb) {
        if (this.verbActions == null) {
            this.verbActions = new ArrayList<>();
        }
        return null;
    }
}