# EBS---Pub-Sub-project
EBS final project - pub/sub architecture

Terminology:

    host machine - ( mostly Windows - machine that is running virtualisation software - VMWare )    
    zkserver     - ( master server running Zookeeper, Nimbus and StormUI )    
    workerN      - ( worker server running Supervisor and Logviewer )



---------------------------------------------------------------------------------------------------------    

VMWare port forwarding.
	
We need to forward some ports from host to machines inside VMWare (using VMWare network editor)
    
        30000 -> zkserver 22 port (master SSH)
        30001 -> worker1 22 port (worker1 SSH)
        30002 -> worker2 22 port (worker2 SSH)
        30003 -> worker3 22 port (worker3 SSH)
        6701  -> worker1 8000 port (worker1 logging)
        6702  -> worker1 8000 port (worker1 logging)
        6703  -> worker1 8000 port (worker1 logging)
    
---------------------------------------------------------------------------------------------------------    

Host DNS and port forwarding.
   1) We need to edit /etc/hosts on host machine (to setup foreach worker)
   
   	127.0.0.1        worker1.stefanstan.ro
   	127.0.0.2        worker2.stefanstan.ro
   	127.0.0.3        worker3.stefanstan.ro
        
   2) Make port forwarding on host so that StormUI logging viewer works
    
    netsh interface portproxy add v4tov4 listenport=8000 listenaddress=127.0.0.1 connectport=6701 connectaddress=localhost
    netsh interface portproxy add v4tov4 listenport=8000 listenaddress=127.0.0.2 connectport=6702 connectaddress=localhost
    netsh interface portproxy add v4tov4 listenport=8000 listenaddress=127.0.0.3 connectport=6703 connectaddress=localhost
    
6701, 6702 and 6703 ports on local are forwarded inside the workers cluster to each worker port 8000 (see the step before)        
        
---------------------------------------------------------------------------------------------------------         
        
How to start needed processes

   1) ON MASTER NODE
  
zookeeper    ->      sudo service zookeeper status

nimbus       ->      nohup /home/storm/apache-storm-0.9.5/bin/storm nimbus > /dev/null 2>&1 &

stormui      ->      nohup /home/storm/apache-storm-0.9.5/bin/storm ui > /dev/null 2>&1 &

  
  2) ON EACH WORKER NODE
  
supervisor   ->      nohup /home/storm/apache-storm-0.9.5/bin/storm supervisor > /dev/null 2>&1 &
logviewer    ->      nohup /home/storm/apache-storm-0.9.5/bin/storm logviewer > /dev/null 2>&1 &

---------------------------------------------------------------------------------------------------------

To upload a topology (do this while connected to nimbus machine, master node)

/home/storm/apache-storm-0.9.5/bin/storm jar /home/storm/proiect/ebs-labs-example.jar example.App ebs-labs-count-topology

---------------------------------------------------------------------------------------------------------

Guides used to set-up linux machines running storm:

https://tecadmin.net/install-python-2-7-on-ubuntu-and-linuxmint/#

http://knowm.org/how-to-install-a-distributed-apache-storm-cluster/

