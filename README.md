# EBS---Pub-Sub-project
EBS final project - pub/sub architecture

    HOW TO START EACH PROCESS

   ON MASTER NODE
  
zookeeper          sudo service zookeeper status

nimbus             nohup /home/storm/apache-storm-0.9.5/bin/storm nimbus > /dev/null 2>&1 &

stormui            nohup /home/storm/apache-storm-0.9.5/bin/storm ui > /dev/null 2>&1 &

  
  ON EACH WORKER NODE
  
supervisor         nohup /home/storm/apache-storm-0.9.5/bin/storm supervisor > /dev/null 2>&1 &





To upload a topology (do this while connected to nimbus machine, master node)

/home/storm/apache-storm-0.9.5/bin/storm jar /home/storm/proiect/ebs-labs-example.jar example.App ebs-labs-count-topology
