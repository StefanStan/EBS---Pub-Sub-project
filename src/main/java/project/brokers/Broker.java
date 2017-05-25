package project.brokers;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import project.Subscription;
import project.generator.domain.Publication;
import project.generator.domain.generated.PublicationProtos;
import project.generator.domain.generated.SubscriptionProtos;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class Broker extends BaseRichBolt implements Serializable {
    private static final String SUBSCRIPTION_FORWARD_ID = "subscription_forward";
    private OutputCollector collector;
    private Map<String, List<Subscription>> subscriptions = new HashMap<>();
    private List<String> subscriberReceivers = new ArrayList<>();
    private int id;

    public static long receivedSubs = 0;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        this.id = topologyContext.getThisTaskId();
    }

    @Override
    public void execute(Tuple tuple) {
        if (tuple.getSourceTask() == this.id) {
            return;
        }

        if (tuple.getFields().contains("subscription")) {
            incrementSubsReceived();
            Object subscription = Subscription.fromModel(project.generator.domain.Subscription.convertToProto((SubscriptionProtos.Subscription) tuple.getValueByField("subscription")));
            Object subscriberReceiverId = tuple.getValueByField("subscriberReceiverId");
            if ((subscription != null) && (subscription instanceof Subscription)) {
                Subscription sub = (Subscription) subscription;
                List<Subscription> subscriptions;
                if (this.subscriptions.containsKey(subscriberReceiverId.toString())) {
                    subscriptions = this.subscriptions.get(subscriberReceiverId.toString());
                } else {
                    subscriptions = new ArrayList<>();
                }

                subscriptions.add(sub);
                this.subscriptions.put(subscriberReceiverId.toString(), subscriptions);
                if (!tuple.getSourceStreamId().equals(SUBSCRIPTION_FORWARD_ID)) {
                    this.collector.emit(SUBSCRIPTION_FORWARD_ID, tuple, new Values(sub.convertToModel().convertToProto(), subscriberReceiverId));
                }
            }
        }

        if (tuple.getFields().contains("publication")) {
            Object publication = Publication.convert((PublicationProtos.Publication) tuple.getValueByField("publication")).getFields();
            if ((publication != null) && (publication instanceof HashMap)) {
                HashMap<String, Object> pub = (HashMap<String, Object>) publication;
                List<String> subscriberIds = getMatchingSubscriberIds(pub);
                for (String subscriberId : subscriberIds) {
                    this.collector.emit(subscriberId, tuple, new Values(new Publication(pub).convert()));
                }
            }
        }

        this.collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(SUBSCRIPTION_FORWARD_ID, new Fields("subscription", "subscriberReceiverId"));
        for (String subscriberReceiverId : this.subscriberReceivers) {
            outputFieldsDeclarer.declareStream(subscriberReceiverId, new Fields("publication"));
        }
    }

    public void addSubscriberReceiverId(String subscriberReceiverId) {
        this.subscriberReceivers.add(subscriberReceiverId);
    }

    private List<String> getMatchingSubscriberIds(HashMap<String, Object> publication) {
        ArrayList<String> result = new ArrayList<>();
        for (String subscriberId : this.subscriptions.keySet()) {
            boolean matches = false;
            for (Subscription subscription : this.subscriptions.get(subscriberId)) {
                if (subscription.matchesPublication(publication)) {
                    matches = true;
                    break;
                }
            }

            if (matches) {
                result.add(subscriberId);
            }
        }

        return result;
    }

    public static synchronized void incrementSubsReceived() {
        receivedSubs++;
    }
}
