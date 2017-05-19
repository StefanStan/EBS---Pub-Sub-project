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
    private Map<String, List<Subscription>> subscriptions = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple tuple) {
        Object subscription = tuple.getValueByField("subscription");
        Object publication = tuple.getValueByField("publication");
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

        if((publication != null) && (publication instanceof HashMap)){
            HashMap<String, Object> pub = (HashMap<String, Object>)publication;
            List<String> subscriberIds = getMatchingSubscriberIds(pub);
            for(String subscriberId : subscriberIds){
                this.collector.emit(subscriberId, tuple, new Values(pub));
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
            boolean matches = true;
            for (Subscription subscription : this.subscriptions.get(subscriberId)){
                if(!subscription.matchesPublication(publication)){
                    matches = false;
                }
            }

            if(matches){
                result.add(subscriberId);
            }
        }

        return result;
    }
}
