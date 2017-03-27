import serv, unittest

class SocketioTestCases(unittest.TestCase):
    def test_song_list(self):
        client = serv.socket.test_client(serv.serv)
        client.emit('join room',"please")
        client.emit("get songs", {'genre':'punk'})

        r = client.get_received()
        print(r)
        from_server = r[0]
        print (from_server)

        self.assertIsNot(from_server,None)
    
if __name__ == '__main__':
    unittest.main()
