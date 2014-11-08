#!/usr/bin/env python

import sys
import os
import flask

# panchromious imports
import utils
import app as panchromious
import model

@panchromious.app.route('/')
def index():
   lst_links = {'color': '/api/color/rgb/red/green/blue', 'vote': '/api/vote'}
   return utils.generate_response(200, lst_links)

@panchromious.app.route('/api/color/rgb/<int:red>/<int:green>/<int:blue>', methods=['GET'])
def get_color(red, green, blue):
   try:
      if not utils.are_valid_rgb_values([red, green, blue]):
         panchromious.app.logger.error('Incorrect color values: [%d, %d, %d]', red, green, blue)
         return utils.error_response('Color values should be in <0;255>')
      else:
         # Response
         panchromious.app.logger.error(model.get_color(red, green, blue))
         return utils.generate_response(200, model.get_color(red, green, blue))
   except:
      panchromious.app.logger.info('Error: %s', sys.exc_info()[2])
      return utils.generate_response(500, {'status': 'error'})

@panchromious.app.route('/api/vote', methods=['GET', 'POST'])
def vote():
   try:
      if flask.request.method == 'POST':
         # Save vote in database

         # Parse request first
         requestStr = flask.request.data
         panchromious.app.logger.info('Received request: %s', requestStr) 
         voteObj = flask.json.loads(requestStr)
         panchromious.app.logger.info('Decoded request: %s', voteObj) 

         colorRed = voteObj['color']['red']
         colorGreen = voteObj['color']['green']
         colorBlue = voteObj['color']['blue']
         name = voteObj['value']

         # Verify that R,G,B components are in valid range
         if not utils.are_valid_rgb_values([colorRed, colorGreen, colorBlue]) or not name:
            panchromious.app.logger.error('Incorrect request: color: [%d, %d, %d], name: %s', colorRed, colorGreen, colorBlue, name)
            return utils.error_response('Incorrect request')
         else: 
            model.save_vote(colorRed, colorGreen, colorBlue, name)
            return utils.generate_response(201, {'status': 'ok'})
      else:
         # Generate new vote
         return utils.generate_response(200, utils.generate_vote())
   except: 
      panchromious.app.logger.info('Error: %s', sys.exc_info()[2])
      return utils.generate_response(500, {'status': 'error'})

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    panchromious.app.run(host='0.0.0.0', port=port, debug=True)
