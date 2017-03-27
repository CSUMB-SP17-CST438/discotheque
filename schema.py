#schema.py
""" This will define the schema for users, rooms/floors, etc. 
"""
import flask_sqlalchemy
import serv
from datetime import datetime
import os
import json
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from marshmallow import fields
from sqlalchemy import orm
from sqlalchemy import desc



serv.serv.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL')
# serv.serv.config['SQLALCHEMY_DATABASE_URI'] = 'postgresql://jcrzr:anchor99@localhost/postgres'
db = flask_sqlalchemy.SQLAlchemy(serv.serv)
ma = Marshmallow(serv.serv)

floor_members = db.Table('floor_members',
	db.Column('floor_id', db.Integer, db.ForeignKey('floor.floor_id')),
	db.Column('member_id', db.Integer, db.ForeignKey('member.member_id'))
	)

class floor(db.Model):
	floor_id = db.Column(db.Integer,primary_key = True)
	floor_name = db.Column(db.String(120),unique=True)
	floor_genre = db.Column(db.String(50),default = 'None')

	floor_members = db.relationship('member',secondary=floor_members,
		backref=db.backref('floors',lazy='dynamic'))

	floor_messages = db.relationship('message',backref=db.backref('floor',lazy='joined'),lazy='dynamic')
	public = db.Column(db.Boolean,default=True)

	def __init__(self,floorName,public):
		self.floorName=floorName
		if public is not None:
			self.public=public

	def __repr__(self):
		return '<Floor: {floor_id: %r, floor_name: %r, floor_is_public: %r>' %(self.floorID,self.floorName,self.public)

class member(db.Model):
	member_id = db.Column(db.Integer, primary_key = True)
	username = db.Column(db.String(120), unique = True)
	member_FName = db.Column(db.String(50))
	member_LName = db.Column(db.String(50))
	member_email = db.Column(db.String(120))
	member_password = db.Column(db.String(140))
	member_img_url = db.Column(db.String(300))
	member_desc = db.Column(db.Text)
	member_fgenres = db.Column(db.Text)
	messages = db.relationship('message', backref=db.backref('member',lazy='joined'),lazy='dynamic')

	def __init__(self,username,password,fname,lname,email,imgLink,desc, genres):
		self.username = username
		self.member_password = password
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
class member_Schema(ma.Schema):
	class Meta:
		fields = ('username','member_FName','member_LName','member_img_url')

class message_Schema(ma.ModelSchema):
	class Meta:
		model = message
	member = fields.Nested(member_Schema, many=False)

class floor_Schema(ma.ModelSchema):
	class Meta:
		model = floor
	members = fields.Nested(member_Schema,many=True)

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
	return found_member

def memberExists_by_username(username):
	found_member = member.query.filter_by(username=username).first()
	return found_member

def registerMember(username,password,fname,lname,email):
	if not memberExists:
		new_member = member(username, password,fname,lname,email)
		db.session.add(new_member)
		db.session.commit()
		return new_member
	else:
		return None

def add_message(self, floor_id, member_id, text):
	new_message = message(floor_id,mem_id,text)
	db.session.add(new_message)
	db.session.commit()

def login_attempt(username,password):
	this_member = memberExists_by_username(username)
	if this_member is not None and this_member.member_password == password:
		return True
	else:
		return False;

def login_attemp_email(email,password):
	this_member = memberExists_by_email(email)
	if this_member is not None and this_member.member_password == password:
		return True
	else:
		return False;
		



"""*************************************************************************************************************************************
****************************************************************************************************************************************
***********************************************END INSERT/UPDATE FUNCTIONS**************************************************************
****************************************************************************************************************************************
****************************************************************************************************************************************"""