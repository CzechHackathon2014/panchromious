#!/usr/bin/env python

import sys

import app as panchromious
import utils

db = panchromious.db
get_color_sql = """
select mean_euclidean_distances.name as name,
       round(mean_values.red) as mean_red,
       round(mean_values.green) as mean_green,
       round(mean_values.blue) as mean_blue,
       mean_euclidean_distances.my_euclidean_distance as my_distance,
       case when mean_euclidean_distances.mean_euclidean_distance = 0 then 1 else mean_euclidean_distances.my_euclidean_distance / mean_euclidean_distances.mean_euclidean_distance end as doubt
from (select votes.name as name,
             avg(sqrt(power(votes.red - mean_values.red, 2) + power(votes.green - mean_values.green, 2) + power(votes.blue - mean_values.blue, 2))) as mean_euclidean_distance,
             max(sqrt(power({0} - mean_values.red, 2) + power({1} - mean_values.green, 2) + power({2} - mean_values.blue, 2))) as my_euclidean_distance       
      from (select name as name, 
              avg(red) as red, 
              avg(green) as green, 
              avg(blue) as blue 
             from votes group by name) mean_values inner join votes votes on mean_values.name = votes.name
       group by votes.name) mean_euclidean_distances inner join (select name as name, 
                           avg(red) as red, 
                           avg(green) as green, 
                           avg(blue) as blue 
                         from votes group by name) mean_values on mean_euclidean_distances.name = mean_values.name
order by my_distance, doubt
limit 8;
""" 
generate_options_sql = """
select candidates.name as name,
       candidates.mean_red as mean_red,
       candidates.mean_green as mean_green,
       candidates.mean_blue as mean_blue
from (select mean_euclidean_distances.name as name,
       round(mean_values.red) as mean_red,
       round(mean_values.green) as mean_green,
       round(mean_values.blue) as mean_blue,
       mean_euclidean_distances.my_euclidean_distance as distance
from (select votes.name as name,
             avg(sqrt(power(votes.red - mean_values.red, 2) + power(votes.green - mean_values.green, 2) + power(votes.blue - mean_values.blue, 2))) as mean_euclidean_distance,
             max(sqrt(power({0} - mean_values.red, 2) + power({1} - mean_values.green, 2) + power({2} - mean_values.blue, 2))) as my_euclidean_distance       
      from (select name as name, 
              avg(red) as red, 
              avg(green) as green, 
              avg(blue) as blue 
             from votes group by name) mean_values inner join votes votes on mean_values.name = votes.name
       group by votes.name) mean_euclidean_distances inner join (select name as name, 
                           avg(red) as red, 
                           avg(green) as green, 
                           avg(blue) as blue 
                         from votes group by name) mean_values on mean_euclidean_distances.name = mean_values.name) candidates
where candidates.distance between {3} and {4}
offset random() limit  4;
"""

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
   vote = Vote(red, green, blue, utils.normalize_name(name))
   panchromious.db.session.add(vote)
   panchromious.app.logger.error('Saving vote to databse: %s', vote)
   panchromious.db.session.commit()

def get_color(red, green, blue):
   jsonObj = []
   result = db.engine.execute(get_color_sql.format(red, green, blue))
   for row in result:
      jsonObj.append(get_color_json_obj_from_row(row))
   return jsonObj

def get_color_json_obj_from_row(row):
   jsonObj = {}
   jsonObj['name'] = row['name']
   jsonObj['distance'] = float(row['my_distance'])
   jsonObj['doubt'] = float(row['doubt'])   

   jsonObj['color'] = {}
   jsonObj['color']['red'] = int(row['mean_red'])
   jsonObj['color']['green'] = int(row['mean_green'])
   jsonObj['color']['blue'] = int(row['mean_blue'])

   return jsonObj
  
def get_similar_colors(red, green, blue):
   candidates = []
   result = db.engine.execute(generate_options_sql.format(red, green, blue, 0, 200))
   for row in result:
      candidates.append(row['name'])
   return candidates


