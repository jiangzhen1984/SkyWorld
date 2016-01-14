#!/usr/bin/env python
#coding=utf8

import httplib
import time
import sys
import json
import urllib
import re
import traceback
import threading
import time
import subprocess







class PushListener:

	def __init__(self, config):
		self.config = config

	def run(self):
	    	headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "application/json"} 
		headers['Authorization'] = self.config['token']
		uri="/SkyWorld/push"
		serviceName = "PushListener"
		while True:
			try:
				print "[INFO][",serviceName,"] start to host: ", self.config['host'], ":" , self.config['port'] , " headers:", headers
				httpClient = httplib.HTTPConnection(self.config['host'], self.config['port'], timeout=30)
				httpClient.request('POST', uri, '', headers)
				response = httpClient.getresponse()
				if (response.status == 200):
					result = response.read(1024)
					result = re.sub('([{,])([^{:\s"]*):', lambda m: '%s"%s":'%(m.group(1),m.group(2)),result)
					print "[INFO][",serviceName,"] ===> GET RESULT ", result
					if self.config['type'] == 'answer_listener':
						print "[INFO][",serviceName,"] ======> GET Answer ", result
						return
					rt = json.loads(result)
					if rt and rt['body']['quest_id']:	
						subprocess.call(["./answer.py", self.config['host'], str(self.config['port']), self.config['token'], str(rt['body']['quest_id'])])
				else:
					print '[ERROR][',serviceName,']  ====>  http status :', response.status
			except Exception, e:
				print '[ERROR][',serviceName,'] ' , e
				traceback.print_exc()
				break;
			finally:
				if httpClient:
					httpClient.close()
		


if __name__ == "__main__":

	if (len(sys.argv) < 4):
		print "[ERROR] ====>  RUN COMMMAND python pushclient.py host port"		
		sys.exit(1)
	p = PushListener({'host' : sys.argv[1], 'port' : int(sys.argv[2]), 'token' : sys.argv[3], 'type' : sys.argv[4]})
	p.run()
	
	
