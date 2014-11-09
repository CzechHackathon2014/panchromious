#!/usr/bin/env python

import os

import flask
from flask.ext.sqlalchemy import SQLAlchemy

# Set up app variables
app = flask.Flask('panchromious', static_url_path='')
app.config['SQLALCHEMY_DATABASE_URI'] = os.environ['DATABASE_URL']

# Database handle
db = SQLAlchemy(app)

