package project.subscribers;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vasile Pojoga on 5/19/17.
 */
public class SubscriberReceiver extends BaseRichBolt implements Serializable {
    private static String PUBLICATION_SEND_DATE_TIME_FIELD_ID = "__PublicationSendDateTime";
    private static String PUBLICATION_RECEIVED_DATE_TIME_FIELD_ID = "__PublicationReceivedDateTime";

    private OutputCollector collector;
    private String id;
    private List<Map<String, Object>> receivedPublications;


    public SubscriberReceiver(String id){
        this.id = id;
        this.receivedPublications = new ArrayList<>();
    }


    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        Object publication = tuple.getValueByField("publication");
        if((publication != null) && (publication instanceof HashMap)){
            HashMap<String, Object> pub = (HashMap<String, Object>)publication;
            System.out.print("[" + this.id + "]");
            System.out.println("[Subscriber Received Publication]");
            for(String field : pub.keySet()){
                System.out.println(field + ":" + pub.get(field).toString());
            }
            System.out.println("---------------------------------");
            //Always add PUBLICATION_RECEIVED_DATE_TIME_FIELD_ID with current time as value to publications, for monitoring reasons!
            pub.put(PUBLICATION_RECEIVED_DATE_TIME_FIELD_ID, LocalDateTime.now());
            this.receivedPublications.add(pub);
        }

        Thread thread = new Thread(createRunnable(this.id, new ArrayList<>(this.receivedPublications)));
        thread.start();
        this.collector.ack(tuple);
    }

    private Runnable createRunnable(final String id, final List<Map<String, Object>> publications){
        Runnable runnable = new Runnable(){
            public void run(){
                double latency = getMediumReceiveLatency(new ArrayList<>(publications));
                System.out.println("[" + id + "] {Latency:" + latency + ", ReceivedPublicationsCount:" + publications.size() + "}");
            }
        };

        return runnable;
    }

    private static double getMediumReceiveLatency(List<Map<String, Object>> publications){
        long seconds = 0;
        for(int i=0;i<publications.size();i++){
            seconds += ChronoUnit.SECONDS.between((LocalDateTime)publications.get(i).get(PUBLICATION_SEND_DATE_TIME_FIELD_ID),
                    (LocalDateTime)publications.get(i).get(PUBLICATION_RECEIVED_DATE_TIME_FIELD_ID));
        }

        if(publications.size() > 0){
            return seconds / (double)publications.size();
        }

        return 0;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
