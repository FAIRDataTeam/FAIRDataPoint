DUMP_DIR = 'metadata'
BASE_URL = 'http://127.0.0.1:8080'

# example catalog, dataset and distributions
URL_PATHS = [ 'fdp',
              'catalog/catalog-01',
              'dataset/breedb',
              'distribution/breedb-sparql',
              'distribution/breedb-sqldump' ]

# lookup table: MIME type - file extension pairs
MIME_TYPES = { 'text/turtle'           : 'ttl',
               'application/n-triples' : 'n3',
               'application/rdf+xml'   : 'rdf',
               'application/ld+json'   : 'jsonld' }


