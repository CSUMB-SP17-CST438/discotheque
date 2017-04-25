#unit_tests.py
import unittest
import discoSounds as ds
from schema import *
import random
import serv
from schema import *	
import time
from song_update_service import * 
from serv import socket

class sc_test(unittest.TestCase):
	db.app = serv.app

	# def test_get_songs(self):
	# 	response = ds.getSongList('punk')
	# 	self.assertIsNotNone(response)

	# def test_add_user(self):
	# 	i = random.randint(1,100000)
	# 	j = random.randint(1,2222)
	# 	usnm = "username" + str(i)+str(j)
	# 	fname = "first_n"+str(i)
	# 	lname = "last_n"+str(i)
	# 	email = "thisiaanemail@"+str(i)
	# 	img = 'link' + str(i)
	# 	new_member = registerMember(usnm,fname,lname,email,img)
	# 	self.assertIsNotNone(new_member)

	# def test_add_message(self):
	# 	nm = add_message(1,1,"this is a test...nowwww")
	# 	self.assertEqual(nm,True)

	# def test_get_messages(self):
	# 	messages = getFloorMessages(1)
	# 	# print(messages)
	# 	# print(messages[0])
	# 	self.assertIsNotNone(messages)

	# def test_get_user(self):
	# 	us = member.query.get(1)
	# 	usl = us.to_simple_list()
	# 	# print(usl)
	# 	self.assertIsNotNone(us.to_simple_list())

 	
	# def test_get_songlist(self):
	# 	db.app = serv.app
	# 	floor1 = floor.query.get(1)
	# 	# print("foor:")
	# 	# print(floor1)
	# 	sl = ds.getSongList("punk")
	# 	floor1.set_songlist(sl)
	# 	floorRefresh = floor.query.get(1)
	# 	# print(floorRefresh.songlist)
	# 	# solist = floorRefresh.songlist
	# 	# print("songlist")
	# 	# print(solist[0])
	# 	self.assertIsNotNone(floorRefresh.songlist)

	def test_get_floors(self):
		db.app = serv.app
		floors = getPublicFloors()
		# print(floors)
		self.assertIsNotNone(floors)

	# def test_floor_not_active(self):
	# 	print("******floor NOT active*****")
	# 	new_floor = add_floor("empty_floor", 1,True,"punk")
	# 	boole = getFloor(new_floor.floor_id).isActive()
	# 	self.assertEqual(boole,False,"THE FLOOR IS NOT CURRENTLY ACTIVE.")

	# def test_floor_active(self):
	# 	print("***********floor is active******")
	# 	new_floor = add_floor("empty_floor", 1,True,"punk")
	# 	new_floor.add_member(1)
	# 	boole = getFloor(new_floor.floor_id).isActive()
	# 	self.assertEqual(boole,True,"THE FLOOR IS CURRENTLY ACTIVE.")
 
	# def test_songlist_service(self):
	# 	abb, new_floor = add_floor("service test floor",1,True,"punk")
	# 	sl = ds.getSongList("punk")
	# 	# print(sl)
	# 	new_floor.set_songlist(sl)
	# 	new_floor.add_member(1)
	# 	songThread_list = SongThreadHolder(socket)
	# 	songThread_list.add_thread(new_floor.floor_name,new_floor.floor_id,sl)
	# 	time.sleep(500)
	# 	songThread_list.update_thread_status(new_floor.floor_id,False)
	# 	print("*********UPDATE THREAD STOPPED********")
	# 	self.assertIsNotNone(sl)

	# def test_songlist_update(self):
	# 	print("***********start songlist update******************")
	# 	new_floor = getFloor(2)
	# 	fl_list = new_floor.to_list()
	# 	sl = fl_list['songlist']
	# 	songThread_list = SongThreadHolder(socket)
	# 	new_thread = songThread_list.add_thread(new_floor.floor_name,new_floor.floor_id,sl)
	# 	time.sleep(2)
	# 	new_floor.set_songlist(new_thread.songlist)
	# 	new_floor = getFloor(2)
	# 	fl_list = new_floor.to_list()
	# 	sl = fl_list['songlist']
	# 	print(json.dumps(sl, indent=4))
	# 	sleep(400)
	# 	this_thread = songThread_list.find_thread(new_floor.floor_id)
	# 	# this_thread.update_list(317826212)
	# 	print("*********************************************UPDATEDDDDDDD LIST************************************************************")
	# 	print(json.dumps(this_thread.songQ.to_list(),indent=4))
	# 	time.sleep(300)
	# 	songThread_list.update_thread_status(new_floor.floor_id,False)
	# 	print("*********UPDATE THREAD STOPPED********")
	# 	self.assertIsNotNone(sl)

	# def test_queue_from_songlist(self):
	# 	songlist = ds.getSongList("rock")
	# 	print("songlist",json.dumps(songlist,indent=4))

	# 	service = songUpdateThread("test",1,songlist,socket)
	# 	service.list_to_queue()
	# 	print("*****************************queueue****************")
	# 	service.print_queue()
	# 	print("****************updates***************")
	# 	service.queue_updates()
	# 	self.assertIsNone(None)

	# def test_songQ(self):
	# 	songlist = ds.getSongList("rock")
	# 	sq = SongQueue(maxsize=0)
	# 	sq.fill(songlist)
	# 	print("**********initial songlist queue*****")
	# 	print(json.dumps(sq.to_list(),indent=4))
	# 	sq.promote_priority(songlist[len(songlist)-1])
	# 	print("**************updated songlist queue*************")
	# 	print(json.dumps(sq.to_list(),indent=4))
	# 	_,s = sq.get()
	# 	sq.add_to_end(s)
	# 	print("removed first song", json.dumps(sq.to_list(),indent=4))
	# 	print("new index 0", sq.queue[0])
	# 	_,s = sq.get()
	# 	print("removed index 0",s)
	# 	self.assertIsNone(None)



	# def test_leave_floor(self):
	# 	floor_new = add_floor("leave_floor",1,True,"punk")
	# 	floor_new.add_member(1)
	# 	fl_id = floor_new.floor_id
	# 	floor_new =getFloor(fl_id)
	# 	print("current members:")
	# 	print(floor_new.floor_members)
	# 	print("removing member...")
	# 	floor_new.rm_member(1)
	# 	floor_new = getFloor(fl_id)
	# 	print("removed members",floor_new.floor_members)
	# 	self.assertIsInstance(floor_new.floor_members,list)
	# def test_refresh_streams(self):
	# 	songs = ds.getSongList("rock")
	# 	print(json.dumps(songs[0],indent=4))
	# 	refreshed = ds.refresh_streams(songs)
	# 	print(json.dumps(refreshed[0],indent=4))
	# 	self.assertEqual(refreshed[0]['stream_url'],songs[0]['stream_url'])



	# def test_user_by_email(self):
	# 	member = db.memberExists_by_email('thisiaanemail@56735')
	# 	print("memberrrrrrrrrrr")
	# 	print(member)
	# 	mem_to_list = db.getMember('thisiaanemail@56735')
	# 	print(mem_to_list)

	# 	print(db.memberExists_by_email('thisanemaillll@'))
	# 	self.assertIsNotNone(member)

	# def test_add_floor(self):
	# 	flag,new_f = add_floor("name1",1,True,"punk")
	# 	flag2, sec_floor = add_floor("name1",1,True,"punk")
	# 	print("result: ",flag2,sec_floor)
	# 	self.assertEqual(flag2,False)

if __name__ == '__main__':
    unittest.main()