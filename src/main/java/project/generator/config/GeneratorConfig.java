package project.generator.config;

import java.io.IOException;
import java.util.List;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class GeneratorConfig {

    private long TotalNumberOfMessages;
    private List<ConfigObj> Fields;

    public GeneratorConfig() throws IOException {}

    public ConfigObj getConfigByName(String member) {
        for(ConfigObj temp: Fields) {
            if(temp.getName().equals(member)) {
                return temp;
            }
        }
        return null;
    }

    public long getTotalNumberOfMessages() {
        return this.TotalNumberOfMessages;
    }

    public void setTotalNumberOfMessages(long totalNumberOfMessages) {
        TotalNumberOfMessages = totalNumberOfMessages;
    }

    public List<ConfigObj> getFields() {
        return this.Fields;
    }

    public void setFields(List<ConfigObj> fields) {
        Fields = fields;
    }
}



