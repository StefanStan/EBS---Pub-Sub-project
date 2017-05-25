package project.generator.config;

import java.io.IOException;
import java.util.List;

/**
 * @author Stefan Stan on 15.03.2017.
 */
public class GeneratorConfig {

    private long TotalNumberOfPubs;
    private long TotalNumberOfSubs;
    private double DeltaSubsGenError;
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

    public long getTotalNumberOfPubs() {
        return this.TotalNumberOfPubs;
    }

    public void setTotalNumberOfPubs(long totalNumberOfPubs) {
        TotalNumberOfPubs = totalNumberOfPubs;
    }

    public long getTotalNumberOfSubs() {
        return this.TotalNumberOfSubs;
    }

    public void setTotalNumberOfSubs(long totalNumberOfSubs) {
        TotalNumberOfSubs = totalNumberOfSubs;
    }

    public double getDeltaSubsGenError() {
        return this.DeltaSubsGenError;
    }

    public void setDeltaSubsGenError(double deltaSubsGenError) {
        this.DeltaSubsGenError = deltaSubsGenError;
    }

    public List<ConfigObj> getFields() {
        return this.Fields;
    }

    public void setFields(List<ConfigObj> fields) {
        Fields = fields;
    }
}



