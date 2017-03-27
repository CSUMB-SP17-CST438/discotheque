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
		usnm = "username" + str(i)
		password = "pass"+str(i)
		email = "thisiaanemail@"+str(i)
		new_member = db.registerMember(usnm,password,None,None,email)
		self.assertIsNotNone(new_member)


if __name__ == '__main__':
    unittest.main()