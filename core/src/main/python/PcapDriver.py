from scapy.all import *
from kafka import KafkaProducer

import json
import sys
import JsonEncoder

class PcapDriver:
    packets = None

    filter_stime = None
    filter_etime = None
    filter_sip = None
    filter_dip = None
    filter_protocol = None
    filter_fields = None

    def __init__(self, path):
        self.packets = rdpcap(path)

    def check_in_set(self, elem, elem_set):
        # if there is no elem in elem_set => no constraint
        if len(elem_set) == 0:
            return True
        else:
            return elem in elem_set

    def check(self, packet):
        date = int(packet.time)

        sip = ""
        dip = ""
        if "IP" in packet:
            sip = packet["IP"].src
            dip = packet["IP"].dst

        # check date / sip / dip / pro
        if date >= self.filter_stime and date <= self.filter_etime and \
            self.check_in_set(sip, self.filter_sip) and self.check_in_set(dip, self.filter_dip):

            if len(self.filter_protocol) == 0:
                return True
            else:
                # the pro of packet must in filter_protocol
                for pro in self.filter_protocol:
                    if pro in packet:
                        return True
                return False
        else:
            return False

    def set_filter_conf(self, filter_conf):
        if filter_conf["stime"] == "*":
            self.filter_stime = 0
        else:
            self.filter_stime = filter_conf["stime"]

        if filter_conf["etime"] == "*":
            self.filter_etime = sys.maxsize
        else:
            self.filter_etime = filter_conf["etime"]

        self.filter_sip = set()
        if filter_conf["sip"] != "*":
            for sip in filter_conf["sip"]:
                self.filter_sip.add(sip)

        self.filter_dip = set()
        if filter_conf["dip"] != "*":
            for dip in filter_conf["dip"]:
                self.filter_dip.add(dip)

        self.filter_protocol = set()
        if filter_conf["protocol"] != "*":
            for pro in filter_conf["protocol"]:
                self.filter_protocol.add(pro)

        self.filter_fields = []
        if filter_conf["filter_fields"] != "*":
            self.filter_fields = filter_conf["filter_fields"]
            # UNDO check filter_fields
            pass
        else:
            # default filter fields
            self.filter_fields = ["time", "Ethernet.src", "Ethernet.src", "IP.src", "IP.dst", "TCP.flags"]

        """
        print(self.filter_stime)
        print(self.filter_etime)
        print(self.filter_sip)
        print(self.filter_dip)
        print(self.filter_protocol)
        print(self.filter_fields)
        """

    def filter_out(self, filter_conf, kafka_producer, topic):
        # set the filter conf
        self.set_filter_conf(filter_conf)

        if len(filter_conf) < 6:
            print("filter_conf error")
            return

        for p in self.packets:
            if self.check(p):
                # the packet meets the constraints
                out = dict()

                for field in self.filter_fields:
                    if field == "time":
                        out["time"] = int(p.time)
                    elif field == "length":
                        out["length"] = int(p.wirelen)
                    else:
                        f1 = field.split(".")[0]
                        f2 = field.split(".")[1]

                        if f1 in p:
                            out[field] = getattr(p[f1], f2, None)

                print(out)
                # print(json.dumps(out, cls=JsonEncoder.JasonEncoder))
                producer.send(topic, out)

        producer.flush()




if __name__ == "__main__":
    """
    with open('data/filter.json') as json_data_file:
        filter_conf = json.load(json_data_file)

    print(filter_conf)

    producer = KafkaProducer(bootstrap_servers='localhost:9092', value_serializer=lambda v: json.dumps(v, cls=JsonEncoder.JasonEncoder).encode('utf-8'))
    pcap_driver = PcapDriver("data/test.pcap")
    pcap_driver.filter_out(filter_conf, producer, "test3")

    """
    packets = rdpcap("data/test2.pcap")

    for p in packets:
        p.show()







