package project.generator.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HomePC on 5/17/2017.
 */
public class Subscription {

    private Map<String, MyPair> fields;

    public Subscription() {
        fields = new HashMap<>();
    }

    public Subscription(Map<String, MyPair> fields) {
        this.fields = fields;
    }

    public Map<String, MyPair> getFields() {
        return this.fields;
    }
}
