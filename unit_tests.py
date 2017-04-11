#unit_tests.py
import unittest
import discoSounds as ds
from schema import *
import random
import serv
from schema import *	
class sc_test(unittest.TestCase):
	db.app = serv.app

	# def test_get_songs(self):
	# 	response = ds.getSongList('punk')
	# 	self.assertIsNotNone(response)

	def test_add_user(self):
		i = random.randint(1,100000)
		j = random.randint(1,2222)
		usnm = "username" + str(i)+str(j)
		fname = "first_n"+str(i)
		lname = "last_n"+str(i)
		email = "thisiaanemail@"+str(i)
		img = 'link' + str(i)
		new_member = registerMember(usnm,fname,lname,email,img)
		self.assertIsNotNone(new_member)

	def test_add_message(self):
		nm = add_message(1,1,"this is a test...nowwww")
		self.assertEqual(nm,True)

	def test_get_messages(self):
		messages = getFloorMessages(1)
		# print(messages)
		# print(messages[0])
		self.assertIsNotNone(messages)

	def test_get_user(self):
		us = member.query.get(1)
		usl = us.to_simple_list()
		# print(usl)
		self.assertIsNotNone(us.to_simple_list())

 	
	def test_get_songlist(self):
		db.app = serv.app
		floor1 = floor.query.get(1)
		# print("foor:")
		# print(floor1)
		sl = ds.getSongList("punk")
		floor1.set_songlist(sl)
		floorRefresh = floor.query.get(1)
		# print(floorRefresh.songlist)
		# solist = floorRefresh.songlist
		# print("songlist")
		# print(solist[0])
		self.assertIsNotNone(floorRefresh.songlist)

	def test_get_floors(self):
		db.app = serv.app
		floors = getPublicFloors()
		# print(floors)
		self.assertIsNotNone(floors)

	def test_floor_not_active(self):
		print("******floor NOT active*****")
		new_floor = add_floor("empty_floor", 1,True,"punk")
		boole = getFloor(new_floor.floor_id).isActive()
		self.assertEqual(boole,False,"THE FLOOR IS NOT CURRENTLY ACTIVE.")

	def test_floor_active(self):
		print("***********floor is active******")
		new_floor = add_floor("empty_floor", 1,True,"punk")
		new_floor.add_member(1)
		boole = getFloor(new_floor.floor_id).isActive()
		self.assertEqual(boole,True,"THE FLOOR IS CURRENTLY ACTIVE.")

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

if __name__ == '__main__':
    unittest.main()