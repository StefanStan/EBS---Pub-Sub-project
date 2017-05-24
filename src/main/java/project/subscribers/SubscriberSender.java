package project.subscribers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import project.Subscription;
import project.generator.Generator;
import project.generator.config.Operator;
import project.generator.domain.MyPair;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriberSender extends BaseRichSpout implements Serializable {
    private SpoutOutputCollector collector;
    private int subscriptionCount;
    private int currentIndex = 0;
    private String subscriberReceiverId;

    private List<project.generator.domain.Subscription> generatedSubs = new ArrayList<>();

    public SubscriberSender(String subscriberReceiverId){
        this.subscriberReceiverId = subscriberReceiverId;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        try {
            Generator gen = new Generator();
            this.generatedSubs = gen.generateSubscription();
            this.subscriptionCount = this.generatedSubs.size();
        } catch (IOException e) {
            for (int i = 0; i < this.subscriptionCount; i++) {
                Map<String, MyPair> subFields = new HashMap<>();
                subFields.put("Name", new MyPair(Operator.parseString("!="), UUID.randomUUID().toString()));
                subFields.put("Age", new MyPair(Operator.parseString(">"), (int)(Math.random() * 100)));
                generatedSubs.add(new project.generator.domain.Subscription(subFields));
            }
        }
    }

    @Override
    public void nextTuple() {
        if(this.currentIndex < this.subscriptionCount){
            this.collector.emit(new Values(new Subscription(generatedSubs.get(currentIndex)), this.subscriberReceiverId));
            this.currentIndex++;
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("subscription", "subscriberReceiverId"));
    }
}
