#!/usr/bin/env python
#coding=utf8

import httplib
import sys
import json
import urllib
import re
import traceback




host = None
port = None
token = None
question = None



class Answer:

	def __init__(self):
		pass

	def run(self):
		httpClient = ''
		result = ''
		data = dict()
		data['header'] = dict()
		data['header']['action'] = "answer"
		data['header']['token'] = token

		data['body'] = dict()
		data['body']['answer'] = "Answer for content"
		data['body']['question_id'] = question
		params = urllib.urlencode({"data" : json.dumps(data)})
	    	headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "application/json"} 
		serviceName = "AnswerProcess"
		uri = '/SkyWorld/api/1.0/QuestionAPI'
		try:
			print "[INFO][",serviceName,"] start to host: ", host, ":" , port , " headers:", headers
			httpClient = httplib.HTTPConnection(host, port, timeout=30)
			httpClient.request('POST', uri, params, headers)
			response = httpClient.getresponse()
			if (response.status == 200):
				result = response.read(1024)
				print "[INFO][",serviceName,"] ===> GET RESULT ", result
				result = re.sub('([{,])([^{:\s"]*):', lambda m: '%s"%s":'%(m.group(1),m.group(2)),result)
				print "[INFO][",serviceName,"] ===> GET RESULT ", result
			else:
				print '[ERROR][',serviceName,']  ====>  http status :', response.status
		except Exception, e:
			print '[ERROR][',serviceName,'] ' , e
			traceback.print_exc()
		finally:
			if httpClient:
				httpClient.close()
		


if __name__ == "__main__":

	if (len(sys.argv) < 4):
		print "[ERROR] ====>  RUN COMMMAND python answer.py host port token question_id"		
		sys.exit(1)
	host = sys.argv[1]
	port = int(sys.argv[2])
	token = sys.argv[3]
	question = sys.argv[4]
	p = Answer()
	p.run()
	
	
