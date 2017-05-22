package project;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriptionField implements Serializable {
    public String field;
    public String operator;
    public Object value;

    public SubscriptionField(String field, String operator, Object value){
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public SubscriptionField(){
        this.field = "";
        this.operator = "";
        this.value = new Object();
    }

    public boolean matchesPublication(HashMap<String, Object> publication){
        if(!publication.containsKey(this.field)){
            return false;
        }

        Object value = publication.get(this.field);
        return applyOperation(value, this.operator, this.value);
    }

    private static boolean applyOperation(Object value1, String operator, Object value2) {
        if(value1 instanceof Integer){
            if(!(value2 instanceof Integer)){
                return false;
            }

            return compareInt((int)value1, operator, (int)value2);
        }
        else if(value1 instanceof String){
            if(!(value2 instanceof String)){
                return false;
            }

            return compareString((String)value1, operator, (String)value2);
        }
        else if(value1 instanceof LocalDateTime){
            if(!(value2 instanceof LocalDateTime)){
                return false;
            }

            return compareDateTime((LocalDateTime)value1, operator, (LocalDateTime)value2);
        }
        else if(value1 instanceof Double) {
            if(!(value2 instanceof Double)) {
                return false;
            }

            return compareDouble((Double) value1, operator, (Double) value2);
        }

        return false;
    }

    private static boolean compareDouble(Double value1, String operator, Double value2) {
        switch (operator) {
            case "=":
                return value1 == value2;
            case "!=":
                return value1 != value2;
            case "<":
                return value1 < value2;
            case "<=":
                return value1 <= value2;
            case ">":
                return value1 > value2;
            case ">=":
                return value1 >= value2;
            default:
                return false;
        }
    }

    private static boolean compareInt(int value1, String operator, int value2){
        switch (operator){
            case "=":
                return value1 == value2;
            case "!=":
                return value1 != value2;
            case "<":
                return value1 < value2;
            case "<=":
                return value1 <= value2;
            case ">":
                return value1 > value2;
            case ">=":
                return value1 >= value2;
            default:
                return false;
        }
    }

    private static boolean compareString(String value1, String operator, String value2){
        switch (operator){
            case "=":
                return value1.equals(value2);
            case "!=":
                return !value1.equals(value2);
            case "<":
                return value1.compareTo(value2) < 0;
            case "<=":
                return value1.compareTo(value2) <= 0;
            case ">":
                return value1.compareTo(value2) > 0;
            case ">=":
                return value1.compareTo(value2) >= 0;
            default:
                return false;
        }
    }

    private static boolean compareDateTime(LocalDateTime value1, String operator, LocalDateTime value2){
        switch (operator){
            case "=":
                return value1.equals(value2);
            case "!=":
                return !value1.equals(value2);
            case "<":
                return value1.compareTo(value2) < 0;
            case "<=":
                return value1.compareTo(value2) <= 0;
            case ">":
                return value1.compareTo(value2) > 0;
            case ">=":
                return value1.compareTo(value2) >= 0;
            default:
                return false;
        }
    }
}
