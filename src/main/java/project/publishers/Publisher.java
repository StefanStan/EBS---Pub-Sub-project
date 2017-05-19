package project.publishers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Publisher extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private int publicationCount = 10;
    private int currentIndex= 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        if(this.currentIndex < this.publicationCount){
            this.currentIndex++;
            Map<String, Object> publication = new HashMap<>();
            publication.put("Name", UUID.randomUUID().toString());
            publication.put("Age", (int)(Math.random() * 100));
            this.collector.emit(new Values(publication));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("publication"));
    }
}
