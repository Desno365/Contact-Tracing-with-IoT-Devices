# Instructions


### Step 0: Enable communication with the MQTT Broker

The Linux machine running the Cooja simulator needs to forward the messages to a public MQTT Broker, modify the file `/etc/mosquitto/mosquitto.conf` by adding these lines:

```
connection bridge-01
address mqtt.neslab.it:3200
topic # out 0
topic # in 0
```
Then restart the machine.


### Step 1: Run the Cooja Simulator

On a terminal window run:

* `cd <path-to-project-folder>/Client/tools/cooja`
* `ant run`


### Step 2: Add motes to the simulation

It is possible to create a new simulation or to use the existing one provided in `./Client/project1/project1-simulation.csc`.
To create a new simulation follow these steps:

* Add one rpl-border-router mote to the simulation: Motes -> Add motes -> Create new mote type -> Cooja mote; Then select the file `./Client/project1/rpl-border-router/border-router.c`.

* Add many mqtt-demo motes to the simulation:  Motes -> Add motes -> Create new mote type -> Cooja mote; Then select the file `./Client/project1/mqtt-demo/border-router.c`;.


### Step 3: Connect the rpl-border-router to the external Internet

* Right click on the rpl-border-router mote inside the simulation -> Mote tools -> Serial Socket (SERVER) -> Start.

* On a new terminal window run:
	* `cd <path-to-project-folder>/Client/project1/rpl-border-router`
	* `make TARGET=cooja connect-router-cooja`


### Step 4: Start the simulation

In the "Simulation control" window select Speed limit -> 100% then press Start.
