package project.publishers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import project.generator.Generator;
import project.generator.domain.Publication;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Publisher extends BaseRichSpout implements Serializable {
    private static String PUBLICATION_DATE_TIME_FIELD_ID = "__PublicationSendDateTime";

    private SpoutOutputCollector collector;
    private int publicationCount = 30000;
    private int currentIndex= 0;

    private List<Publication> generatedPubs = new ArrayList<>();

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        try {
            Generator gen = new Generator();
            this.generatedPubs = gen.generatePublications();
            this.publicationCount = this.generatedPubs.size();

        } catch (IOException e) {
            //  in case config is not present or it can't read from it will generate some random
            for (int i = 0; i < publicationCount; i++) {
                Map<String, Object> pubFields = new HashMap<>();
                pubFields.put("Name", UUID.randomUUID().toString());
                pubFields.put("Age", (int)(Math.random() * 100));
                //Always add PUBLICATION_DATE_TIME_FIELD_ID with current time as value to publications, for monitoring reasons!
                pubFields.put(PUBLICATION_DATE_TIME_FIELD_ID, LocalDateTime.now());
                generatedPubs.add(new Publication(pubFields));
            }
        }
    }

    @Override
    public void nextTuple() {
        Utils.sleep(5000);
        while(this.currentIndex < this.publicationCount){
            this.generatedPubs.get(currentIndex).getFields().put(PUBLICATION_DATE_TIME_FIELD_ID, LocalDateTime.now());
            this.collector.emit(new Values(this.generatedPubs.get(currentIndex).getFields()));
            this.currentIndex++;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("publication"));
    }
}
