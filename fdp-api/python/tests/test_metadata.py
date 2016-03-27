from nose import with_setup
from nose.tools import assert_equals, assert_in, assert_true, assert_false
from metadata import FAIRConfigReader, FAIRGraph, FDPath
from urllib2 import urlparse


reader = FAIRConfigReader()


def test_sections():
   set_a = set(['fdp','catalog/catalog-01','dataset/breedb','distribution/breedb-sqldump','distribution/breedb-sparql'])
   set_b = set(reader.getSectionHeaders())
   assert_true(set_a == set_b)


def test_get_items():
   for section,fields in reader.getMetadata().iteritems():
      for field in fields:
         assert_false(isinstance(reader.getItems(section, field), list))


def test_get_triples():
   for triple in reader.getTriples():
      assert_true(isinstance(triple, tuple))
      assert_equals(len(triple), 3)


base_uri = 'http://127.0.0.1:8080'
g = FAIRGraph(base_uri)


def test_base_uri():
   assert_equals(base_uri, g.baseURI())


def test_doc_uri():
   assert_equals(urlparse.urljoin(base_uri, 'doc'), g.docURI())


def test_fdp_uri():
   assert_equals(urlparse.urljoin(base_uri, 'fdp'), g.fdpURI())


def test_catalog_uri():
   assert_equals(urlparse.urljoin(base_uri, 'catalog/catalog-01'), g.catURI('catalog-01'))


def test_dataset_uri():
   assert_equals(urlparse.urljoin(base_uri, 'dataset/breedb'), g.datURI('breedb'))


def test_distribution_uri():
   assert_equals(urlparse.urljoin(base_uri, 'distribution/breedb-sqldump'), g.distURI('breedb-sqldump'))

