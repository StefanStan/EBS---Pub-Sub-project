package example;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class SourceTextSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String[] sourcetext = {
            "text one",
            "text two",
            "text three",
            "text four",
            "too much text after one"
    };
    private HashMap<Integer,Values> tobeconfirmed;
    private int i = 0;
    private int id = 0;

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        // TODO Auto-generated method stub
        this.collector = collector;
        this.tobeconfirmed = new HashMap<Integer,Values>();

    }

    public void nextTuple() {
        // TODO Auto-generated method stub
        if (i < sourcetext.length) {
            this.collector.emit(new Values(sourcetext[i]),id);
            this.tobeconfirmed.put(id, new Values(sourcetext[i]));
            id++;
            i++;
        }

        //if (i >= sourcetext.length) {
        //	i = 0;
        //}

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub
        declarer.declare(new Fields("words"));

    }

    public void ack(Object id) {
        this.tobeconfirmed.remove(id);
        System.out.println("ACKED "+id.toString());
    }

    public void fail(Object id) {
        this.collector.emit(this.tobeconfirmed.get(id), id);
    }

}