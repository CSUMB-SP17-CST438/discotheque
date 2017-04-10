#schema.py
""" This will define the schema for users, rooms/floors, etc. 
"""
import flask_sqlalchemy
# import serv
from datetime import datetime
import os
import json
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from marshmallow import fields as f
from sqlalchemy import orm
from sqlalchemy import desc

# serv.app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL','postgresql://jcrzr:anchor99@localhost/postgres')
# serv.app.config['SQLALCHEMY_TRACK_MODIFICATIONS']= False

db = flask_sqlalchemy.SQLAlchemy()
ma = Marshmallow()

floor_members = db.Table('floor_members',
	db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id')),
	db.Column('member_id', db.Integer, db.ForeignKey('member.member_id'))
	)
# created_floors = db.Table('created_floors',
# 	db.Column('member_id',db.Integer, db.ForeignKey('member.member_id')),
# 		db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id'))
# 		)



class floor(db.Model):
	floor_id = db.Column(db.Integer,primary_key = True)
	floor_name = db.Column(db.String(120),unique=True)
	floor_genre = db.Column(db.String(50),default = 'None')

	floor_members = db.relationship('member',secondary=floor_members,
		backref=db.backref('floors',lazy='dynamic'))

	creator_id = db.Column(db.Integer,db.ForeignKey('member.member_id'))


	floor_messages = db.relationship('message',backref=db.backref('floor',lazy='joined'),lazy='dynamic')
	public = db.Column(db.Boolean,default=True)

	songlist = db.Column(db.PickleType)

	def __init__(self,floorName,creator_id,public,genre):
		self.floorName=floorName
		self.creator_id = creator_id
		if public is not None:
			self.public=public
		if genre is not None:
			self.floor_genre=genre
			
	def add_member(self,member_id):
		self.floor_members.append(getMemberObject(member_id))
		db.session.commit()
		
	def rm_member(self,member_id):
		self.floor_members.delete(getMemberObject(member_id))
		db.session.commit()
		
	def to_list(self):
		fl_sc = floor_Schema()
		fl_ = fl_sc.dump(self)
		creator = getMemberObject(self.creator_id)
		fl_[0].pop('member',None)
		cr = creator.to_simple_list()
		cr.pop('created_floors',None)
		fl_[0]['creator'] = cr
		fl_[0]['floor_members'] = getFloorMembers(self.floor_id)
		members = []
		# print(self.floor_members)
		# for i in self.floor_members:
		# 	mem = getMemberObject(i)
		# 	members.append(mem.to_simple_list())
		fl_[0]['floor_messages'] = getFloorMessages(self.floor_id)
		return fl_[0]
	
	def set_songlist(self,songs):
		self.songlist = songs
		db.session.commit()

	def to_list_with_songlist(self):
		fl_sc = floor_Schema_without_songlist()
		floor_obj = fl_sc.dump(self)
		print("************floor object*************")
		print(floor_obj)
		print("*************************songlist*******************")

		floor_obj[0]['songlist'] = self.songlist

		return floor_obj[0]
	# def get_songlist(self):
	# return self.songs.loads()


	def __repr__(self):
		return '<Floor: {floor_id: %r, floor_name: %r, floor_is_public: %r>' %(self.floor_id,self.floor_name,self.public)

class member(db.Model):
	member_id = db.Column(db.Integer, primary_key = True)
	username = db.Column(db.String(120))
	member_FName = db.Column(db.String(120))
	member_LName = db.Column(db.String(120))
	member_email = db.Column(db.String(120))
	member_password = db.Column(db.String(140))
	member_img_url = db.Column(db.String(500))
	member_desc = db.Column(db.Text)
	member_fgenres = db.Column(db.Text)

	created_floors = db.relationship('floor',backref=db.backref('member',lazy='joined'),lazy='joined')


	messages = db.relationship('message', backref=db.backref('member',lazy='joined'),lazy='dynamic')

	def __init__(self,username,fname,lname,email,imgLink,desc, genres):
		self.username = username
		# self.member_password = password
		self.member_FName = fname
		self.member_LName = lname
		self.member_email = email
		if imgLink is None:
			self.member_img_url = ""
		if desc is None:
			self.member_desc = ""
		if genres is None:
			self.member_fgenres = ""
		self.member_img_url = imgLink
		self.member_desc = desc
		self.member_fgenres = genres
	
	def to_list(self):
		mem_sc = member_Schema()
		f_mem = mem_sc.dump(self)
		return f_mem[0]

	def to_simple_list(self):
		mem_sc = emailless_member()
		f_mem = mem_sc.dump(self)
		return f_mem[0]


		
		

	def __repr__(self):
		return '<Member: f_name: %r, l_name: %r, username: %r >' %(self.member_FName, self.member_LName, self.username)


