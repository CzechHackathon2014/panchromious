#!/usr/bin/env python

import sys

import app as panchromious

db = panchromious.db

class Vote(panchromious.db.Model):
   __tablename__ = 'votes'
   
   id = db.Column(db.Integer, primary_key=True)
   red = db.Column(db.Integer)
   green = db.Column(db.Integer)
   blue = db.Column(db.Integer)
   name = db.Column(db.String(20))

   def __init__(self, red, green, blue, name):
      self.red = red
      self.blue = blue
      self.green = green
      self.name = name

   def __repr__(self):
      return 'Vote<id: {}, color: [{},{},{}], name: {}>'.format(self.id, self.red, self.green, self.blue, self.name)

def save_vote(red, green, blue, name):
   vote = Vote(red, green, blue, name)
   panchromious.db.session.add(vote)
   panchromious.app.logger.error('Saving vote to databse: %s', vote)
   panchromious.db.session.commit()

