package project.publishers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Publisher extends BaseRichSpout implements Serializable {
    private static String PUBLICATION_DATE_TIME_FIELD_ID = "__PublicationSendDateTime";

    private SpoutOutputCollector collector;
    private int publicationCount = 30000;
    private int currentIndex= 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        Utils.sleep(5000);
        while(this.currentIndex < this.publicationCount){
            this.currentIndex++;
            Map<String, Object> publication = new HashMap<>();
            publication.put("Name", UUID.randomUUID().toString());
            publication.put("Age", (int)(Math.random() * 100));
            //Always add PUBLICATION_DATE_TIME_FIELD_ID with current time as value to publications, for monitoring reasons!
            publication.put(PUBLICATION_DATE_TIME_FIELD_ID, LocalDateTime.now());
            this.collector.emit(new Values(publication));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("publication"));
    }
}
