import json
from scapy.all import *

class JasonEncoder(json.JSONEncoder):
    def default(self, o):

        # encode FlagValue
        if isinstance(o, FlagValue):
            return o.__str__()

        return json.JSONEncoder.default(self, o)
