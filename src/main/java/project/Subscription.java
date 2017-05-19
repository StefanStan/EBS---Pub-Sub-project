package project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Subscription {
    public List<SubscriptionField> fields;

    public Subscription(){
        this.fields = new ArrayList<SubscriptionField>();
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
