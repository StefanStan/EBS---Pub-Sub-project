# EBS---Pub-Sub-project
EBS final project - pub/sub architecture

zookeeper          sudo service zookeeper status

nimbus             nohup /home/storm/apache-storm-0.9.5/bin/storm nimbus > /dev/null 2>&1 &

stormui            nohup /home/storm/apache-storm-0.9.5/bin/storm ui > /dev/null 2>&1 &

supervisor         nohup /home/storm/apache-storm-0.9.5/bin/storm supervisor > /dev/null 2>&1 &


To upload a topology (do this while connected to nimbus machine)

/home/storm/apache-storm-0.9.5/bin/storm jar /home/storm/apache-storm-0.9.5/examples/storm-starter/storm-starter-topologies-0.9.5.jar storm.starter.ExclamationTopology exclamation-topology
