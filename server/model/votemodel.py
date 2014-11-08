import sys
from app import db

class Vote(db.Model):
   __tablename__ = 'votes'

   id = db.Column(db.Integer, primary_key=True)
   red = db.Column(db.Integer)
   green = db.Column(db.Integer)
   blue = db.Column(db.Integer)
   name = db.Column(db.String)

   def __init__(self, red, green, blue, name):
      self.red = red
      self.blue = blue
      self.green = green
      self.name = name

   def __repr__(self):
      return '<id {}>'.format(self.id)

