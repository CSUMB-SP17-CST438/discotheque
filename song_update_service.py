#song_update_service.py
"""
	the basic idea is to create a service that constantly updates the songlist with the next song_url
	that way if url's expire . There service will be comprised of multiple threads that handle the updating per 
	active floor. When a floor is empty of members, a flag should be passed to the the floor's thread that it is no longer, 
	allowing the service to stop. 
	Each thread should emit the next song object in the list with a refreshed stream_url.
	1. Create isActive() flag
	2. Thread

"""


# from serv import socket
from time import time, sleep
import json
import threading
import discoSounds as ds
import time
import math 

class songUpdateThread(threading.Thread):
	def __init__(self,thread_name,floor_id,songlist,socket):
		super(songUpdateThread,self).__init__()
		self.songlist = songlist
		self.start_time = time.time()
		self.floor_id = floor_id
		self.thread_name = thread_name
		self.current_song_position = 0
		self.last_picked_song = 0
		self.sleep_duration = 0
		self.SLU_TAG = "songlist update"
		self.stopper = threading.Event()
		self.socket = socket

	def run(self):
		print("Starting thread..")
		self.send_updates()
		print("Ending thread..")

	def send_updates(self):
		position = 0
		while(not self.stopper.is_set()):
			for t in self.songlist:
				if self.current_song_position == 0:
					self.start_time = math.floor(time.time() + 2.0)
					self.songlist[0] = ds.refresh_song(self.songlist[0],self.start_time)
					self.songlist[0]['start_time'] = self.start_time
					del self.songlist[1]
					st = math.floor(self.start_time+(self.songlist[position]['duration']/1000))
					self.songlist[1] = ds.refresh_song(self.songlist[1],st)
					del self.songlist[2]
					self.socket.emit(self.SLU_TAG, self.songlist,room=self.floor_id)
					print("***********init emit*******")
					print(json.dumps(self.songlist,indent=4))
					self.current_song_position +=1
					self.sleep_duration = (self.songlist[0]['duration']/1000)-1.0
					self.start_time = math.floor(self.start_time + self.sleep_duration)
					# sleep(duration)
				else:
					time.sleep(self.sleep_duration)
					if not self.stopper.is_set():
						self.sleep_duration = (self.songlist[position]['duration']/1000)-1.0
						self.songlist[self.current_song_position] = ds.refresh_song(self.songlist[self.current_song_position],self.start_time)
						self.start_time = math.floor(self.start_time + self.sleep_duration)
						print("***************update emit***********")
						print("emit time:",self.start_time) 
						print("song",json.dumps(self.songlist[self.current_song_position],indent=4))
						self.socket.emit(self.SLU_TAG, self.songlist,room=self.floor_id)
						self.current_song_position+=1

	def update_list(self, song_id):
		print("*************UPDATE LIST***********")
		loc = 0
		for t in self.songlist:
			if t['id'] == song_id:
				self.last_picked_song+=1
				print("****SONG TO BE INSERTED*****")
				print(t['id'])
				self.songlist.insert(self.current_song_position, t)
				print("length",str(len(self.songlist)))
				print("****SONG TO BE DELETED****")
				print(json.dumps(self.songlist[loc+self.last_picked_song]['id'],indent=4))
				del self.songlist[loc+1]
				break
			loc+=1
			print("loc",str(loc))
		

	def stop(self):
		self.stopper.set()


#wrapper to manage multiple songlist update threads
class SongThreadHolder:
	def __init__(self, socket):
		self.threads = []
		self.socket = socket

	def __str__(self):
		return self.threads
	
	def add_thread(self,thread_name,floor_id,songlist):
		new_thread = songUpdateThread(thread_name,floor_id,songlist,self.socket)
		new_thread.start()
		self.threads.append(new_thread)

	def find_thread(self,floor_id):
		for t in self.threads:
			if t.floor_id == floor_id:
				return t

	def update_thread_status(self,floor_id,FLAG):
		if FLAG == False:
			for t in self.threads:
				if t.floor_id == floor_id:
					t.stop()
			print("killing thread...")
		else:
			print(floor_id, " thread still active..")

		


	


# class songlist(object):
# 	def __init__(self,songlist,start_time):
# 		self.songlist = songlist
# 		self.start_time = start_time

# 	def get_emit_time(self, track_id):
# 		for t in songlist:
# 			if t['id'] == track_id:
# 				emit_time = self.start_time + t['duration']
# 				self.start_time =  emit_time
# 		return emit_time

# def create_thread(songlist,sleep_time):
# 	