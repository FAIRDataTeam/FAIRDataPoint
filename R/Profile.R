#Template
#https://www.eu-sol.wur.nl/fair/v1/germplasm/EA00258

library(rrdf)
library(rrdflibs)
library(RMySQL)
library(sqldf)


setwd("/home/anandgavai/Documents/ODEX4all/R_Scripts")

#setwd("\\\\psf\\Home\\Desktop\\Fair_BreeDB")


# Access the local database

mydb = dbConnect(MySQL(), user='root', password='xxxxxxxx', dbname='breedb', host='localhost')
table_list<- dbListTables(mydb)
field_names<- dbListFields(mydb, 'pp_accession')
dat = dbGetQuery(mydb, "select * from pp_accession") # result remains on the MySQL database

#dat<-read.csv("pp_accession.csv",header=TRUE)
# Selection for FAIR 

lat_long<-paste(dat[,"gpsLat"],dat[,"gpsLong"],sep=",")

dat<-dat[,c("accessionID","accessionName","fromGenebankID","taxonomyID","collectionDate")]
dat<-cbind(dat,lat_long)

fairStore<-new.rdf(ontology=FALSE)


### Start adding  prefixes
add.prefix(fairStore,"accID","https://www.eu-sol.wur.nl/passport/SelectAccessionByAccessionID.do?accessionID=")
add.prefix(fairStore,"sName","http://openlifedata.org/taxonomy:")
add.prefix(fairStore,"donorID","http://purl.org/cgngenis/accenumb/")
add.prefix(fairStore,"bStatus","http://purl.org/germplasm/germplasmType#cultivatedHabitat/")
add.prefix(fairStore,"lat_long","http://www.w3.org/2003/01/geo/wgs84_pos#")
add.prefix(fairStore,"accnNum","http://www.cropontology.org/terms/CO_715:0000227/Accession number")


#Addition of predicates

accessionID<-"https://www.eu-sol.wur.nl/passport/SelectAccessionByAccessionID.do?accessionID="
scientificName<-"http://openlifedata.org/taxonomy:"
donoID<-"http://purl.org/cgngenis/accenumb/"
biologicalStatus<-"http://purl.org/germplasm/germplasmType#cultivatedHabitat/"
lat_long<-"http://www.w3.org/2003/01/geo/wgs84_pos#"
accnNum<-"http://www.cropontology.org/terms/CO_715:0000227/Accession number"

createFairEntry<- function(row){
  aID<-row[1]
  genbankID<-row[3]
  tID<-row[4]
  ll<- row[6]
  
  # Subject
  acnID<- paste("https://www.eu-sol.wur.nl/passport/SelectAccessionByAccessionID.do?accessionID=",aID,sep="")
  
  
  #  add.triple(fairStore,acnID,accessionID,aID)
  
  #Predicates
  add.data.triple(fairStore,acnID,lat_long,ll)
  add.triple(fairStore,acnID,scientificName,tID)  
  add.triple(fairStore,acnID,donoID,genbankID)
  add.triple(fairStore,acnID,accnNum,aID)
}

apply(dat, MARGIN=1, FUN=createFairEntry)
save.rdf(fairStore, "FAIR_Profile.ttl","TURTLE")

cat(asString.rdf(fairStore))



