package project;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import project.brokers.Broker;
import project.publishers.Publisher;
import project.subscribers.SubscriberReceiver;
import project.subscribers.SubscriberSender;

/**
 * Created by Vasile Pojoga on 5/14/2017.
 */
public class App {

    private static final String PUBLISHER_SPOUT_ID = "publisher";
    private static final String BROKER_BOLT_ID = "broker";
    private static final String SUBSCRIBER_RECEIVER_BOLT_ID = "subscriber_receiver";
    private static final String SUBSCRIBER_SENDER_BOLT_ID = "subscriber_sender";

    public static void main( String[] args ) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        Publisher publisher = new Publisher();
        Broker broker = new Broker();
        SubscriberReceiver subscriberReceiver = new SubscriberReceiver();
        SubscriberSender subscriberSender = new SubscriberSender();

        builder.setSpout(PUBLISHER_SPOUT_ID, publisher, 1);
        builder.setBolt(BROKER_BOLT_ID, broker, 1).shuffleGrouping(SUBSCRIBER_SENDER_BOLT_ID).shuffleGrouping(PUBLISHER_SPOUT_ID);
        builder.setBolt(SUBSCRIBER_RECEIVER_BOLT_ID, subscriberReceiver, 1).shuffleGrouping(BROKER_BOLT_ID);
        builder.setSpout(SUBSCRIBER_SENDER_BOLT_ID, subscriberSender);

        Config config = new Config();
        config.setNumWorkers(3);

        if (args != null && args.length > 0) {
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("PubSub_topology", config, builder.createTopology());

            try {
                Thread.sleep(120000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cluster.killTopology("PubSub_topology");
            cluster.shutdown();
        }
    }
}
