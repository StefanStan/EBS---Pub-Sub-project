package project.generator.config;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class ConfigObj {
    private String Name;
    private String Type;
    private project.generator.config.Values Values;
    private int FrequencyInSubscriptions;
    private project.generator.config.OperationsFrequency OperationsFrequency;

    public String getName() {
        return this.Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return this.Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public Values getValues() {
        return this.Values;
    }

    public void setValues(Values values) {
        Values = values;
    }

    public int getFrequencyInSubscriptions() {
        return this.FrequencyInSubscriptions;
    }

    public void setFrequencyInSubscriptions(int frequencyInSubscriptions) {
        FrequencyInSubscriptions = frequencyInSubscriptions;
    }

    public OperationsFrequency getOperationsFrequency() {
        return this.OperationsFrequency;
    }

    public void setOperationsFrequency(OperationsFrequency operationsFrequency) {
        OperationsFrequency = operationsFrequency;
    }
}