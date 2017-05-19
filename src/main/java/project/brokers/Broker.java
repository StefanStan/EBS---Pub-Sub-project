package project.brokers;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import project.Subscription;

import java.util.*;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Broker extends BaseRichBolt {
    private OutputCollector collector;
    private Map<String, List<Subscription>> subscriptions = new HashMap<>();

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        if(tuple.getFields().contains("subscription")){
            Object subscription = tuple.getValueByField("subscription");
            if((subscription != null) && (subscription instanceof Subscription)){
                Subscription sub = (Subscription)subscription;
                List<Subscription> subscriptions;
                if(this.subscriptions.containsKey(tuple.getSourceStreamId())){
                    subscriptions = this.subscriptions.get(tuple.getSourceStreamId());
                }
                else{
                    subscriptions = new ArrayList<>();
                }

                subscriptions.add(sub);
                this.subscriptions.put(tuple.getSourceStreamId(), subscriptions);
            }
        }

        if(tuple.getFields().contains("publication")){
            Object publication = tuple.getValueByField("publication");
            if((publication != null) && (publication instanceof HashMap)){
                HashMap<String, Object> pub = (HashMap<String, Object>)publication;
                List<String> subscriberIds = getMatchingSubscriberIds(pub);
                for(String subscriberId : subscriberIds){
                    this.collector.emit(subscriberId, tuple, new Values(pub));
                }
            }
        }

        this.collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("publication"));
    }

    private List<String> getMatchingSubscriberIds(HashMap<String, Object> publication){
        ArrayList<String> result = new ArrayList<>();
        for(String subscriberId : this.subscriptions.keySet()){
            boolean matches = false;
            for (Subscription subscription : this.subscriptions.get(subscriberId)){
                if(subscription.matchesPublication(publication)){
                    matches = true;
                    break;
                }
            }

            if(matches){
                result.add(subscriberId);
            }
        }

        return result;
    }
}
