import schema as s
from schema import db 
import serv

def fillDB():
	db.app = serv.app
	db.drop_all()
	db.create_all()
	member = s.member("test_u1","test_1","one","one@test.com","link",None,None)
	member1 = s.member("test_u2","test_2","two","two@test.com","link",None,None)
	member2 = s.member("test_u3","test_3","three","three@test.com","link",None,None)
	room2 = s.floor("test_floor_1",1,True,"rock")
	room3 = s.floor("test_floor_2",1,False,"pop")

	db.session.add(member)
	db.session.add(member1)
	db.session.add(member2)
	db.session.add(room2)
	db.session.add(room3)
	db.session.commit()
fillDB()