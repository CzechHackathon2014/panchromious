#!/usr/bin/env python

import os
import json
import random
import flask

import app as panchromious
import model

def are_valid_rgb_values(values):
   return not True in map(lambda v : v not in range(256), values)

def error_response(message):
   return generate_response(400, {'error': message})

def generate_response(status, response):
   return flask.make_response((json.dumps(response), status, {'content_type': 'application/json', 'charset': 'utf-8', 'Access-Control-Allow-Origin': '*'}))

def generate_vote():
   return generate_text_vote()

def generate_text_vote():
   red, green, blue = generate_color()

   vote = {}
   vote['type'] = 'name_entry'
   
   vote['data'] = {}

   vote['data']['color'] = {}
   vote['data']['color']['red'] = red
   vote['data']['color']['green'] = green
   vote['data']['color']['blue'] = blue
   
   return vote

def generate_color():
   red = random.randint(0, 255)
   green = random.randint(0, 255)
   blue = random.randint(0, 255)
   return red, green, blue

def normalize_name(name):
   return name.strip().lower()
   
