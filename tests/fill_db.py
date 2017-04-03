import schema as s


def fillDB():
	member = s.member("test_u1", "password1","test_1","one","one@test.com",None,None,None)
	member1 = s.member("test_u2", "password2","test_2","two","two@test.com",None,None,None)
	member2 = s.member("test_u3", "password3","test_3","three","three@test.com",None,None,None)
	room2 = s.floor("test_floor_1",True)
	room3 = s.floor("test_floor_2",False)

	db.session.add(member)
	db.session.add(member1)
	db.session.add(member2)
	db.session.add(room2)
	db.session.add(room3)
	db.session.commit()