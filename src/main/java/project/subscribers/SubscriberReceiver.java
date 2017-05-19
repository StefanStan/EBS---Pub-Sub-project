package project.subscribers;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriberReceiver extends BaseRichBolt {
    private OutputCollector collector;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        Object publication = tuple.getValueByField("publication");
        if((publication != null) && (publication instanceof HashMap)){
            HashMap<String, Object> pub = (HashMap<String, Object>)publication;
            System.out.println("[Subscriber Received Publication]");
            for(String field : pub.keySet()){
                System.out.println(field + ":" + pub.get(field).toString());
            }
            System.out.println("---------------------------------");
        }

        this.collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
