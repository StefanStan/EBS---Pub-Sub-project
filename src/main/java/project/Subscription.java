package project;

import project.generator.domain.MyPair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Subscription implements Serializable {
    public List<SubscriptionField> fields;

    public Subscription(){
        this.fields = new ArrayList<SubscriptionField>();
    }

    public Subscription(project.generator.domain.Subscription subscription) {
        this.fields = new ArrayList<>();
        for (Map.Entry<String, MyPair> entry : subscription.getFields().entrySet()) {
            this.fields.add(new SubscriptionField(entry.getKey(), entry.getValue().getOperator().toString(), entry.getValue().getOperand()));
        }
    }

    public void addField(String field, String operator, Object value){
        this.fields.add(new SubscriptionField(field, operator, value));
    }

    public boolean matchesPublication(HashMap<String, Object> publication){
        for(SubscriptionField subscriptionField : this.fields){
            if(!subscriptionField.matchesPublication(publication)){
                return false;
            }
        }

        return true;
    }
}
