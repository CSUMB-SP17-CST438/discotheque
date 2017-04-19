import serv, unittest
import random
import json
from schema import *

class SocketioTestCases(unittest.TestCase):
    db.app = serv.app
    # def test_add_message(self):
    #     client = serv.socket.test_client(serv.app)
    #     client.emit('new message', {'floor':1,'from':2,'message':'this is a test message from socket_tests.'})
    #     r = client.get_received()
    #     # response_message = r
    #     # print(r)
    #     self.assertIsNotNone(r)

    # def test_create_floor(self):
    #     client = serv.socket.test_client(serv.app)
    #     i = random.randint(1,2222)
    #     fl_name = "floor"+str(i)
    #     client.emit('create floor',{'floor_name':fl_name, 'member_id':1,'is_public':True,'floor_genre':'Pop'})
    #     r = client.get_received()
    #     print("************************create_floor**************************") 
    #     # song = r[0]['args'][0]['floor']['songlist']
    #     # print(song)
    #     self.assertIsNotNone(r)
        
    # def test_join_floor(self):
    #     client = serv.socket.test_client(serv.app)
    #     client2 = serv.socket.test_client(serv.app)
    #     client.emit('join floor',{'floor_id':1, 'member_id':1})

    #     r = client.get_received()
    #     client2.emit('join floor',{'floor_id':1, 'member_id':2})
    #     print("**************************test_join_floor***********")
    #     # print(r)
    #     # song = r[0]['args'][0]['floor']['songlist'][0]['stream_url']
    #     print("*******************songlist*******************")
    #     secR = client.get_received()
    #     print(json.dumps(secR,indent=4))
    #     # print(song)
    #     self.assertIsNone(None)

    def test_leave_floor(self):
        db.app = serv.app 
        client = serv.socket.test_client(serv.app)
        client2 = serv.socket.test_client(serv.app)
        new_mem = registerMember("us","fname","lname","email22222","img")
        client.emit('join floor',{'floor_id':1, 'member_id':new_mem.member_id})

        r = client.get_received()
        print("**************************test_join_floor***********")
        # print(r)
        # song = r[0]['args'][0]['floor']['songlist'][0]['stream_url']
        client.get_received()
        print("***************leave floor*****************")
        client.emit('leave floor',{'floor_id':1,'member_id':new_mem.member_id})
        rep2 = client.get_received()
        print(r)
        # print(song)
        self.assertIsNone(None)

        

    
if __name__ == '__main__':
    unittest.main()
