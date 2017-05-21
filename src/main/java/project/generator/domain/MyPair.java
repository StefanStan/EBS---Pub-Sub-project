package project.generator.domain;

import project.generator.config.Operator;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class MyPair {

    private Operator operator;
    private Object operand;

    public static MyPair from(String operator, Object operand) {
        Operator op = Operator.parseString(operator);
        if(op != null) {
            return new MyPair(op, operand);
        }
        return null;
    }

    public MyPair(Operator operator, Object operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public Operator getOperator() {
        return this.operator;
    }

    public Object getOperand() {
        return this.operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MyPair myPair = (MyPair) o;

        if (!operator.equals(myPair.operator)) return false;
        return operand.equals(myPair.operand);
    }

    @Override
    public int hashCode() {
        int result = operator.hashCode();
        result = 31 * result + operand.hashCode();
        return result;
    }
}
