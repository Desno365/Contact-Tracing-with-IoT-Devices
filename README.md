# Contact Tracing with Body-worn IoT Devices


## Assignment

Note: for more details on the assignment see [Projects 2021.pdf](https://github.com/Desno365/Contact-Tracing-with-IoT-Devices/blob/master/Projects%202021.pdf)

#### Description of the project

People roaming in a given location carry IoT devices. The devices use the radio as a proximity sensor.
Every time two such devices are within the same broadcast domain, that is, at 1-hop distance from each other, the two people wearing the devices are considered to be in contact.
The contacts between people’s devices are periodically reported to the backend on the regular Internet.
Whenever one device signals an event of interest, every other device that was in contact with the former must be informed.

#### Assumptions and Guidelines

* The IoT devices may be assumed to be constantly reachable, possibly across multiple hops, from a single static IoT device that acts as a IPv6 border router. That is, cases of network partitions do not need to be considered.

* The IoT part may be developed and tested entirely using the Cooja simulator. To simulate mobility, you may simply move around nodes manually.


## Technologies

* **IoT devices**: using the [Contiki-NG operating system for Next-Generation IoT devices](https://www.contiki-ng.org/), simulated on the Cooja simulator.

* **Servers**: using the [Akka framework](https://akka.io/).


## Usage

For the IoT devices see [Client/project1/INSTRUCTIONS.md](https://github.com/Desno365/Contact-Tracing-with-IoT-Devices/blob/master/Client/project1/INSTRUCTIONS.md).

For the Server see [Server/INSTRUCTIONS-SINGLE-HOST.md](https://github.com/Desno365/Contact-Tracing-with-IoT-Devices/blob/master/Server/INSTRUCTIONS-SINGLE-HOST.md) or [Server/INSTRUCTIONS-MULTIPLE-HOSTS.md](https://github.com/Desno365/Contact-Tracing-with-IoT-Devices/blob/master/Server/INSTRUCTIONS-MULTIPLE-HOSTS.md).


## Developers

[Accordi Gianmarco](https://github.com/gianfi12)

[Buratti Roberto](https://github.com/Furcanzo)

[Motta Dennis](https://github.com/Desno365)
