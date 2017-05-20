package project.subscribers;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import project.Subscription;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriberSender extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private int subscriptionCount = 1;
    private int currentIndex = 0;
    private String subscriberReceiverId;

    public SubscriberSender(String subscriberReceiverId){
        this.subscriberReceiverId = subscriberReceiverId;
    }

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
    }

    @Override
    public void nextTuple() {
        if(this.currentIndex < this.subscriptionCount){
            this.currentIndex++;
            Subscription subscription = new Subscription();
            //subscription.addField("Name", "=", UUID.randomUUID().toString());
            subscription.addField("Age", ">", (int)(Math.random() * 100));
            this.collector.emit(new Values(subscription, this.subscriberReceiverId));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("subscription", "subscriberReceiverId"));
    }
}
