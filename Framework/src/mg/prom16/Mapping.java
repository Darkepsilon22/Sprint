package mg.prom16;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class Mapping {
    private String className;
    private Method method;
    private List<String> verbActions; // New field for verb actions

    public Mapping(String className, Method method, List<String> verbActions) {
        this.className = className;
        this.method = method;
        this.verbActions = verbActions; // Initialize the verb actions
    }

    public String getClassName() {
        return className;
    }

    public Method getMethod() {
        return method;
    }

    public List<String> getVerbActions() {
        return verbActions; // Getter for verb actions
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "className='" + className + '\'' +
                ", methodName='" + method.getName() + '\'' +
                ", verbActions=" + verbActions + // Include verb actions in toString
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

        methodString.append(")");
        return methodString.toString();
    }

    // Méthode pour ajouter des verbes (facultatif)
    public void addVerbAction(String verb) {
        if (this.verbActions == null) {
            this.verbActions = new ArrayList<>();
        }
        this.verbActions.add(verb);
    }
}
