package example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import backtype.storm.generated.GlobalStreamId;
import backtype.storm.grouping.CustomStreamGrouping;
import backtype.storm.task.WorkerTopologyContext;

public class MyGrouping implements CustomStreamGrouping {

    ArrayList<Integer> targetTasks;
    HashMap<String,Integer> taskMappings;
    int taskcounter=0;


    public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
        // TODO Auto-generated method stub
        this.targetTasks = new ArrayList<Integer>(targetTasks);
        this.taskMappings = new HashMap<String,Integer>();
        for (int i = 0; i<targetTasks.size(); i++) {
            System.out.println("###### TASK - "+targetTasks.get(i));
        }
    }

    public List<Integer> chooseTasks(int taskId, List<Object> values) {
        // TODO Auto-generated method stub
		/*for (int j=0;j<values.size();j++) {
			System.out.println("VALUE "+values.get(0)+" "+j);
		}*/
        ArrayList<Integer> chosenTasks = new ArrayList<Integer>();

        String value = values.get(0).toString();

        if (value.equals("text")) {
            return null;
        }

        if (taskMappings.get(value) == null) {
            taskMappings.put(value, targetTasks.get(taskcounter));
            if (taskcounter < targetTasks.size()-1) {
                taskcounter++;
            }
            else {
                taskcounter=0;
            }
        }

        chosenTasks.add(taskMappings.get(value));
        System.out.println("CHOSEN TASK "+taskMappings.get(value)+" for "+value);
        return chosenTasks;
    }

}