class message(db.Model):
    mess_id = db.Column(db.Integer, primary_key = True)
    floor_id = db.Column(db.Integer,db.ForeignKey('floor.floor_id'))
    member_id = db.Column(db.Integer, db.ForeignKey('member.member_id'))
    text = db.Column(db.Text)
    pubTime = db.Column(db.DateTime)
    def __init__(self, floor_id, member_id, text, pubTime=None):
        self.text = text
        self.member_id = member_id
        self.floor_id = floor_id
        if pubTime is None:
            pubTime = datetime.utcnow()
        self.pubTime = pubTime

    def __repr__():
    	if self.member_id is None:
    		thisMember = "No member information"
    	else:
    		thisMember = member.query.get(self.member_id)
    	return '<Message: {Text: %r, Member: %r} >' %(self.text, thisMember)


"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************START MARSHMALLOW SCHEMA*****************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""
class f_Schema(ma.ModelSchema):
	class Meta:
		model = floor

class member_Schema(ma.Schema):
	class Meta:
		fields = ('username','member_FName','member_LName','member_img_url','created_floors'
			)
	created_floors = f.Nested(f_Schema,many=True, exclude=('floor_members'))

class message_Schema(ma.ModelSchema):
	class Meta:
		model = message
	member = f.Nested(member_Schema, many=False)


class floor_Schema(ma.ModelSchema):
	class Meta():
		model = floor
	members = f.Nested(member_Schema,many=True)

class floor_Schema_without_songlist(ma.Schema):
	class Meta:
		fields = ('floor_id','floor_name','floor_genre','floor_messages','creator_id','public', 'songlist')
	songlist = ''

class emailless_member(ma.Schema):
	class Meta:
		fields = ('username','member_FName','member_LName','member_img_url','created_floors'
			)
		created_floors = f.Nested(f_Schema,many=True, exclude=('floor_members'))

class simple_floor_Schema(ma.Schema):
	class Meta:
		fields = ('floor_id','floor_name','floor_genre','creator_id')


"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************END MARSHMALLOW SCHEMA*******************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""

"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************START INSERT/UPDATE FUNCTIONS************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""
def memberExists_by_email(email):
	found_member = member.query.filter_by(member_email=email).first()
	if found_member is None:
		return False
	else:
		return True

def memberExists_by_username(username):
	found_member = member.query.filter_by(username=username).first()
	return found_member

def memberExists(mem_id):
	if db.member.query.get(mem_id):
		return True
	return False

# ******************************************************************************************************
# ************************************START INSERT MESSAGES*********************************************
# ******************************************************************************************************
def registerMember(username,fname,lname,email,imgLink):
	if not memberExists_by_email(email):
		#fname,lname,email,imgLink,desc, genres
		new_member = member(username,fname,lname,email,imgLink,None,None)
		db.session.add(new_member)
		db.session.commit()
		return new_member
	else:
		return None

def add_message(floor_id, member_id, text):
	new_message = message(floor_id,member_id,text)
	db.session.add(new_message)
	db.session.commit()
	return True
	
def add_floor(floor_name,creator_id,public,genre):
	print(floor_name)
	new_floor = floor(floor_name,creator_id,public,genre)
	db.session.add(new_floor)
	db.session.commit()
	print("*********************floor added*********************")
	return new_floor


# ******************************************************************************************************
# **************************************END INSERT MESSAGES*********************************************
# ******************************************************************************************************



# def login_attempt(username,password):
# 	this_member = memberExists_by_username(username)
# 	if this_member is not None and this_member.member_password == password:
# 		return True
# 	else:
# 		return False;

# def login_attemp_email(email,password):
# 	this_member = memberExists_by_email(email)
# 	if this_member is not None and this_member.member_password == password:
# 		return True
# 	else:
# 		return False;
		



"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************END INSERT/UPDATE FUNCTIONS**************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""

"""*************************************************************************************************************************************
****************************************************************************************************************************************
**************************************************START GET FUNCTIONS*******************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""
def getFloorMessages(floor_id):
	all_messages = floor.query.get(floor_id).floor_messages.order_by(message.pubTime)
	me_schem = message_Schema()
	floor_Messages = []
	for i in all_messages:
		floor_Messages.append(me_schem.dump(i).data)
	return floor_Messages


def getFloorMembers(floor_id):
	membs = floor.query.get(floor_id).floor_members
	memb_schem = member_Schema()
	floor_members = []
	count = 0
	for m in membs:
		floor_members.append(memb_schem.dump(m).data)
		floor_members[count].pop('created_floors',None)
		count+=1
	return floor_members

def getMember(email):
	found_member = member.query.filter_by(member_email=email).first()
	mem_sc = member_Schema()
	f_mem = mem_sc.dump(found_member)
	return f_mem[0]
	
def getFloor(floor_id):
	return floor.query.get(floor_id)
	
def getMemberObject(mem_id):
	return member.query.get(mem_id)

def getMemberObject_by_email(email):
	return member.query.filter_by(member_email=email).first()

def getPublicFloors():
	all_floors = floor.query.filter_by(public=True).all()
	simple_schema = simple_floor_Schema()
	fl_list = []
	for f in all_floors:
		fl_list.append(simple_schema.dump(f).data)
	return fl_list


"""*************************************************************************************************************************************
****************************************************************************************************************************************
*****************************************************END GET FUNCTIONS******************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""
