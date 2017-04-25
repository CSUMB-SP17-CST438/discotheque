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
from queue import PriorityQueue


class SongQueue(PriorityQueue):
	def __init__(self,maxsize):
		super(SongQueue,self).__init__()
		self.current_priority = 0
		self.least_priority = 0

	def fill(self,songlist):
		for s in songlist:
			self.put((self.least_priority,s))
			self.least_priority+=1

	def to_list(self):
		songlist = []
		position = 0
		while position != self.qsize():
			_,song = self.queue[position]
			songlist.append(song)
			position+=1
		return songlist

	def add_to_end(self,song):
		self.put((self.least_priority,song))
		self.least_priority+=1


	def promote_priority(self,song):
		#frees up spot for the song whose priotity will be changed
		for i in range(self.current_priority+1,self.qsize()):
			pri,song2move = self.queue[i]
			print(pri+1)
			self.queue[i] = (pri+1), song2move
		#inserts the promoted song to the next priority level and increase the current priority index

		self.queue.insert(self.current_priority+1,((self.current_priority+1),song))
		self.current_priority += 1
		for i in range(self.current_priority+2,self.qsize()):
			priority_, found_song = self.queue[i]
			if song == found_song:
				self.queue.remove((priority_,song))

	def get(self):
		self.current_priority-=1
		return super(SongQueue,self).get()

	#allows user to see the item at front of queue without removing
	def peek(self):
		_, song = self.queue[0] 
		return song
	def peek_pos(self,position):
		_,song = self.queue[position]
		return song

	def update_pos(self,pos,song):
		self.queue[pos] = song


class songUpdateThread(threading.Thread):
	def __init__(self,thread_name,floor_id,songlist,socket):
		super(songUpdateThread,self).__init__()
		self.songlist = songlist
		self.start_time = 0
		self.floor_id = floor_id
		self.thread_name = thread_name
		self.current_song_position = 0
		self.last_picked_song = 0
		self.sleep_duration = 0
		self.SLU_TAG = "song list update"
		self.stopper = threading.Event()
		self.socket = socket
		self.songQ = SongQueue(maxsize=0)
		

	def run(self):
		print("Starting thread..")
		# self.send_updates()
		self.songQ.fill(self.songlist)
		self.queue_updates()
		print("Ending thread..")

	def queue_updates(self):
		position = 0
		while not self.stopper.is_set():
			while not self.songQ.empty():
				if position == 0:
					##start time 
					self.start_time = math.floor(time.time() + 1)
					print("init time: ",self.start_time)
					#update initial song info
					_song = self.songQ.peek()
					current_song = ds.refresh_song(_song,self.start_time)
					self.songQ.update_pos(0,(0,current_song))
					self.sleep_duration = math.floor((_song['duration']/1000.00))
					#update information for seconf song in list
					_song = self.songQ.peek_pos(1)
					print("sleep duration:",self.sleep_duration)
					self.start_time = math.floor(self.start_time+self.sleep_duration)
					print("2nd song time:",self.start_time)

					current_song = ds.refresh_song(_song,(self.start_time+2))
					# self.sleep_duration += math.floor((_song['duration']/1000.00))
					self.songQ.update_pos(1,(1,current_song))
					# print("*****queue init emit****")
					self.songlist = self.songQ.to_list()
					# self.socket.emit(self.SLU_TAG,sl,room=self.floor_id)
					print(json.dumps(self.songlist,indent=4))
					# print("removing top two songs")
					_,s = self.songQ.get()
					self.songQ.add_to_end(s)
					# print(s)
					# _,s = self.songQ.get()
					# self.songQ.add_to_end(s)
					# print(s)
					position +=1
				else:
					time.sleep(self.sleep_duration-3)
					#gets song at index 0
					_song = self.songQ.peek()
					#sets sleep duration to be the length of the song at index 0
					self.sleep_duration = math.floor((_song['duration']/1000.0))
					self.songQ.update_pos(0,(0,ds.refresh_song(_song,(self.start_time+2))))
					#update song on the next update
					self.start_time = math.floor(self.start_time + self.sleep_duration+2)
					_song = self.songQ.peek_pos(1)
					self.songQ.update_pos(1,(1,ds.refresh_song(_song,self.start_time)))
					print("************************************************************************************************************")
					print("*************************************************update emit****************************************************")
					print("emit time:",self.start_time)
					print("song",json.dumps(self.songQ.peek(),indent=4))
					print("*****updated list*****")
					self.songlist = self.songQ.to_list()
					print(json.dumps(self.songlist[0:4],indent=4))
					self.socket.emit(self.SLU_TAG,self.songlist,room=self.floor_id)
					_,s = self.songQ.get()
					self.songQ.add_to_end(s)
					
	def print_queue(self):
		print(json.dumps(list(self.songQ.queue),indent=4))

	def stop(self):
		self.stopper.set()





"""**************************************************************************************************************************************"""
"""**************************************************************************************************************************************"""
#wrapper to manage multiple songlist update threads
class SongThreadHolder:
	def __init__(self, socket):
		self.threads = []
		self.socket = socket

	def __str__(self):
		return self.threads
	
	def add_thread(self,thread_name,floor_id,songlist):
		print("inside add thread")
		new_thread = songUpdateThread(thread_name,floor_id,songlist,self.socket)
		new_thread.start()
		self.threads.append(new_thread)
		return new_thread

	def find_thread(self,floor_id):
		print("print inside find thread")
		print("key to search for:",floor_id)
		for t in self.threads:
			ids = t.floor_id
			print("searching is",ids)
			if t.floor_id == floor_id:
				print("thread found")
				return t

	def update_thread_status(self,floor_id,FLAG):
		if FLAG == False:
			for t in self.threads:
				if t.floor_id == floor_id:
					t.stop()
			print("killing thread...")
		else:
			print(floor_id, " thread still active..")

	
