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
import os
import popen2
import subprocess






class ServiceConfig:

	def __init__(self, host, port, threadFlag = False):
		self.host = host
		self.port = port
		self.threadFlag = threadFlag
		self.token = ''
		self.cellphone = ''
		self.username = ''
		self.pwd = ''

	def setUserName(self, username):
		self.username = username

	def setCellphone(self, cellphone):
		self.cellphone = cellphone

	def setToken(self, token):
		self.token = token

	def setPwd(self, pwd):
		self.pwd = pwd 

	def getUserName(self):
		return self.username

	def getCellphone(self):
		return self.cellphone

	def getToken(self):
		return self.token

	def getPwd(self):
		return self.pwd

	def setThreadFlag(self, flag):
		self.threadFlag = flag

	def getThreadFlag(self):
		return self.threadFlag

	def getHost(self):
		return self.host

	def getPort(self):
		return int(self.port)


class Service:
	
	def __init__(self):
		pass

	def run(self, config):
		pass


	def post(self, config, uri, headers, params, serviceName):
		try:
	    		print "[INFO][",serviceName,"] start to host: ", config.getHost(), ":" , config.getPort() , " with paramter:" , params
	    		httpClient = httplib.HTTPConnection(config.getHost(), config.getPort(), timeout=30)
	    		httpClient.request('POST', uri, params, headers)
	    		response = httpClient.getresponse()
	    		if (response.status == 200):
				result = response.read()
				print "[INFO][",serviceName,"] ===> GET RESULT ", result
				result = re.sub('([{,])([^{:\s"]*):', lambda m: '%s"%s":'%(m.group(1),m.group(2)),result)
				print "[INFO][",serviceName,"] ===> GET RESULT ", result
				rt = json.loads(result)
				return rt
            		else:
				print '[ERROR][',serviceName,']  ====>  http status :', response.status
				return dict()
		except Exception, e:
	    		print '[ERROR][',serviceName,'] ' , e
			traceback.print_exc()
		finally:
	    		if httpClient:
				httpClient.close()
		


class RegisterService(Service):
	
	def __init__(self):
		Service.__init__(self)


	def run(self, config):
		httpClient = ''
		result = ''
		data = dict()
		data['header'] = dict()
		data['header']['action'] = "register"

		data['body'] = dict()
		data['body']['cellphone'] = config.getCellphone()
		data['body']['username'] = config.getUserName()
		data['body']['pwd'] = config.getPwd()
		data['body']['confirm_pwd'] = config.getPwd()

		params = urllib.urlencode({"data" : json.dumps(data)})
	    	headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "application/json"}  
		rt = self.post(config, '/SkyWorld/api/1.0/UserAPI', headers, params, 'RegisterService')
		if rt['ret'] != 0:
			print '[ERROR] ====>  Register Request result:', rt['ret']
		else:
			print  '[INFO] ====> get token :', rt['token']
			config.setToken(rt['token'])
		return rt['ret'] == 0


class UpgradeService(Service):

	def __init__(self):
		Service.__init__(self)


	def run(self, config):
		httpClient = ''
		result = ''
		data = dict()
		data['header'] = dict()
		data['header']['action'] = "upgrade"
		data['header']['token'] = config.getToken()

		data['body'] = dict()

		params = urllib.urlencode({"data" : json.dumps(data)})
	    	headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "application/json"}  
		rt = self.post(config, '/SkyWorld/api/1.0/UserAPI', headers, params, 'UpgradeService')
		if rt['ret'] != 0:
			print '[ERROR] ====>  Upgrade Request result:', rt['ret']
		else:
			print  '[INFO] ====> get token :', rt['token']
			config.setToken(rt['token'])


class QuestionListenService(Service):

	def __init__(self):
		Service.__init__(self)


	def run(self, config):
		if config.getToken():
			subprocess.Popen(["./pushlistener.py", config.getHost(), str(config.getPort()), config.getToken(), "question_listener"])


class AnswerListenService(Service):

	def __init__(self):
		Service.__init__(self)


	def run(self, config):
		if config.getToken():
			subprocess.Popen(["./pushlistener.py", config.getHost(), str(config.getPort()), config.getToken(), "answer_listener"])



class InquireService(Service):

	def __init__(self):
		Service.__init__(self)


	def run(self, config):
		httpClient = ''
		result = ''
		data = dict()
		data['header'] = dict()
		data['header']['action'] = "question"
		data['header']['token'] = config.getToken()

		data['body'] = dict()
		data['body']['opt'] = 1
		data['body']['question'] = "test question"

		params = urllib.urlencode({"data" : json.dumps(data)})
	    	headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "application/json"}  
		rt = self.post(config, '/SkyWorld/api/1.0/QuestionAPI', headers, params, 'InqurieService')
		print '[ERROR][InquireService] ====>  result:', rt['ret']




class ChainService:

	def __init__(self, chain = [], configs = []):
		self.chain = chain
		self.configs = configs

	def run(self):
		for i in  range(len(self.chain)):
			self.chain[i].run(self.configs[i])




if __name__ == "__main__":

	if (len(sys.argv) < 2):
		print "[ERROR] ====>  RUN COMMMAND python pushclient.py host port"		
		sys.exit(1)
	print len(sys.argv)
	re1config = ServiceConfig(sys.argv[1], sys.argv[2], False)
	re2config = ServiceConfig(sys.argv[1], sys.argv[2], False)

	re1config.setUserName("a")
	re1config.setCellphone("1")
	re1config.setPwd("a")

	re2config.setUserName("baaa82341")
	re2config.setCellphone("213582341")
	re2config.setPwd("a")

	chain = []
	configs = []
	chain.append(RegisterService())
	configs.append(re1config)

	chain.append(RegisterService())
	configs.append(re2config)

	chain.append(UpgradeService())
	configs.append(re2config)


	chain.append(QuestionListenService())
	configs.append(re2config)

	chain.append(AnswerListenService())
	configs.append(re1config)

	chain.append(InquireService())
	configs.append(re1config)
	

	service = ChainService(chain, configs)
	service.run()
	
	
