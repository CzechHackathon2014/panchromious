import os

import flask
from flask.ext.sqlalchemy import SQLAlchemy

from core import utils

# Set up app variables
app = flask.Flask('Panchromious')
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']

# Database handle
db = SQLAlchemy(app)

@app.route('/')
def index():
   lst_links = {'color': '/api/color/rgb/red/green/blue', 'vote': '/api/vote'}
   return utils.generate_response(200, lst_links)

@app.route('/api/color/rgb/<int:red>/<int:green>/<int:blue>', methods=['GET'])
def get_color(red, green, blue):
   if not utils.are_valid_rgb_values([red, green, blue]):
      app.logger.error('Incorrect color values: [%d, %d, %d]', red, green, blue)
      return utils.error_response('Color values should be in <0;255>')
   return utils.generate_response(200, {'response': 'Color World'})

@app.route('/api/vote', methods=['GET', 'POST'])
def vote():
   return utils.generate_response(200, {'response': 'API World'})

if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port, debug=True)
