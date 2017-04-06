import serv, unittest
import random

class SocketioTestCases(unittest.TestCase):
    def test_song_list(self):
        client = serv.socket.test_client(serv.app)
        client.emit('join room',"please")
        client.emit("get songs", {'genre':'punk'})

        r = client.get_received()
        # print(r)
        from_server = r[0]
        # print (from_server)
        self.assertIsNot(from_server,None)

    # def test_register(self):
    #     client = serv.socket.test_client(serv.serv)
    #     i = random.randint(1,100000)
    #     usnm = "username" + str(i)
    #     password = "pass"+st r(i)
    #     email = "thisiaanemail@"+str(i)
    #     client.emit("register",{'username':usnm,'password':password,'email':email})
    #     r = client.get_received()
    #     response_message = r[0]['name']
    #     print(response_message)
    #     self.assertEquals(response_message,"register response")

    def test_add_message(self):
        client = serv.socket.test_client(serv.app)
        client.emit('new message', {'floor':1,'from':2,'message':'this is a test message from socket_tests.'})
        r = client.get_received()
        # response_message = r
        print(r)
        self.assertIsNotNone(r)


    # def test_login_fb(self):
    # 	client = serv.socket.test_client(serv.serv)
    # 	client.emit('login',{'fb_t': 'EAAF55XYSy2sBABpHK2UqcyZAz11UBBrTiMzyDSP9rvm7rvnEiG1oG95UUCWfr2oXmMZArjaGHif5nV3mJIIT8JXg6TnSIIhVXe6NGSBZAHBZC1vIRlN5AxJQFPZAMSei1asovC8oeN3FiMSaCP2cVaoxEBXvAV1VTjgoLEz9g2zBYBpLTIze0wJHIGZB2xZBTwXInD0smTjlFJcgg0oWq8DIlFFgZCTjdFQZD'})
    # 	r = client.get_received()
    # 	print(r)
    # 	self.assertIsNotNone(r)
    
if __name__ == '__main__':
    unittest.main()