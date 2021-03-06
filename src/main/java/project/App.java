package project;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;
import project.brokers.Broker;
import project.generator.Generator;
import project.generator.domain.generated.PublicationProtos;
import project.generator.domain.generated.SubscriptionProtos;
import project.publishers.Publisher;
import project.subscribers.SubscriberReceiver;
import project.subscribers.SubscriberSender;
import project.utils.PublicationSerialiser;
import project.utils.SubscriptionSerialiser;

/**
 * Created by Vasile Pojoga on 5/14/2017.
 */
public class App {

    private static final String PUBLISHER_SPOUT_ID = "publisher";
    private static final String BROKER_BOLT_ID = "broker";
    private static final String SUBSCRIPTION_FORWARD_ID = "subscription_forward";
    private static final String SUBSCRIBER_RECEIVER_BOLT_ID = "subscriber_receiver_";
    private static final String SUBSCRIBER_SENDER_SPOUT_ID = "subscriber_sender_";
    public static final int SUBSCRIBERS_NUMBER = 3;

    public static void main( String[] args ) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        Publisher publisher = new Publisher();
        Broker broker = new Broker();

        builder.setSpout(PUBLISHER_SPOUT_ID, publisher, 1);
        BoltDeclarer brokerBoltDeclarer = builder.setBolt(BROKER_BOLT_ID, broker, 3);

        for(int i=1;i<=SUBSCRIBERS_NUMBER;i++){
            String subscriberReceiverBoltId = SUBSCRIBER_RECEIVER_BOLT_ID + Integer.toString(i);
            String subscriberSenderSpoutId = SUBSCRIBER_SENDER_SPOUT_ID + Integer.toString(i);
            SubscriberReceiver subscriberReceiver = new SubscriberReceiver(subscriberReceiverBoltId);
            SubscriberSender subscriberSender = new SubscriberSender(subscriberReceiverBoltId);

            builder.setSpout(subscriberSenderSpoutId, subscriberSender);
            brokerBoltDeclarer.shuffleGrouping(subscriberSenderSpoutId);

            broker.addSubscriberReceiverId(subscriberReceiverBoltId);
            builder.setBolt(subscriberReceiverBoltId, subscriberReceiver, 1)
                    .shuffleGrouping(BROKER_BOLT_ID, subscriberReceiverBoltId);
        }

        brokerBoltDeclarer.shuffleGrouping(PUBLISHER_SPOUT_ID);
        brokerBoltDeclarer.allGrouping(BROKER_BOLT_ID, SUBSCRIPTION_FORWARD_ID);

        Config config = new Config();
        config.setNumWorkers(3);
        config.registerSerialization(PublicationProtos.Publication.class, PublicationSerialiser.class);
        config.registerSerialization(SubscriptionProtos.Subscription.class, SubscriptionSerialiser.class);

        boolean runRemote = false;

        if (args != null && args.length == 1) {
            runRemote = Boolean.parseBoolean(args[0]);
            Generator.configPath = null;
        } else if (args != null && args.length == 2) {
            runRemote = Boolean.parseBoolean(args[0]);
            Generator.configPath = args[1];
        }

        if (runRemote) {
            StormSubmitter.submitTopology("ebs-pub-sub-topology", config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("ebs-pub-sub-topology", config, builder.createTopology());

            try {
                Thread.sleep(5 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cluster.killTopology("ebs-pub-sub-topology");
            cluster.shutdown();
        }
    }
}
