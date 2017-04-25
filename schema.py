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
import jsonpickle

# serv.app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL','postgresql://jcrzr:anchor99@localhost/postgres')
# serv.app.config['SQLALCHEMY_TRACK_MODIFICATIONS']= False

db = flask_sqlalchemy.SQLAlchemy()

ma = Marshmallow()
pickl = jsonpickle.pickler.Pickler()
unpickl = jsonpickle.unpickler.Unpickler()

#############################ASSOCIATION TABLES########################################
member_genres = db.Table('favorite genres', 
	db.Column('member_id', db.Integer, db.ForeignKey('member.member_id')),
	db.Column('genre_id', db.Integer, db.ForeignKey('genre.genre_id'))
)

floor_theme = db.Table('floor theme', 
	db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id')),
	db.Column('theme_id', db.Integer, db.ForeignKey('theme.theme_id'))
	)
  
floor_members = db.Table('floor_members',
	db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id')),
	db.Column('member_id', db.Integer, db.ForeignKey('member.member_id'))
	)
	
floor_genre = db.Table('floor_genres',
	db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id')),
	db.Column('genre_id', db.Integer, db.ForeignKey('genre.genre_id'))
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
		self.floor_name=floorName
		self.creator_id = creator_id
		if public is not None:
			self.public=public
		if genre is not None:
			self.floor_genre=genre
			
	def add_member(self,member_id):
		joining_member = getMemberObject(member_id)
		if joining_member not in self.floor_members:
			print("member: ",member_id," joined")
			self.floor_members.append(getMemberObject(member_id))
			db.session.commit()
		else:
			print("member: ", member_id," already joined...")
		
	def rm_member(self,member_id):
		self.floor_members.remove(getMemberObject(member_id))
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
		fl_[0]['floor_messages'] = getFloorMessages(self.floor_id)
		sl = unpickl.restore(self.songlist)
		fl_[0]['songlist'] = sl
		return fl_[0]
	
	def set_songlist(self,songs):
		self.songlist = pickl.flatten(songs)
		db.session.commit()

	def to_list_with_songlist(self):
		fl_sc = floor_Schema_without_songlist()
		floor_obj = fl_sc.dump(self)
		floor_obj[0]['songlist'] = self.songlist
		return floor_obj[0]

	def isActive(self):
		# print("****floor_members_active_****")
		print(self.floor_members)
		if len(self.floor_members) ==0:
			return False;
		else:
			return True

	def get_member_profiles(self):
		profiles = []
		for m in self.floor_members:
			profiles.append(memb_schem.dump(m).data)
		return profiles

	def __repr__(self):
		return '<Floor: {floor_id: %r, floor_name: %r, floor_is_public: %r>' %(self.floor_id,self.floor_name,self.public)

"""********************************************************************************************************************
*********************************************END FLOOR CLASS **********************************************************
***********************************************************************************************************************
***********************************************************************************************************************"""

class member(db.Model):
	member_id = db.Column(db.Integer, primary_key = True)
	username = db.Column(db.String(120))
	member_FName = db.Column(db.String(120))
	member_LName = db.Column(db.String(120))
	member_email = db.Column(db.String(120))
	member_password = db.Column(db.String(140))
	member_img_url = db.Column(db.String(500))
	member_bio = db.Column(db.Text,default="I like music, and that's all for now (update me)")

	member_fgenres = db.relationship('genre',secondary=member_genres,
		backref=db.backref('members',lazy='joined'))

	created_floors = db.relationship('floor',backref=db.backref('member',lazy='joined'),lazy='joined')

	messages = db.relationship('message', backref=db.backref('member',lazy='joined'),lazy='dynamic')

	def __init__(self,username,fname,lname,email,imgLink,bio):
		self.username = username
		# self.member_password = password
		self.member_FName = fname
		self.member_LName = lname
		self.member_email = email
		if imgLink is None:
			self.member_img_url = ""
		if bio is None:
			self.member_bio = ""
		self.member_img_url = imgLink
		self.member_bio = bio
	
	def add_genre(genre_id):
		genre_to_add = getGenreObject(genre_id)
		if genre_to_add not in self.member_fgenres:
			print("genre: ",g," added")
			self.floor_members.append(getMemberObject(member_id))
			db.session.commit()
		else:
			print("genre: ", g, " already a favorite")
		
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

class genre(db.Model):
	genre_id = db.Column(db.Integer,primary_key=True)
	genre_name = db.Column(db.String(120))

	def __init__(self, genre_name):
		self.genre_name = genre_name

	def __repr__(self):
		return '<Genre: {id: %r, name: %r>' %(self.genre_id,self.genre_name)

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


class theme(db.Model):
	theme_id = db.Column(db.Integer, primary_key= True)
	theme_name = db.Column(db.String(120))
	primary_color = db.Column(db.Text)
	secondary_color = db.Column(db.Text)

	def __init__(self,theme_name,primary_color,secondary_color):
		self.theme_name = name
		self.primary_color = primary_color
		self.secondary_color = secondary_color

	def __repr__(self):
		return '<Theme: {name: %r, primary color: %r, secondary color: %r >' %(self.theme_name,self.primary_color,self.secondary_color)

"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************START MARSHMALLOW SCHEMA*****************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""
class genre_Schema(ma.Schema):
	class Meta:
		model = genre
		
class simple_floor_Schema(ma.Schema):
	class Meta:
		fields = ('floor_id','floor_name','floor_genre','creator_id')

class f_Schema(ma.ModelSchema):
	class Meta:
		model = floor

class member_Schema(ma.Schema):
	class Meta:
		fields = ('username','member_FName','member_LName','member_img_url','member_bio','member_fgenres','created_floors'
			)
	member_fgenres = f.Nested(genre_Schema,many=True)
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
	created_floors = f.Nested(simple_floor_Schema,many=True, exclude=('floor_members'))


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
# ************************************START INSERT FUNCTIONS********************************************
# ******************************************************************************************************
def registerMember(fname,lname,email,imgLink):
	if not memberExists_by_email(email):
		#fname,lname,email,imgLink,bio, genres
		generated_usrnm = email.split("@")[0]
		new_member = member(generated_usrnm,fname,lname,email,imgLink,None)
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
	if floor_name_taken(floor_name):
		return (False, "Floor name is already taken")
	else:
		new_floor = floor(floor_name,creator_id,public,genre)
		db.session.add(new_floor)
		db.session.commit()
		print("*********************floor added*********************")
		return (True, new_floor)	

def floor_name_taken(name):
	fl = floor.query.filter_by(floor_name=name).first()
	print("fl",fl)
	if fl is not None:
		return True
	return False

#  username','member_FName','member_LName','member_img_url','member_bio','member_','created_floors'
def update_profile(**kwargs):
	if kwargs is not None and 'member_id' in kwargs:
		me = getMemberObject(kwargs['member_id'])
		for key, value in kwargs.items():
			if key == 'username':
				me.username = value
			if key == 'f_name':
				me.member_FName = value
			if key == 'l_name':
				me.member_LName = value
			if key == 'bio':
				me.member_bio = value
			if key == 'genres':
				for g in value:
					#g should be the id of the genre 
					me.add_genre(g)
		return getMemberObject(kwargs['member_id']).to_list()

	return None
# ******************************************************************************************************
# **************************************END INSERT MESSAGES*********************************************
# ******************************************************************************************************



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
