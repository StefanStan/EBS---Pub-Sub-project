package project.generator.config;

/**
 * Created by HomePC on 5/17/2017.
 */
public enum Operator {
    GT(">"),
    LT("<"),
    EQ("="),
    DIFF("!="),
    GTE(">="),
    LTE("<=");

    private String value;

    Operator(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    public static Operator parseString(String name) {
        switch (name) {
            case ">":
                return Operator.GT;
            case "<":
                return Operator.LT;
            case "=":
                return Operator.EQ;
            case "!=":
                return Operator.DIFF;
            case ">=":
                return Operator.GTE;
            case "<=":
                return Operator.LTE;
            default:
                return null;
        }
    }
}
