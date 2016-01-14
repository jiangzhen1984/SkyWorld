#!/usr/bin/env python



import subprocess
import time
import sys


if __name__ =="__main__":
	po = []
	for i in range(60):
		command = "./pushlistener.py {0} {1} {2} question_listener".format(sys.argv[1], sys.argv[2], str(time.time())) 
		print command
		p = subprocess.Popen(command, shell=True)
		po.append(p)

	time.sleep(10)
	for p in po:
		p.terminate()
	
