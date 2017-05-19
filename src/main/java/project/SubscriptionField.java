package project;

import java.util.HashMap;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriptionField {
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

    private boolean applyOperation(Object value1, String operator, Object value2) {
        boolean result = false;
        switch (operator){
            case "=":
                result = value1.equals(value2);
                break;
            case "!=":
                result = !value1.equals(value2);
                break;
            case ">":
                result = (int)value1 > (int)value2;
                break;
            case ">=":
                result = (int)value1 >= (int)value2;
                break;
            case "<":
                result = (int)value1 < (int)value2;
                break;
            case "<=":
                result = (int)value1 <= (int)value2;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }
}
