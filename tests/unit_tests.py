#unit_tests.py
import unittest
import discoSounds as ds

class sc_test(unittest.TestCase):

	def test_get_songs(self):
		response = ds.getSongList('punk')
		self.assertIsNotNone(response)
		