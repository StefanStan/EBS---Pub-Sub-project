package example;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;

public class App {
    private static final String SPOUT_ID = "source_text_spout";
    private static final String SPLIT_BOLT_ID = "split_bolt";
    private static final String COUNT_BOLT_ID = "count_bolt";
    private static final String TERMINAL_BOLT_ID = "terminal_bolt";

    public static void main( String[] args ) throws Exception {
        TopologyBuilder builder = new TopologyBuilder();
        SourceTextSpout spout = new SourceTextSpout();
        SplitTextBolt splitbolt = new SplitTextBolt();
        WordCountBolt countbolt = new WordCountBolt();
        TerminalBolt terminalbolt = new TerminalBolt();

        builder.setSpout(SPOUT_ID, spout, 1);
        builder.setBolt(SPLIT_BOLT_ID, splitbolt, 1).setNumTasks(2).shuffleGrouping(SPOUT_ID);
        builder.setBolt(COUNT_BOLT_ID, countbolt, 2).customGrouping(SPLIT_BOLT_ID, new MyGrouping());
        //builder.setBolt(COUNT_BOLT_ID, countbolt, 2).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
        builder.setBolt(TERMINAL_BOLT_ID, terminalbolt).globalGrouping(COUNT_BOLT_ID);

        Config config = new Config();
        config.setNumWorkers(3);

        if (args != null && args.length > 0) {
            StormSubmitter.submitTopology(args[0], config, builder.createTopology());
        } else {
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("count_topology", config, builder.createTopology());

            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            cluster.killTopology("count_topology");
            cluster.shutdown();
        }
    }
}