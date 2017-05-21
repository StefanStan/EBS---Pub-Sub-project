package project.generator.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by HomePC on 5/17/2017.
 */
public class Publication implements Serializable {
    private Map<String, Object> fields;

    public Publication(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getFields() {
        return this.fields;
    }
}
