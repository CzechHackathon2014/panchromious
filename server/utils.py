#!/usr/bin/env python

import os
import json
import flask

import app as panchromious
import model

def are_valid_rgb_values(values):
   return not True in map(lambda v : v not in range(256), values)

def error_response(message):
   return generate_response(400, {'error': message})

def generate_response(status, response):
   return flask.make_response((json.dumps(response), status, {'content_type': 'application/json', 'charset': 'utf-8'}))
