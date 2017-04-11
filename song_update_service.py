#song_update_service.py
import time
import json
import threading
import discoSounds as ds
from serv import * 



class songList(object):
	def __init__(self,songList,start_time):
		self.songlist = songlist
		self.start_time = start_time

	def get_emit_time(self, track_id):
		for t in songList:
			if t['id'] == track_id:
				emit_time = self.start_time + t['duration']
				self.start_time =  emit_time
		return emit_time

	def start_timing(emit_time)
		threading.Timer(emit_time,)

def create_thread(songlist,sleep_time):
	