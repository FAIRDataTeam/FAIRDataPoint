#!/bin/bash -x

DBNAME=breedb_germplasm.sqlite
DBSCHEMA=create_breedb_germplasm.sql

sqlite3 $DBNAME < $DBSCHEMA
