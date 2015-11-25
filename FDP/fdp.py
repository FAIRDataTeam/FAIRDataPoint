#
# Minimalistic FAIR Data Point (FDP) Service with the following endpoints:
#  /
#  /catalog
#  /catalog/{dataset} # with 'breedb' example dataset
#
# The service returns metadata using Data Catalog Vocabulary (DCAT).
#

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

@app.get('/doc/<filename:re:.*>') # Swagger documentation '/index.html'
def html(filename):
   return static_file(filename, root='doc/')

run(app, host='localhost', port='8080', debug=True)
