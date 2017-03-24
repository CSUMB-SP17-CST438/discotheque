#schema.py
""" This will define the schema for users, rooms/floors, etc. 
"""
import flask_sqlalchemy, serv
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
db = flask_sqlalchemy.SQLAlchemy(serv.serv)
ma = Marshmallow(serv.serv)

class listeningFloor(db.Model):
	floor_id = db.Column(db.Integer,primary_key = True)
	floorName = db.Column(db.String(120),unique=True)
	floorGenre = db.Column(db.String(50),default = 'None')
	floorMembers = db.relationship('member',backref=db.backref('chatroom',lazy='joined'),lazy='dynamic')
	floorMessages = db.relationship('message',backref=db.backref('chatroom',lazy='joined'),lazy='dynamic')
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
	member_img_url = db.Column(db.String(300))
	member_desc = db.Column(db.Text)
	member_fgenres = db.Column(db.Text)

	def __init__(self,username,fname,lname,email,imgLink,desc, genres):
		self.username = username
		self.member_FName = fname
		self.member_LName = lname
		self.member_email = email
		self.member_img_url = imgLink
		self.member_desc = desc
		self.member_fgenres = genres

	def __repr__(self):
		return '<Member: f_name: %r, l_name: %r, username: %r >' %(self.member_FName, self.member_LName, self.username)


class message(db.Model):
    mess_id = db.Column(db.Integer, primary_key = True)
    floor_id = db.Column(db.Integer,db.ForeignKey('listeningFloor.floor_id'))
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