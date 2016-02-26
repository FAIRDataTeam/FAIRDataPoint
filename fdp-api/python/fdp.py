# FAIR Data Point Service
#
# Copyright 2015 Netherlands eScience Center in collaboration with
# Dutch Techcenter for Life Sciences.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
# FAIR Data Point (FDP) Service exposes the following endpoints (URL paths):
#   [ /, /doc, /doc/ ]             = Redirect to the API documentation (Swagger UI)
#   /fdp                           = returns FDP metadata
#   /catalog/{catalogID}           = returns catalog metadata (default: catalog-01)
#   /dataset/{datasetID}           = returns dataset metadata (default: breedb)
#   /distribution/{distributionID} = returns distribution metadata (default: breedb-sparql)
#
# This services makes extensive use of metadata defined by:
#   Data Catalog Vocabulary (DCAT, http://www.w3.org/TR/vocab-dcat/)
#   Dublin Core Metadata Terms (DCMI, http://dublincore.org/documents/dcmi-terms/)
#   DBpedia (DBPEDIA, http://dbpedia.org/resource/)
#

__author__  = 'Arnold Kuzniar'
__version__ = '0.4.0'
__status__  = 'Prototype'
__license__ = 'Apache Lincense, Version 2.0'

import os
from bottle import (get, run, static_file, redirect, response, request, opt)
from metadata import FAIRConfigParser, FAIRGraph
from miniuri import Uri

project_dir = os.path.dirname(os.path.abspath(__file__))
doc_dir = os.path.join(project_dir, 'doc/')
host = Uri(opt.bind)    # pass host:[port] through the command-line -b option
host.scheme = 'http'    # add URI scheme

# populate FAIR metadata from config file
config_file = 'metadata.ini'
parser = FAIRConfigParser()
parser.read(config_file)
g = FAIRGraph(host.uri)

for triple in parser.triplify():
   g.setMetadata(triple)

# HTTP response: FAIR metadata in RDF and JSON-LD formats
def httpResponse(graph, uri):
   accept_header = request.headers.get('Accept')
   fmt = 'text/turtle' # set default format (MIME type)

   if 'triples' in accept_header:
      fmt = 'application/n-triples'

   if 'rdf+xml' in accept_header:
      fmt = 'application/rdf+xml'

   if 'ld+json' in accept_header:
      fmt = 'application/ld+json'

   serialized_graph = graph.serialize(uri, fmt)

   if serialized_graph is None:
      response.status = 404 # web resource not found
      return

   response.content_type = fmt
   response.set_header('Allow', 'GET') 

   return serialized_graph

# HTTP request handlers
@get(['/', '/doc', '/doc/'])
def defaultPage():
   redirect('/doc/index.html')

@get('/doc/<fname:path>')
def sourceDocFiles(fname):
   return static_file(fname, root=doc_dir)

@get('/fdp')
def getFdpMetadata(graph=g):
   return httpResponse(graph, graph.fdpURI())

@get('/catalog/<catalog_id>')
def getCatalogMetadata(catalog_id, graph=g):
   return httpResponse(graph, graph.catURI(catalog_id))

@get('/dataset/<dataset_id>')
def getDatasetMetadata(dataset_id, graph=g):
   return httpResponse(graph, graph.datURI(dataset_id))

@get('/distribution/<distribution_id>')
def getDistributionMetadata(distribution_id, graph=g):
   return httpResponse(graph, graph.distURI(distribution_id))

if __name__ == '__main__':
   run(server='wsgiref')

