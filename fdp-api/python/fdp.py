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
#################################################################################
#
# Minimalist FAIR Data Point (FDP) Metadata Service with the following endpoints:
#
#  /                  = FDP metadata
#  /catalog           = catalog-level metadata
#  /catalog/{dataset} = dataset-level metadata with 'breedb' as an example
#
# The FDP service returns metadata using Data Catalog Vocabulary (DCAT).
#
#################################################################################

__author__  = 'Arnold Kuzniar'
__version__ = '0.1'
__status__  = 'Prototype'
__license__ = 'Apache Lincense, Version 2.0'

from bottle import Bottle, run, static_file

app = Bottle()

@app.route('/')
def FAIRDataPointMetadata():
   return static_file('fairdatapoint.ttl', root='metadata')

@app.route('/catalog')
def CatalogMetadata():
   return static_file('catalog.ttl', root='metadata')

@app.route('/catalog/<dataset>')
def DatasetMetadata(dataset):
   filename = '{dataset}.ttl'.format(dataset=dataset)
   return static_file(filename, root='metadata')

@app.get('/doc/<filename:re:.*>') # Swagger API documentation '/doc/index.html'
def html(filename):
   return static_file(filename, root='doc/')

run(app, host='localhost', port='8080', debug=True)
