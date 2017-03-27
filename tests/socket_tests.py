import serv, unittest
import random

class SocketioTestCases(unittest.TestCase):
    def test_song_list(self):
        client = serv.socket.test_client(serv.serv)
        client.emit('join room',"please")
        client.emit("get songs", {'genre':'punk'})

        r = client.get_received()
        # print(r)
        from_server = r[0]
        # print (from_server)
        self.assertIsNot(from_server,None)

    def test_register(self):
        client = serv.socket.test_client(serv.serv)
        i = random.randint(1,100000)
        usnm = "username" + str(i)
        password = "pass"+str(i)
        email = "thisiaanemail@"+str(i)
        client.emit("register",{'username':usnm,'password':password,'email':email})
        r = client.get_received()
        response_message = r[0]['name']
        print(response_message)
        self.assertEquals(response_message,"register response")
    
if __name__ == '__main__':
    unittest.main()
