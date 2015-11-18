--
-- This SQL file contains the DDL for the BreeDB 'pp_accession' table only.
-- The BreeDB database has been developed by Dr. Richard Finkers, WUR Plant Breeding.
-- 
-- Notes: The original BreeDB.pp_accession table in MySQL was adjusted for Virtuoso OSE.
--
-- 	MySQL		Virtuoso
--      ------------------------ 
--	INT(N)		INTEGER
-- 	TINYTEXT	VARCHAR(255)
-- 	TIMESTAMP	DATE (truncation)
--
-- TODO: Use a column naming convension consistently.
--

CREATE TABLE "BreeDB"."breedb"."pp_accession" (
  "accessionID" varchar(10) NOT NULL,
  "accessionName" varchar(125) DEFAULT NULL,
  "accessionDescription" varchar(150) DEFAULT NULL,
  "fromGenebank" varchar(50) DEFAULT NULL,
  "fromGenebankID" varchar(12) DEFAULT NULL,
  "fromGenebankID2" varchar(12) DEFAULT NULL,
  "spp" varchar(15) DEFAULT NULL,
  "speciesName" varchar(35) DEFAULT NULL,
  "taxonomyID" integer DEFAULT NULL,
  "subTaxaID" integer DEFAULT NULL,
  "gpsLat" varchar(12) DEFAULT NULL,
  "gpsLat_txt" varchar(30) DEFAULT NULL,
  "gpsLong" varchar(12) DEFAULT NULL,
  "gpsLong_txt" varchar(30) DEFAULT NULL,
  "adm1" varchar(40) DEFAULT NULL,
  "adm2" varchar(40) DEFAULT NULL,
  "collectionSiteCountry" varchar(3) DEFAULT 'unk',
  "adm0" varchar(5) DEFAULT NULL,
  "collectionSite" varchar(40) DEFAULT NULL,
  "collectionSiteDetails" varchar(255),
  "collectionSiteProvence" varchar(40) DEFAULT NULL,
  "collectionDate" date DEFAULT NULL,
  "colNu_IPD" varchar(30) DEFAULT NULL,
  "elevation" integer DEFAULT NULL,
  "chromosomeNumber" varchar(8) DEFAULT NULL,
  "germplasmStatus" varchar(15) DEFAULT NULL,
  "MTA" varchar(25) DEFAULT NULL,
  "ploidy" varchar(15) DEFAULT NULL,
  "storageID" integer DEFAULT NULL,
  "remarkID" integer DEFAULT NULL,
  "dateCreated" date NOT NULL DEFAULT '2000-01-01',
  "lastUpdate" date NOT NULL,
  PRIMARY KEY ("accessionID")
);
