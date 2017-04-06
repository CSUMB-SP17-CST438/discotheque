#unit_tests.py
import unittest
import discoSounds as ds
import schema as db
import random

class sc_test(unittest.TestCase):

	def test_get_songs(self):
		response = ds.getSongList('punk')
		self.assertIsNotNone(response)

	def test_add_user(self):
		i = random.randint(1,100000)
		j = random.randint(1,2222)
		usnm = "username" + str(i)+str(j)
		fname = "first_n"+str(i)
		lname = "last_n"+str(i)
		email = "thisiaanemail@"+str(i)
		img = 'link' + str(i)
		new_member = db.registerMember(usnm,fname,lname,email,img)
		self.assertIsNotNone(new_member)

	def test_add_message(self):
		nm = db.add_message(1,1,"this is a test...nowwww")
		self.assertEqual(nm,True)

	def test_get_messages(self):
		messages = db.getFloorMessages(1)
		# print(messages)
		# print(messages[0])
		self.assertIsNotNone(messages)

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