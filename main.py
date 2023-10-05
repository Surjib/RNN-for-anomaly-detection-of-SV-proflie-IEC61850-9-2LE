from scapy.all import *


a = show_interfaces(resolve_mac=False)
split = a.split('index')
print(split)
print(show_interfaces(resolve_mac=False))
sniff()