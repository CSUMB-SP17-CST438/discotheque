import schema as s
from schema import db 
import discoSounds as ds
import serv

def fillDB():
	genres = ['Alternative','Ambient','Classical','Country','Dance','EDM','Dancehall','Deep House','Disco','Drum','Dubstep','Electronic','Folk','Hip Hop','Rap','House','indie','jazz','latin','metal',
	'Piano','Pop','R&B','Soul','Raggae','Reggaeton','Rock','Soundtrack','Techno','Trance','Trap','Triphop','World']

	db.app = serv.app
	db.drop_all()
	db.create_all()
	for g in genres:
		gen = s.genre(g)
		db.session.add(gen)
		db.session.commit()

	member = s.member("test_u1","test_1","one","one@test.com","link",None)
	member1 = s.member("test_u2","test_2","two","two@test.com","link",None)
	member2 = s.member("test_u3","test_3","three","three@test.com","link",None)
	room2 = s.floor("test_floor_1",1,True,"rock")
	room2.set_songlist(ds.getSongList("rock"))
	room3 = s.floor("test_floor_2",1,False,"pop")
	room3.set_songlist(ds.getSongList("Pop"))

	db.session.add(member)
	db.session.add(member1)
	db.session.add(member2)
	db.session.commit()

	room2.add_member(member.member_id)
	s.update_profile(**{'member_id':member.member_id,'bio':"My favoreite music is music that bangs! I like tons of genres from hard rock to indie"})
	db.session.add(room2)
	db.session.add(room3)
	db.session.commit()
	#simple test to make sure member can't join twice
	room2.add_member(member.member_id)
fillDB()