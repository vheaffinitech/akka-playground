import requests
import datetime


host = "http://localhost:8888"

t = datetime.datetime.now()

for i in range(0,100):
    r = requests.get(host + '/req?name=vincent')
    #r = requests.get(host + '/reqresp?name=vincent')
    t2 = datetime.datetime.now()
    d = t2 - t
    t = t2
    print d
