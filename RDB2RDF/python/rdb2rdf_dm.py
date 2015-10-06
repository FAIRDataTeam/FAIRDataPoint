#!/usr/bin/env python
#
# This is a generic script to convert a relational database (RDB)
# into an RDF serialization using Direct Mapping approach.
#
# Author: Arnold Kuzniar
# Version: 0.1

import os, sys
import argparse as argp

# parse command-line args
parser = argp.ArgumentParser(
   description = 'This script converts a relational database (RDB) into RDF using a direct mapping approach.')

parser.add_argument(
   '-o',
   dest = 'outfile',
   help = 'output file')

parser.add_argument(
   '-v',
   action = 'version',
   version = 'version 0.1')

parser.add_argument(
   '-f',
   action = 'store',
   dest = 'format',
   choices = ['turtle','n3', 'nt', 'trix', 'xml'],
   default = 'turtle',
   help = 'select one of the RDF serializations (default turtle)')

parser.add_argument( # Note: set HOST to '127.0.0.1' instead of 'localhost' to force
   'dburl',          # the db client to use TCP/IP rather than Unix socket
   action = 'store',
   help = """
      DB URL:
         mysql|postgresql|oracle://USER:PASSWD@HOST/DBNAME
         sqlite:///REL_PATH_TO_DBFILE or ////ABS_PATH_TO_DBFILE
   """)

args = parser.parse_args()
dburl = args.dburl
format = args.format
outfile = args.outfile
dbpfx = 'sqlite:///'

if dburl.startswith(dbpfx) is True:
   dbfile = dburl.replace(dbpfx, '')

   if os.path.isfile(dbfile) is False:
      parser.error("sqlite dbfile '%s' not found" % dbfile)

import rdflib as _rdf
import sqlalchemy as _sqla
from rdb2rdf import *
from rdflib.plugin import register, Store

# serialize RDF data
# Bug: rdb2rdf crashes on MySQL INTEGER data types TINY|SMALL|MEDIUM|BIGINT
db = _sqla.create_engine(dburl, echo=False)
register('rdb2rdf_dm', Store, 'rdb2rdf.stores', 'DirectMapping')
graph = _rdf.Graph('rdb2rdf_dm')
graph.open(db)

# write RDF into file or STDOUT
if outfile is not None:
   with open(outfile, 'w') as fout:
      fout.write(graph.serialize(format=format))
else:
   print(graph.serialize(format=format))

graph.close()
