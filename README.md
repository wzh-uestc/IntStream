# IntStream

Due to the complexity of the network structure and the high growth of the transmission speed, the measurement and management of the network are facing serious challenges. The traditional bottom-up network telemetry methods are no longer applicable to complex network scenarios. To bridge this gap, we propose IntStream, an intent-driven streaming network telemetry framework to allow network operators to measure and analyze network through passive stream processing and active probing. However, there are three key challenges to building an intent-based telemetry system: (1) The diversity of network data sources. (2) The complexity of the measurement tasks. (3) The low overhead requirements of the telemetry system. IntStream introduces a lightweight component to extract and parse data from various data sources and divides the data stream processing into local and global stages. IntStream provides a set of rich expressive primitives to support operators to write telemetry tasks based on intent. By performing part of the telemetry task on the local stage, the transmission overhead of intermediate data can be effectively reduced. The evaluation results conducted on a large campus network show that IntStream can support a wide range of telemetry tasks while reducing the intermediate data transmission overhead by 99.37% on average.

# Example Applications
We show some telemetry tasks that operators can write with our primitives:

|  #   | Application  | Description |
|  ----  | ----  | ----  |
| 1 | TCP new conn | Hosts for which the number of newly opened TCP connections exceeds threshold. |
| 2 | Sketch generation | Generate periodic sketches of the traffic within a certain network segment. |
| 3 | Port scan | Hosts that send traffic over more than threshold destination ports. |
| 4 | Super spreader | Hosts that contact more than threshold unique destinations. |
| 5 | TCP retransmission | Find out frequently retransmitted TCP connections and get the RTT of them. |
| 6 | SYN flood | Hosts for which the number of half-open TCP connections exceeds threshold. |
| 7 | Slowloris attack | Hosts for which the average transfer rate per flow is below threshold. |
| 8 | Two-phase heavy hitter | Two-phase heavy hitter detection based on sketch and bloom filter. |
| 9 | TCP incomplete flows | Hosts for which the number of incomplete TCP connections exceeds threshold. |
| 10 | Split processsing | Obtain the number of TCP and UDP packets through split processing. |
| 11 | Cryptomining traffic detection | Extract the timestamp sequence of traffic over a period of time. |


# License
This project is licensed under the GPLv3 License.
