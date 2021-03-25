# Instructions for a Multiple Hosts setup

We can run each actor of the system in a different host. We will use directly IntelliJ IDEA.


### Step 0: Create IntelliJ IDEA configurations to run the applications

Here we will create an example setup with 2 servers and 2 reporters, but of course it can be configured in any way.

In IntelliJ IDEA press "Edit Configurations..." and add 4 new "Application" configurations as specified here:

* **Server region1 + seed node**: select as Main class the class "it.polimi.middleware.project1.server.Main" and set as arguments "region1".
* **Server region2 port 6124**: select as Main class the class "it.polimi.middleware.project1.server.Main" and set as arguments "region2 6124".
* **Reporter port 6224**: select as Main class the class "it.polimi.middleware.project1.reporter.Main" and set as arguments "6224".
* **Reporter port 6225**: select as Main class the class "it.polimi.middleware.project1.reporter.Main" and set as arguments "6225".


### Step 1: Modify the akka.conf file in each host

In each host the file /src/main/resources/akka.conf needs to be configured based on the specific setup. On each host put these values:
* `akka.remote.artery.canonical.hostname=<host-ip>`
* `akka.cluster.seed-nodes=["akka://contact-tracing-system@<seed-node-ip>:6123"]`

Substitute <host-ip> with the IPv4 address of the host running the configuration, <seed-node-ip> with the IPv4 address of the host running the seed node.


### Step 2: Run the configurations

Run all the configurations in IntelliJ IDEA in each host, just make sure to run first the configuration that contains the seed node, which in our setup is called "Server region1 + seed node".
