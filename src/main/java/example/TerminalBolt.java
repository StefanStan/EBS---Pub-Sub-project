package example;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class TerminalBolt extends BaseRichBolt {

    private HashMap<String, Integer> count;
    private OutputCollector collector;

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        // TODO Auto-generated method stub
        this.count = new HashMap<String, Integer>();
        this.collector = collector;

    }

    public void execute(Tuple input) {
        // TODO Auto-generated method stub
        String word = input.getStringByField("word");
        Integer count = input.getIntegerByField("count");
        this.count.put(word, count);
        this.collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub

    }

    public void cleanup() {
        System.out.println("Topology Result:");
        for (Map.Entry<String, Integer> entry : this.count.entrySet()) {
            System.out.println(entry.getKey()+" - "+entry.getValue());
        }
    }

}