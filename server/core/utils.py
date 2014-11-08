import os
import flask

def are_valid_rgb_values(values):
   return not True in map(lambda v : v not in range(256), values)

def error_response(message):
   return generate_response(400, {'error': message})

def generate_response(status, response):
   return flask.make_response((flask.jsonify(response), status, {'content_type': 'application/json', 'charset': 'utf-8'}))
