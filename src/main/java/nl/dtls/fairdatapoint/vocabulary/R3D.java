/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.dtls.fairdatapoint.vocabulary;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public final class R3D {
    public static final String NAMESPACE = "http://www.re3data.org/schema/3-0#";
    public static final String PREFIX = "r3d";

    /** <tt>http://www.re3data.org/schema/3-0#Repository</tt> */
    public static final IRI REPOSITORY;

    /** <tt>http://www.re3data.org/schema/3-0#Institution</tt> */
    public static final IRI INSTITUTION;

    /** <tt>http://www.re3data.org/schema/3-0#Regulation</tt> */
    public static final IRI REGULATION;

    /** <tt>http://www.re3data.org/schema/3-0#Access</tt> */
    public static final IRI ACCESS;

    /** <tt>http://www.re3data.org/schema/3-0#License</tt> */
    public static final IRI LICENSE;

    /** <tt>http://www.re3data.org/schema/3-0#Policy</tt> */
    public static final IRI POLICY;

    /** <tt>http://www.re3data.org/schema/3-0#Api</tt> */
    public static final IRI API;

    /** <tt>http://www.re3data.org/schema/3-0#ReferenceDocument</tt> */
    public static final IRI REFERENCEDOCUMENT;

    /** <tt>http://www.re3data.org/schema/3-0#Responsibility</tt> */
    public static final IRI RESPONSIBILITY;

    /** <tt>http://www.re3data.org/schema/3-0#PublicationSupport</tt> */
    public static final IRI PUBLICATIONSUPPORT;

    /** <tt>http://www.re3data.org/schema/3-0#publicationSupport</tt> */
    public static final IRI HAS_PUBLICATIONSUPPORT;

    /** <tt>http://www.re3data.org/schema/3-0#dataAccess</tt> */
    public static final IRI DATAACCESS;

    /** <tt>http://www.re3data.org/schema/3-0#dataUpload</tt> */
    public static final IRI DATAUPLOAD;

    /** <tt>http://www.re3data.org/schema/3-0#databaseAccess</tt> */
    public static final IRI DATABASEACCESS;

    /** <tt>http://www.re3data.org/schema/3-0#dataLicense</tt> */
    public static final IRI DATALICENSE;

    /** <tt>http://www.re3data.org/schema/3-0#dataUploadLicense</tt> */
    public static final IRI DATAUPLOADLICENSE;

    /** <tt>http://www.re3data.org/schema/3-0#databaseLicense</tt> */
    public static final IRI DATABASELICENSE;

    /** <tt>http://www.re3data.org/schema/3-0#policy</tt> */
    public static final IRI HAS_POLICY;

    /** <tt>http://www.re3data.org/schema/3-0#api</tt> */
    public static final IRI HAS_API;

    /** <tt>http://www.re3data.org/schema/3-0#metadataStandard</tt> */
    public static final IRI METADATASTANDARD;

    /** <tt>http://www.re3data.org/schema/3-0#certificate</tt> */
    public static final IRI CERTIFICATE;

    /** <tt>http://www.re3data.org/schema/3-0#syndication</tt> */
    public static final IRI SYNDICATION;

    /** <tt>http://www.re3data.org/schema/3-0#repositoryType</tt> */
    public static final IRI REPOSITORYTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#providerType</tt> */
    public static final IRI PROVIDERTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#repositoryLanguage</tt> */
    public static final IRI REPOSITORYLANGUAGE;

    /** <tt>http://www.re3data.org/schema/3-0#contentType</tt> */
    public static final IRI CONTENTTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#re3data</tt> */
    public static final IRI RE3DATA;

    /** <tt>http://www.re3data.org/schema/3-0#subject</tt> */
    public static final IRI SUBJECT;

    /** <tt>http://www.re3data.org/schema/3-0#doi</tt> */
    public static final IRI DOI;

    /** <tt>http://www.re3data.org/schema/3-0#repositoryIdentifier</tt> */
    public static final IRI REPOSITORYIDENTIFIER;

    /** <tt>http://www.re3data.org/schema/3-0#repositoryPost</tt> */
    public static final IRI REPOSITORYPOST;

    /** <tt>http://www.re3data.org/schema/3-0#institution</tt> */
    public static final IRI HAS_INSTITUTION;

    /** <tt>http://www.re3data.org/schema/3-0#dataCatalog</tt> */
    public static final IRI DATACATALOG;

    /** <tt>http://www.re3data.org/schema/3-0#catalogRepository</tt> */
    public static final IRI CATALOGREPOSITORY;

    /** <tt>http://www.re3data.org/schema/3-0#inRepository</tt> */
    public static final IRI INREPOSITORY;

    /** <tt>http://www.re3data.org/schema/3-0#reposits</tt> */
    public static final IRI REPOSITS;

    /** <tt>http://www.re3data.org/schema/3-0#institutionIdentifier</tt> */
    public static final IRI INSTITUTIONIDENTIFIER;

    /** <tt>http://www.re3data.org/schema/3-0#responsibility</tt> */
    public static final IRI HAS_RESPONSIBILITY;

    /** <tt>http://www.re3data.org/schema/3-0#institutionType</tt> */
    public static final IRI INSTITUTIONTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#responsibilityType</tt> */
    public static final IRI RESPONSIBILITYTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#country</tt> */
    public static final IRI COUNTRY;

    /** <tt>http://www.re3data.org/schema/3-0#apiType</tt> */
    public static final IRI APITYPE;

    /** <tt>http://www.re3data.org/schema/3-0#policyType</tt> */
    public static final IRI POLICYTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#accessRestriction</tt> */
    public static final IRI ACCESSRESTRICTION;

    /** <tt>http://www.re3data.org/schema/3-0#accessType</tt> */
    public static final IRI ACCESSTYPE;

    /** <tt>http://www.re3data.org/schema/3-0#citationReference</tt> */
    public static final IRI CITATIONREFERENCE;

    /** <tt>http://www.re3data.org/schema/3-0#pidSystem</tt> */
    public static final IRI PIDSYSTEM;

    /** <tt>http://www.re3data.org/schema/3-0#aidSystem</tt> */
    public static final IRI AIDSYSTEM;

    /** <tt>http://www.re3data.org/schema/3-0#startDate</tt> */
    public static final IRI STARTDATE;

    /** <tt>http://www.re3data.org/schema/3-0#entryDate</tt> */
    public static final IRI ENTRYDATE;

    /** <tt>http://www.re3data.org/schema/3-0#closed</tt> */
    public static final IRI CLOSED;

    /** <tt>http://www.re3data.org/schema/3-0#offline</tt> */
    public static final IRI OFFLINE;

    /** <tt>http://www.re3data.org/schema/3-0#metrics</tt> */
    public static final IRI METRICS;

    /** <tt>http://www.re3data.org/schema/3-0#size</tt> */
    public static final IRI SIZE;

    /** <tt>http://www.re3data.org/schema/3-0#software</tt> */
    public static final IRI SOFTWARE;

    /** <tt>http://www.re3data.org/schema/3-0#hasVersioning</tt> */
    public static final IRI HASVERSIONING;

    /** <tt>http://www.re3data.org/schema/3-0#hasQualityManagement</tt> */
    public static final IRI HASQUALITYMANAGEMENT;

    /** <tt>http://www.re3data.org/schema/3-0#responsibilityStartDate</tt> */
    public static final IRI RESPONSIBILITYSTARTDATE;

    /** <tt>http://www.re3data.org/schema/3-0#responsibilityEndDate</tt> */
    public static final IRI RESPONSIBILITYENDDATE;

    /** <tt>http://www.re3data.org/schema/3-0#apiUrl</tt> */
    public static final IRI APIURL;

    /** <tt>http://www.re3data.org/schema/3-0#wsdlDocument</tt> */
    public static final IRI WSDLDOCUMENT;

    /** <tt>http://www.re3data.org/schema/3-0#citeGuidelineUrl</tt> */
    public static final IRI CITEGUIDELINEURL;

    /** <tt>http://www.re3data.org/schema/3-0#enhancedPubliction</tt> */
    public static final IRI ENHANCEDPUBLICTION;


    static {
        ValueFactory VF = SimpleValueFactory.getInstance();

        REPOSITORY = VF.createIRI("http://www.re3data.org/schema/3-0#Repository");
        INSTITUTION = VF.createIRI("http://www.re3data.org/schema/3-0#Institution");
        REGULATION = VF.createIRI("http://www.re3data.org/schema/3-0#Regulation");
        ACCESS = VF.createIRI("http://www.re3data.org/schema/3-0#Access");
        LICENSE = VF.createIRI("http://www.re3data.org/schema/3-0#License");
        POLICY = VF.createIRI("http://www.re3data.org/schema/3-0#Policy");
        API = VF.createIRI("http://www.re3data.org/schema/3-0#Api");
        REFERENCEDOCUMENT = VF.createIRI("http://www.re3data.org/schema/3-0#ReferenceDocument");
        RESPONSIBILITY = VF.createIRI("http://www.re3data.org/schema/3-0#Responsibility");
        PUBLICATIONSUPPORT = VF.createIRI("http://www.re3data.org/schema/3-0#PublicationSupport");
        HAS_PUBLICATIONSUPPORT = VF.createIRI("http://www.re3data.org/schema/3-0#publicationSupport");
        DATAACCESS = VF.createIRI("http://www.re3data.org/schema/3-0#dataAccess");
        DATAUPLOAD = VF.createIRI("http://www.re3data.org/schema/3-0#dataUpload");
        DATABASEACCESS = VF.createIRI("http://www.re3data.org/schema/3-0#databaseAccess");
        DATALICENSE = VF.createIRI("http://www.re3data.org/schema/3-0#dataLicense");
        DATAUPLOADLICENSE = VF.createIRI("http://www.re3data.org/schema/3-0#dataUploadLicense");
        DATABASELICENSE = VF.createIRI("http://www.re3data.org/schema/3-0#databaseLicense");
        HAS_POLICY = VF.createIRI("http://www.re3data.org/schema/3-0#policy");
        HAS_API = VF.createIRI("http://www.re3data.org/schema/3-0#api");
        METADATASTANDARD = VF.createIRI("http://www.re3data.org/schema/3-0#metadataStandard");
        CERTIFICATE = VF.createIRI("http://www.re3data.org/schema/3-0#certificate");
        SYNDICATION = VF.createIRI("http://www.re3data.org/schema/3-0#syndication");
        REPOSITORYTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#repositoryType");
        PROVIDERTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#providerType");
        REPOSITORYLANGUAGE = VF.createIRI("http://www.re3data.org/schema/3-0#repositoryLanguage");
        CONTENTTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#contentType");
        RE3DATA = VF.createIRI("http://www.re3data.org/schema/3-0#re3data");
        SUBJECT = VF.createIRI("http://www.re3data.org/schema/3-0#subject");
        DOI = VF.createIRI("http://www.re3data.org/schema/3-0#doi");
        REPOSITORYIDENTIFIER = VF.createIRI("http://www.re3data.org/schema/3-0#repositoryIdentifier");
        REPOSITORYPOST = VF.createIRI("http://www.re3data.org/schema/3-0#repositoryPost");
        HAS_INSTITUTION = VF.createIRI("http://www.re3data.org/schema/3-0#institution");
        DATACATALOG = VF.createIRI("http://www.re3data.org/schema/3-0#dataCatalog");
        CATALOGREPOSITORY = VF.createIRI("http://www.re3data.org/schema/3-0#catalogRepository");
        INREPOSITORY = VF.createIRI("http://www.re3data.org/schema/3-0#inRepository");
        REPOSITS = VF.createIRI("http://www.re3data.org/schema/3-0#reposits");
        INSTITUTIONIDENTIFIER = VF.createIRI("http://www.re3data.org/schema/3-0#institutionIdentifier");
        HAS_RESPONSIBILITY = VF.createIRI("http://www.re3data.org/schema/3-0#responsibility");
        INSTITUTIONTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#institutionType");
        RESPONSIBILITYTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#responsibilityType");
        COUNTRY = VF.createIRI("http://www.re3data.org/schema/3-0#country");
        APITYPE = VF.createIRI("http://www.re3data.org/schema/3-0#apiType");
        POLICYTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#policyType");
        ACCESSRESTRICTION = VF.createIRI("http://www.re3data.org/schema/3-0#accessRestriction");
        ACCESSTYPE = VF.createIRI("http://www.re3data.org/schema/3-0#accessType");
        CITATIONREFERENCE = VF.createIRI("http://www.re3data.org/schema/3-0#citationReference");
        PIDSYSTEM = VF.createIRI("http://www.re3data.org/schema/3-0#pidSystem");
        AIDSYSTEM = VF.createIRI("http://www.re3data.org/schema/3-0#aidSystem");
        STARTDATE = VF.createIRI("http://www.re3data.org/schema/3-0#startDate");
        ENTRYDATE = VF.createIRI("http://www.re3data.org/schema/3-0#entryDate");
        CLOSED = VF.createIRI("http://www.re3data.org/schema/3-0#closed");
        OFFLINE = VF.createIRI("http://www.re3data.org/schema/3-0#offline");
        METRICS = VF.createIRI("http://www.re3data.org/schema/3-0#metrics");
        SIZE = VF.createIRI("http://www.re3data.org/schema/3-0#size");
        SOFTWARE = VF.createIRI("http://www.re3data.org/schema/3-0#software");
        HASVERSIONING = VF.createIRI("http://www.re3data.org/schema/3-0#hasVersioning");
        HASQUALITYMANAGEMENT = VF.createIRI("http://www.re3data.org/schema/3-0#hasQualityManagement");
        RESPONSIBILITYSTARTDATE = VF.createIRI("http://www.re3data.org/schema/3-0#responsibilityStartDate");
        RESPONSIBILITYENDDATE = VF.createIRI("http://www.re3data.org/schema/3-0#responsibilityEndDate");
        APIURL = VF.createIRI("http://www.re3data.org/schema/3-0#apiUrl");
        WSDLDOCUMENT = VF.createIRI("http://www.re3data.org/schema/3-0#wsdlDocument");
        CITEGUIDELINEURL = VF.createIRI("http://www.re3data.org/schema/3-0#citeGuidelineUrl");
        ENHANCEDPUBLICTION = VF.createIRI("http://www.re3data.org/schema/3-0#enhancedPubliction");

    }

    /** Utility class; private constructor to prevent instance being created. */
    private R3D() {
    }
}