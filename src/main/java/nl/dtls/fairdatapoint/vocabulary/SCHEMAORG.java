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

public final class SCHEMAORG {
    public static final String NAMESPACE = "http://schema.org/";
    public static final String PREFIX = "schemaOrg";

    /** <tt>http://schema.org/APIReference</tt> */
    public static final IRI APIREFERENCE;

    /** <tt>http://schema.org/AboutPage</tt> */
    public static final IRI ABOUTPAGE;

    /** <tt>http://schema.org/AcceptAction</tt> */
    public static final IRI ACCEPTACTION;

    /** <tt>http://schema.org/Accommodation</tt> */
    public static final IRI ACCOMMODATION;

    /** <tt>http://schema.org/AccountingService</tt> */
    public static final IRI ACCOUNTINGSERVICE;

    /** <tt>http://schema.org/AchieveAction</tt> */
    public static final IRI ACHIEVEACTION;

    /** <tt>http://schema.org/Action</tt> */
    public static final IRI ACTION;

    /** <tt>http://schema.org/ActionStatusType</tt> */
    public static final IRI ACTIONSTATUSTYPE;

    /** <tt>http://schema.org/ActivateAction</tt> */
    public static final IRI ACTIVATEACTION;

    /** <tt>http://schema.org/AddAction</tt> */
    public static final IRI ADDACTION;

    /** <tt>http://schema.org/AdministrativeArea</tt> */
    public static final IRI ADMINISTRATIVEAREA;

    /** <tt>http://schema.org/AdultEntertainment</tt> */
    public static final IRI ADULTENTERTAINMENT;

    /** <tt>http://schema.org/AggregateOffer</tt> */
    public static final IRI AGGREGATEOFFER;

    /** <tt>http://schema.org/AggregateRating</tt> */
    public static final IRI AGGREGATERATING;

    /** <tt>http://schema.org/AgreeAction</tt> */
    public static final IRI AGREEACTION;

    /** <tt>http://schema.org/Airline</tt> */
    public static final IRI AIRLINE;

    /** <tt>http://schema.org/Airport</tt> */
    public static final IRI AIRPORT;

    /** <tt>http://schema.org/AlignmentObject</tt> */
    public static final IRI ALIGNMENTOBJECT;

    /** <tt>http://schema.org/AllocateAction</tt> */
    public static final IRI ALLOCATEACTION;

    /** <tt>http://schema.org/AmusementPark</tt> */
    public static final IRI AMUSEMENTPARK;

    /** <tt>http://schema.org/AnimalShelter</tt> */
    public static final IRI ANIMALSHELTER;

    /** <tt>http://schema.org/Answer</tt> */
    public static final IRI ANSWER;

    /** <tt>http://schema.org/Apartment</tt> */
    public static final IRI APARTMENT;

    /** <tt>http://schema.org/ApartmentComplex</tt> */
    public static final IRI APARTMENTCOMPLEX;

    /** <tt>http://schema.org/AppendAction</tt> */
    public static final IRI APPENDACTION;

    /** <tt>http://schema.org/ApplyAction</tt> */
    public static final IRI APPLYACTION;

    /** <tt>http://schema.org/Aquarium</tt> */
    public static final IRI AQUARIUM;

    /** <tt>http://schema.org/ArriveAction</tt> */
    public static final IRI ARRIVEACTION;

    /** <tt>http://schema.org/ArtGallery</tt> */
    public static final IRI ARTGALLERY;

    /** <tt>http://schema.org/Article</tt> */
    public static final IRI ARTICLE;

    /** <tt>http://schema.org/AskAction</tt> */
    public static final IRI ASKACTION;

    /** <tt>http://schema.org/AssessAction</tt> */
    public static final IRI ASSESSACTION;

    /** <tt>http://schema.org/AssignAction</tt> */
    public static final IRI ASSIGNACTION;

    /** <tt>http://schema.org/Attorney</tt> */
    public static final IRI ATTORNEY;

    /** <tt>http://schema.org/Audience</tt> */
    public static final IRI AUDIENCE;

    /** <tt>http://schema.org/AudioObject</tt> */
    public static final IRI AUDIOOBJECT;

    /** <tt>http://schema.org/AuthorizeAction</tt> */
    public static final IRI AUTHORIZEACTION;

    /** <tt>http://schema.org/AutoBodyShop</tt> */
    public static final IRI AUTOBODYSHOP;

    /** <tt>http://schema.org/AutoDealer</tt> */
    public static final IRI AUTODEALER;

    /** <tt>http://schema.org/AutoPartsStore</tt> */
    public static final IRI AUTOPARTSSTORE;

    /** <tt>http://schema.org/AutoRental</tt> */
    public static final IRI AUTORENTAL;

    /** <tt>http://schema.org/AutoRepair</tt> */
    public static final IRI AUTOREPAIR;

    /** <tt>http://schema.org/AutoWash</tt> */
    public static final IRI AUTOWASH;

    /** <tt>http://schema.org/AutomatedTeller</tt> */
    public static final IRI AUTOMATEDTELLER;

    /** <tt>http://schema.org/AutomotiveBusiness</tt> */
    public static final IRI AUTOMOTIVEBUSINESS;

    /** <tt>http://schema.org/Bakery</tt> */
    public static final IRI BAKERY;

    /** <tt>http://schema.org/BankAccount</tt> */
    public static final IRI BANKACCOUNT;

    /** <tt>http://schema.org/BankOrCreditUnion</tt> */
    public static final IRI BANKORCREDITUNION;

    /** <tt>http://schema.org/BarOrPub</tt> */
    public static final IRI BARORPUB;

    /** <tt>http://schema.org/Barcode</tt> */
    public static final IRI BARCODE;

    /** <tt>http://schema.org/Beach</tt> */
    public static final IRI BEACH;

    /** <tt>http://schema.org/BeautySalon</tt> */
    public static final IRI BEAUTYSALON;

    /** <tt>http://schema.org/BedAndBreakfast</tt> */
    public static final IRI BEDANDBREAKFAST;

    /** <tt>http://schema.org/BedDetails</tt> */
    public static final IRI BEDDETAILS;

    /** <tt>http://schema.org/BefriendAction</tt> */
    public static final IRI BEFRIENDACTION;

    /** <tt>http://schema.org/BikeStore</tt> */
    public static final IRI BIKESTORE;

    /** <tt>http://schema.org/Blog</tt> */
    public static final IRI BLOG;

    /** <tt>http://schema.org/BlogPosting</tt> */
    public static final IRI BLOGPOSTING;

    /** <tt>http://schema.org/BoardingPolicyType</tt> */
    public static final IRI BOARDINGPOLICYTYPE;

    /** <tt>http://schema.org/BodyOfWater</tt> */
    public static final IRI BODYOFWATER;

    /** <tt>http://schema.org/Book</tt> */
    public static final IRI BOOK;

    /** <tt>http://schema.org/BookFormatType</tt> */
    public static final IRI BOOKFORMATTYPE;

    /** <tt>http://schema.org/BookSeries</tt> */
    public static final IRI BOOKSERIES;

    /** <tt>http://schema.org/BookStore</tt> */
    public static final IRI BOOKSTORE;

    /** <tt>http://schema.org/BookmarkAction</tt> */
    public static final IRI BOOKMARKACTION;

    /** <tt>http://schema.org/Boolean</tt> */
    public static final IRI BOOLEAN;

    /** <tt>http://schema.org/BorrowAction</tt> */
    public static final IRI BORROWACTION;

    /** <tt>http://schema.org/BowlingAlley</tt> */
    public static final IRI BOWLINGALLEY;

    /** <tt>http://schema.org/Brand</tt> */
    public static final IRI BRAND;

    /** <tt>http://schema.org/BreadcrumbList</tt> */
    public static final IRI BREADCRUMBLIST;

    /** <tt>http://schema.org/Brewery</tt> */
    public static final IRI BREWERY;

    /** <tt>http://schema.org/Bridge</tt> */
    public static final IRI BRIDGE;

    /** <tt>http://schema.org/BroadcastChannel</tt> */
    public static final IRI BROADCASTCHANNEL;

    /** <tt>http://schema.org/BroadcastEvent</tt> */
    public static final IRI BROADCASTEVENT;

    /** <tt>http://schema.org/BroadcastService</tt> */
    public static final IRI BROADCASTSERVICE;

    /** <tt>http://schema.org/BuddhistTemple</tt> */
    public static final IRI BUDDHISTTEMPLE;

    /** <tt>http://schema.org/BusReservation</tt> */
    public static final IRI BUSRESERVATION;

    /** <tt>http://schema.org/BusStation</tt> */
    public static final IRI BUSSTATION;

    /** <tt>http://schema.org/BusStop</tt> */
    public static final IRI BUSSTOP;

    /** <tt>http://schema.org/BusTrip</tt> */
    public static final IRI BUSTRIP;

    /** <tt>http://schema.org/BusinessAudience</tt> */
    public static final IRI BUSINESSAUDIENCE;

    /** <tt>http://schema.org/BusinessEntityType</tt> */
    public static final IRI BUSINESSENTITYTYPE;

    /** <tt>http://schema.org/BusinessEvent</tt> */
    public static final IRI BUSINESSEVENT;

    /** <tt>http://schema.org/BusinessFunction</tt> */
    public static final IRI BUSINESSFUNCTION;

    /** <tt>http://schema.org/BuyAction</tt> */
    public static final IRI BUYACTION;

    /** <tt>http://schema.org/CableOrSatelliteService</tt> */
    public static final IRI CABLEORSATELLITESERVICE;

    /** <tt>http://schema.org/CafeOrCoffeeShop</tt> */
    public static final IRI CAFEORCOFFEESHOP;

    /** <tt>http://schema.org/Campground</tt> */
    public static final IRI CAMPGROUND;

    /** <tt>http://schema.org/CampingPitch</tt> */
    public static final IRI CAMPINGPITCH;

    /** <tt>http://schema.org/Canal</tt> */
    public static final IRI CANAL;

    /** <tt>http://schema.org/CancelAction</tt> */
    public static final IRI CANCELACTION;

    /** <tt>http://schema.org/Car</tt> */
    public static final IRI CAR;

    /** <tt>http://schema.org/Casino</tt> */
    public static final IRI CASINO;

    /** <tt>http://schema.org/CatholicChurch</tt> */
    public static final IRI CATHOLICCHURCH;

    /** <tt>http://schema.org/Cemetery</tt> */
    public static final IRI CEMETERY;

    /** <tt>http://schema.org/CheckAction</tt> */
    public static final IRI CHECKACTION;

    /** <tt>http://schema.org/CheckInAction</tt> */
    public static final IRI CHECKINACTION;

    /** <tt>http://schema.org/CheckOutAction</tt> */
    public static final IRI CHECKOUTACTION;

    /** <tt>http://schema.org/CheckoutPage</tt> */
    public static final IRI CHECKOUTPAGE;

    /** <tt>http://schema.org/ChildCare</tt> */
    public static final IRI CHILDCARE;

    /** <tt>http://schema.org/ChildrensEvent</tt> */
    public static final IRI CHILDRENSEVENT;

    /** <tt>http://schema.org/ChooseAction</tt> */
    public static final IRI CHOOSEACTION;

    /** <tt>http://schema.org/Church</tt> */
    public static final IRI CHURCH;

    /** <tt>http://schema.org/City</tt> */
    public static final IRI CITY;

    /** <tt>http://schema.org/CityHall</tt> */
    public static final IRI CITYHALL;

    /** <tt>http://schema.org/CivicStructure</tt> */
    public static final IRI CIVICSTRUCTURE;

    /** <tt>http://schema.org/ClaimReview</tt> */
    public static final IRI CLAIMREVIEW;

    /** <tt>http://schema.org/Clip</tt> */
    public static final IRI CLIP;

    /** <tt>http://schema.org/ClothingStore</tt> */
    public static final IRI CLOTHINGSTORE;

    /** <tt>http://schema.org/Code</tt> */
    public static final IRI CODE;

    /** <tt>http://schema.org/CollectionPage</tt> */
    public static final IRI COLLECTIONPAGE;

    /** <tt>http://schema.org/CollegeOrUniversity</tt> */
    public static final IRI COLLEGEORUNIVERSITY;

    /** <tt>http://schema.org/ComedyClub</tt> */
    public static final IRI COMEDYCLUB;

    /** <tt>http://schema.org/ComedyEvent</tt> */
    public static final IRI COMEDYEVENT;

    /** <tt>http://schema.org/Comment</tt> */
    public static final IRI COMMENT;

    /** <tt>http://schema.org/CommentAction</tt> */
    public static final IRI COMMENTACTION;

    /** <tt>http://schema.org/CommunicateAction</tt> */
    public static final IRI COMMUNICATEACTION;

    /** <tt>http://schema.org/CompoundPriceSpecification</tt> */
    public static final IRI COMPOUNDPRICESPECIFICATION;

    /** <tt>http://schema.org/ComputerLanguage</tt> */
    public static final IRI COMPUTERLANGUAGE;

    /** <tt>http://schema.org/ComputerStore</tt> */
    public static final IRI COMPUTERSTORE;

    /** <tt>http://schema.org/ConfirmAction</tt> */
    public static final IRI CONFIRMACTION;

    /** <tt>http://schema.org/ConsumeAction</tt> */
    public static final IRI CONSUMEACTION;

    /** <tt>http://schema.org/ContactPage</tt> */
    public static final IRI CONTACTPAGE;

    /** <tt>http://schema.org/ContactPoint</tt> */
    public static final IRI CONTACTPOINT;

    /** <tt>http://schema.org/ContactPointOption</tt> */
    public static final IRI CONTACTPOINTOPTION;

    /** <tt>http://schema.org/Continent</tt> */
    public static final IRI CONTINENT;

    /** <tt>http://schema.org/ControlAction</tt> */
    public static final IRI CONTROLACTION;

    /** <tt>http://schema.org/ConvenienceStore</tt> */
    public static final IRI CONVENIENCESTORE;

    /** <tt>http://schema.org/Conversation</tt> */
    public static final IRI CONVERSATION;

    /** <tt>http://schema.org/CookAction</tt> */
    public static final IRI COOKACTION;

    /** <tt>http://schema.org/Corporation</tt> */
    public static final IRI CORPORATION;

    /** <tt>http://schema.org/Country</tt> */
    public static final IRI COUNTRY;

    /** <tt>http://schema.org/Course</tt> */
    public static final IRI COURSE;

    /** <tt>http://schema.org/CourseInstance</tt> */
    public static final IRI COURSEINSTANCE;

    /** <tt>http://schema.org/Courthouse</tt> */
    public static final IRI COURTHOUSE;

    /** <tt>http://schema.org/CreateAction</tt> */
    public static final IRI CREATEACTION;

    /** <tt>http://schema.org/CreativeWork</tt> */
    public static final IRI CREATIVEWORK;

    /** <tt>http://schema.org/CreativeWorkSeason</tt> */
    public static final IRI CREATIVEWORKSEASON;

    /** <tt>http://schema.org/CreativeWorkSeries</tt> */
    public static final IRI CREATIVEWORKSERIES;

    /** <tt>http://schema.org/CreditCard</tt> */
    public static final IRI CREDITCARD;

    /** <tt>http://schema.org/Crematorium</tt> */
    public static final IRI CREMATORIUM;

    /** <tt>http://schema.org/CurrencyConversionService</tt> */
    public static final IRI CURRENCYCONVERSIONSERVICE;

    /** <tt>http://schema.org/DanceEvent</tt> */
    public static final IRI DANCEEVENT;

    /** <tt>http://schema.org/DanceGroup</tt> */
    public static final IRI DANCEGROUP;

    /** <tt>http://schema.org/DataCatalog</tt> */
    public static final IRI DATACATALOG;

    /** <tt>http://schema.org/DataDownload</tt> */
    public static final IRI DATADOWNLOAD;

    /** <tt>http://schema.org/DataFeed</tt> */
    public static final IRI DATAFEED;

    /** <tt>http://schema.org/DataFeedItem</tt> */
    public static final IRI DATAFEEDITEM;

    /** <tt>http://schema.org/DataType</tt> */
    public static final IRI DATATYPE;

    /** <tt>http://schema.org/Dataset</tt> */
    public static final IRI DATASET;

    /** <tt>http://schema.org/Date</tt> */
    public static final IRI DATE;

    /** <tt>http://schema.org/DateTime</tt> */
    public static final IRI DATETIME;

    /** <tt>http://schema.org/DatedMoneySpecification</tt> */
    public static final IRI DATEDMONEYSPECIFICATION;

    /** <tt>http://schema.org/DayOfWeek</tt> */
    public static final IRI DAYOFWEEK;

    /** <tt>http://schema.org/DaySpa</tt> */
    public static final IRI DAYSPA;

    /** <tt>http://schema.org/DeactivateAction</tt> */
    public static final IRI DEACTIVATEACTION;

    /** <tt>http://schema.org/DefenceEstablishment</tt> */
    public static final IRI DEFENCEESTABLISHMENT;

    /** <tt>http://schema.org/DeleteAction</tt> */
    public static final IRI DELETEACTION;

    /** <tt>http://schema.org/DeliveryChargeSpecification</tt> */
    public static final IRI DELIVERYCHARGESPECIFICATION;

    /** <tt>http://schema.org/DeliveryEvent</tt> */
    public static final IRI DELIVERYEVENT;

    /** <tt>http://schema.org/DeliveryMethod</tt> */
    public static final IRI DELIVERYMETHOD;

    /** <tt>http://schema.org/Demand</tt> */
    public static final IRI DEMAND;

    /** <tt>http://schema.org/Dentist</tt> */
    public static final IRI DENTIST;

    /** <tt>http://schema.org/DepartAction</tt> */
    public static final IRI DEPARTACTION;

    /** <tt>http://schema.org/DepartmentStore</tt> */
    public static final IRI DEPARTMENTSTORE;

    /** <tt>http://schema.org/DepositAccount</tt> */
    public static final IRI DEPOSITACCOUNT;

    /** <tt>http://schema.org/DigitalDocument</tt> */
    public static final IRI DIGITALDOCUMENT;

    /** <tt>http://schema.org/DigitalDocumentPermission</tt> */
    public static final IRI DIGITALDOCUMENTPERMISSION;

    /** <tt>http://schema.org/DigitalDocumentPermissionType</tt> */
    public static final IRI DIGITALDOCUMENTPERMISSIONTYPE;

    /** <tt>http://schema.org/DisagreeAction</tt> */
    public static final IRI DISAGREEACTION;

    /** <tt>http://schema.org/DiscoverAction</tt> */
    public static final IRI DISCOVERACTION;

    /** <tt>http://schema.org/DiscussionForumPosting</tt> */
    public static final IRI DISCUSSIONFORUMPOSTING;

    /** <tt>http://schema.org/DislikeAction</tt> */
    public static final IRI DISLIKEACTION;

    /** <tt>http://schema.org/Distance</tt> */
    public static final IRI DISTANCE;

    /** <tt>http://schema.org/DonateAction</tt> */
    public static final IRI DONATEACTION;

    /** <tt>http://schema.org/DownloadAction</tt> */
    public static final IRI DOWNLOADACTION;

    /** <tt>http://schema.org/DrawAction</tt> */
    public static final IRI DRAWACTION;

    /** <tt>http://schema.org/DrinkAction</tt> */
    public static final IRI DRINKACTION;

    /** <tt>http://schema.org/DriveWheelConfigurationValue</tt> */
    public static final IRI DRIVEWHEELCONFIGURATIONVALUE;

    /** <tt>http://schema.org/DryCleaningOrLaundry</tt> */
    public static final IRI DRYCLEANINGORLAUNDRY;

    /** <tt>http://schema.org/Duration</tt> */
    public static final IRI DURATION;

    /** <tt>http://schema.org/EatAction</tt> */
    public static final IRI EATACTION;

    /** <tt>http://schema.org/EducationEvent</tt> */
    public static final IRI EDUCATIONEVENT;

    /** <tt>http://schema.org/EducationalAudience</tt> */
    public static final IRI EDUCATIONALAUDIENCE;

    /** <tt>http://schema.org/EducationalOrganization</tt> */
    public static final IRI EDUCATIONALORGANIZATION;

    /** <tt>http://schema.org/Electrician</tt> */
    public static final IRI ELECTRICIAN;

    /** <tt>http://schema.org/ElectronicsStore</tt> */
    public static final IRI ELECTRONICSSTORE;

    /** <tt>http://schema.org/ElementarySchool</tt> */
    public static final IRI ELEMENTARYSCHOOL;

    /** <tt>http://schema.org/EmailMessage</tt> */
    public static final IRI EMAILMESSAGE;

    /** <tt>http://schema.org/Embassy</tt> */
    public static final IRI EMBASSY;

    /** <tt>http://schema.org/EmergencyService</tt> */
    public static final IRI EMERGENCYSERVICE;

    /** <tt>http://schema.org/EmployeeRole</tt> */
    public static final IRI EMPLOYEEROLE;

    /** <tt>http://schema.org/EmploymentAgency</tt> */
    public static final IRI EMPLOYMENTAGENCY;

    /** <tt>http://schema.org/EndorseAction</tt> */
    public static final IRI ENDORSEACTION;

    /** <tt>http://schema.org/Energy</tt> */
    public static final IRI ENERGY;

    /** <tt>http://schema.org/EngineSpecification</tt> */
    public static final IRI ENGINESPECIFICATION;

    /** <tt>http://schema.org/EntertainmentBusiness</tt> */
    public static final IRI ENTERTAINMENTBUSINESS;

    /** <tt>http://schema.org/EntryPoint</tt> */
    public static final IRI ENTRYPOINT;

    /** <tt>http://schema.org/Enumeration</tt> */
    public static final IRI ENUMERATION;

    /** <tt>http://schema.org/Episode</tt> */
    public static final IRI EPISODE;

    /** <tt>http://schema.org/Event</tt> */
    public static final IRI EVENT;

    /** <tt>http://schema.org/EventReservation</tt> */
    public static final IRI EVENTRESERVATION;

    /** <tt>http://schema.org/EventStatusType</tt> */
    public static final IRI EVENTSTATUSTYPE;

    /** <tt>http://schema.org/EventVenue</tt> */
    public static final IRI EVENTVENUE;

    /** <tt>http://schema.org/ExerciseAction</tt> */
    public static final IRI EXERCISEACTION;

    /** <tt>http://schema.org/ExerciseGym</tt> */
    public static final IRI EXERCISEGYM;

    /** <tt>http://schema.org/ExhibitionEvent</tt> */
    public static final IRI EXHIBITIONEVENT;

    /** <tt>http://schema.org/FastFoodRestaurant</tt> */
    public static final IRI FASTFOODRESTAURANT;

    /** <tt>http://schema.org/Festival</tt> */
    public static final IRI FESTIVAL;

    /** <tt>http://schema.org/FilmAction</tt> */
    public static final IRI FILMACTION;

    /** <tt>http://schema.org/FinancialProduct</tt> */
    public static final IRI FINANCIALPRODUCT;

    /** <tt>http://schema.org/FinancialService</tt> */
    public static final IRI FINANCIALSERVICE;

    /** <tt>http://schema.org/FindAction</tt> */
    public static final IRI FINDACTION;

    /** <tt>http://schema.org/FireStation</tt> */
    public static final IRI FIRESTATION;

    /** <tt>http://schema.org/Flight</tt> */
    public static final IRI FLIGHT;

    /** <tt>http://schema.org/FlightReservation</tt> */
    public static final IRI FLIGHTRESERVATION;

    /** <tt>http://schema.org/Float</tt> */
    public static final IRI FLOAT;

    /** <tt>http://schema.org/Florist</tt> */
    public static final IRI FLORIST;

    /** <tt>http://schema.org/FollowAction</tt> */
    public static final IRI FOLLOWACTION;

    /** <tt>http://schema.org/FoodEstablishment</tt> */
    public static final IRI FOODESTABLISHMENT;

    /** <tt>http://schema.org/FoodEstablishmentReservation</tt> */
    public static final IRI FOODESTABLISHMENTRESERVATION;

    /** <tt>http://schema.org/FoodEvent</tt> */
    public static final IRI FOODEVENT;

    /** <tt>http://schema.org/FoodService</tt> */
    public static final IRI FOODSERVICE;

    /** <tt>http://schema.org/FurnitureStore</tt> */
    public static final IRI FURNITURESTORE;

    /** <tt>http://schema.org/Game</tt> */
    public static final IRI GAME;

    /** <tt>http://schema.org/GamePlayMode</tt> */
    public static final IRI GAMEPLAYMODE;

    /** <tt>http://schema.org/GameServer</tt> */
    public static final IRI GAMESERVER;

    /** <tt>http://schema.org/GameServerStatus</tt> */
    public static final IRI GAMESERVERSTATUS;

    /** <tt>http://schema.org/GardenStore</tt> */
    public static final IRI GARDENSTORE;

    /** <tt>http://schema.org/GasStation</tt> */
    public static final IRI GASSTATION;

    /** <tt>http://schema.org/GatedResidenceCommunity</tt> */
    public static final IRI GATEDRESIDENCECOMMUNITY;

    /** <tt>http://schema.org/GenderType</tt> */
    public static final IRI GENDERTYPE;

    /** <tt>http://schema.org/GeneralContractor</tt> */
    public static final IRI GENERALCONTRACTOR;

    /** <tt>http://schema.org/GeoCircle</tt> */
    public static final IRI GEOCIRCLE;

    /** <tt>http://schema.org/GeoCoordinates</tt> */
    public static final IRI GEOCOORDINATES;

    /** <tt>http://schema.org/GeoShape</tt> */
    public static final IRI GEOSHAPE;

    /** <tt>http://schema.org/GiveAction</tt> */
    public static final IRI GIVEACTION;

    /** <tt>http://schema.org/GolfCourse</tt> */
    public static final IRI GOLFCOURSE;

    /** <tt>http://schema.org/GovernmentBuilding</tt> */
    public static final IRI GOVERNMENTBUILDING;

    /** <tt>http://schema.org/GovernmentOffice</tt> */
    public static final IRI GOVERNMENTOFFICE;

    /** <tt>http://schema.org/GovernmentOrganization</tt> */
    public static final IRI GOVERNMENTORGANIZATION;

    /** <tt>http://schema.org/GovernmentPermit</tt> */
    public static final IRI GOVERNMENTPERMIT;

    /** <tt>http://schema.org/GovernmentService</tt> */
    public static final IRI GOVERNMENTSERVICE;

    /** <tt>http://schema.org/GroceryStore</tt> */
    public static final IRI GROCERYSTORE;

    /** <tt>http://schema.org/HVACBusiness</tt> */
    public static final IRI HVACBUSINESS;

    /** <tt>http://schema.org/HairSalon</tt> */
    public static final IRI HAIRSALON;

    /** <tt>http://schema.org/HardwareStore</tt> */
    public static final IRI HARDWARESTORE;

    /** <tt>http://schema.org/HealthAndBeautyBusiness</tt> */
    public static final IRI HEALTHANDBEAUTYBUSINESS;

    /** <tt>http://schema.org/HealthClub</tt> */
    public static final IRI HEALTHCLUB;

    /** <tt>http://schema.org/HighSchool</tt> */
    public static final IRI HIGHSCHOOL;

    /** <tt>http://schema.org/HinduTemple</tt> */
    public static final IRI HINDUTEMPLE;

    /** <tt>http://schema.org/HobbyShop</tt> */
    public static final IRI HOBBYSHOP;

    /** <tt>http://schema.org/HomeAndConstructionBusiness</tt> */
    public static final IRI HOMEANDCONSTRUCTIONBUSINESS;

    /** <tt>http://schema.org/HomeGoodsStore</tt> */
    public static final IRI HOMEGOODSSTORE;

    /** <tt>http://schema.org/Hospital</tt> */
    public static final IRI HOSPITAL;

    /** <tt>http://schema.org/Hostel</tt> */
    public static final IRI HOSTEL;

    /** <tt>http://schema.org/Hotel</tt> */
    public static final IRI HOTEL;

    /** <tt>http://schema.org/HotelRoom</tt> */
    public static final IRI HOTELROOM;

    /** <tt>http://schema.org/House</tt> */
    public static final IRI HOUSE;

    /** <tt>http://schema.org/HousePainter</tt> */
    public static final IRI HOUSEPAINTER;

    /** <tt>http://schema.org/HowTo</tt> */
    public static final IRI HOWTO;

    /** <tt>http://schema.org/HowToDirection</tt> */
    public static final IRI HOWTODIRECTION;

    /** <tt>http://schema.org/HowToItem</tt> */
    public static final IRI HOWTOITEM;

    /** <tt>http://schema.org/HowToSection</tt> */
    public static final IRI HOWTOSECTION;

    /** <tt>http://schema.org/HowToStep</tt> */
    public static final IRI HOWTOSTEP;

    /** <tt>http://schema.org/HowToSupply</tt> */
    public static final IRI HOWTOSUPPLY;

    /** <tt>http://schema.org/HowToTip</tt> */
    public static final IRI HOWTOTIP;

    /** <tt>http://schema.org/HowToTool</tt> */
    public static final IRI HOWTOTOOL;

    /** <tt>http://schema.org/IceCreamShop</tt> */
    public static final IRI ICECREAMSHOP;

    /** <tt>http://schema.org/IgnoreAction</tt> */
    public static final IRI IGNOREACTION;

    /** <tt>http://schema.org/ImageGallery</tt> */
    public static final IRI IMAGEGALLERY;

    /** <tt>http://schema.org/ImageObject</tt> */
    public static final IRI IMAGEOBJECT;

    /** <tt>http://schema.org/IndividualProduct</tt> */
    public static final IRI INDIVIDUALPRODUCT;

    /** <tt>http://schema.org/InformAction</tt> */
    public static final IRI INFORMACTION;

    /** <tt>http://schema.org/InsertAction</tt> */
    public static final IRI INSERTACTION;

    /** <tt>http://schema.org/InstallAction</tt> */
    public static final IRI INSTALLACTION;

    /** <tt>http://schema.org/InsuranceAgency</tt> */
    public static final IRI INSURANCEAGENCY;

    /** <tt>http://schema.org/Intangible</tt> */
    public static final IRI INTANGIBLE;

    /** <tt>http://schema.org/Integer</tt> */
    public static final IRI INTEGER;

    /** <tt>http://schema.org/InteractAction</tt> */
    public static final IRI INTERACTACTION;

    /** <tt>http://schema.org/InteractionCounter</tt> */
    public static final IRI INTERACTIONCOUNTER;

    /** <tt>http://schema.org/InternetCafe</tt> */
    public static final IRI INTERNETCAFE;

    /** <tt>http://schema.org/InvestmentOrDeposit</tt> */
    public static final IRI INVESTMENTORDEPOSIT;

    /** <tt>http://schema.org/InviteAction</tt> */
    public static final IRI INVITEACTION;

    /** <tt>http://schema.org/Invoice</tt> */
    public static final IRI INVOICE;

    /** <tt>http://schema.org/ItemAvailability</tt> */
    public static final IRI ITEMAVAILABILITY;

    /** <tt>http://schema.org/ItemList</tt> */
    public static final IRI ITEMLIST;

    /** <tt>http://schema.org/ItemListOrderType</tt> */
    public static final IRI ITEMLISTORDERTYPE;

    /** <tt>http://schema.org/ItemPage</tt> */
    public static final IRI ITEMPAGE;

    /** <tt>http://schema.org/JewelryStore</tt> */
    public static final IRI JEWELRYSTORE;

    /** <tt>http://schema.org/JobPosting</tt> */
    public static final IRI JOBPOSTING;

    /** <tt>http://schema.org/JoinAction</tt> */
    public static final IRI JOINACTION;

    /** <tt>http://schema.org/LakeBodyOfWater</tt> */
    public static final IRI LAKEBODYOFWATER;

    /** <tt>http://schema.org/Landform</tt> */
    public static final IRI LANDFORM;

    /** <tt>http://schema.org/LandmarksOrHistoricalBuildings</tt> */
    public static final IRI LANDMARKSORHISTORICALBUILDINGS;

    /** <tt>http://schema.org/Language</tt> */
    public static final IRI LANGUAGE;

    /** <tt>http://schema.org/LeaveAction</tt> */
    public static final IRI LEAVEACTION;

    /** <tt>http://schema.org/LegalService</tt> */
    public static final IRI LEGALSERVICE;

    /** <tt>http://schema.org/LegislativeBuilding</tt> */
    public static final IRI LEGISLATIVEBUILDING;

    /** <tt>http://schema.org/LendAction</tt> */
    public static final IRI LENDACTION;

    /** <tt>http://schema.org/Library</tt> */
    public static final IRI LIBRARY;

    /** <tt>http://schema.org/LikeAction</tt> */
    public static final IRI LIKEACTION;

    /** <tt>http://schema.org/LiquorStore</tt> */
    public static final IRI LIQUORSTORE;

    /** <tt>http://schema.org/ListItem</tt> */
    public static final IRI LISTITEM;

    /** <tt>http://schema.org/ListenAction</tt> */
    public static final IRI LISTENACTION;

    /** <tt>http://schema.org/LiteraryEvent</tt> */
    public static final IRI LITERARYEVENT;

    /** <tt>http://schema.org/LiveBlogPosting</tt> */
    public static final IRI LIVEBLOGPOSTING;

    /** <tt>http://schema.org/LoanOrCredit</tt> */
    public static final IRI LOANORCREDIT;

    /** <tt>http://schema.org/LocalBusiness</tt> */
    public static final IRI LOCALBUSINESS;

    /** <tt>http://schema.org/LocationFeatureSpecification</tt> */
    public static final IRI LOCATIONFEATURESPECIFICATION;

    /** <tt>http://schema.org/LockerDelivery</tt> */
    public static final IRI LOCKERDELIVERY;

    /** <tt>http://schema.org/Locksmith</tt> */
    public static final IRI LOCKSMITH;

    /** <tt>http://schema.org/LodgingBusiness</tt> */
    public static final IRI LODGINGBUSINESS;

    /** <tt>http://schema.org/LodgingReservation</tt> */
    public static final IRI LODGINGRESERVATION;

    /** <tt>http://schema.org/LoseAction</tt> */
    public static final IRI LOSEACTION;

    /** <tt>http://schema.org/Map</tt> */
    public static final IRI MAP;

    /** <tt>http://schema.org/MapCategoryType</tt> */
    public static final IRI MAPCATEGORYTYPE;

    /** <tt>http://schema.org/MarryAction</tt> */
    public static final IRI MARRYACTION;

    /** <tt>http://schema.org/Mass</tt> */
    public static final IRI MASS;

    /** <tt>http://schema.org/MediaObject</tt> */
    public static final IRI MEDIAOBJECT;

    /** <tt>http://schema.org/MedicalOrganization</tt> */
    public static final IRI MEDICALORGANIZATION;

    /** <tt>http://schema.org/MeetingRoom</tt> */
    public static final IRI MEETINGROOM;

    /** <tt>http://schema.org/MensClothingStore</tt> */
    public static final IRI MENSCLOTHINGSTORE;

    /** <tt>http://schema.org/Menu</tt> */
    public static final IRI MENU;

    /** <tt>http://schema.org/MenuItem</tt> */
    public static final IRI MENUITEM;

    /** <tt>http://schema.org/MenuSection</tt> */
    public static final IRI MENUSECTION;

    /** <tt>http://schema.org/Message</tt> */
    public static final IRI MESSAGE;

    /** <tt>http://schema.org/MiddleSchool</tt> */
    public static final IRI MIDDLESCHOOL;

    /** <tt>http://schema.org/MobileApplication</tt> */
    public static final IRI MOBILEAPPLICATION;

    /** <tt>http://schema.org/MobilePhoneStore</tt> */
    public static final IRI MOBILEPHONESTORE;

    /** <tt>http://schema.org/MonetaryAmount</tt> */
    public static final IRI MONETARYAMOUNT;

    /** <tt>http://schema.org/Mosque</tt> */
    public static final IRI MOSQUE;

    /** <tt>http://schema.org/Motel</tt> */
    public static final IRI MOTEL;

    /** <tt>http://schema.org/MotorcycleDealer</tt> */
    public static final IRI MOTORCYCLEDEALER;

    /** <tt>http://schema.org/MotorcycleRepair</tt> */
    public static final IRI MOTORCYCLEREPAIR;

    /** <tt>http://schema.org/Mountain</tt> */
    public static final IRI MOUNTAIN;

    /** <tt>http://schema.org/MoveAction</tt> */
    public static final IRI MOVEACTION;

    /** <tt>http://schema.org/Movie</tt> */
    public static final IRI MOVIE;

    /** <tt>http://schema.org/MovieClip</tt> */
    public static final IRI MOVIECLIP;

    /** <tt>http://schema.org/MovieRentalStore</tt> */
    public static final IRI MOVIERENTALSTORE;

    /** <tt>http://schema.org/MovieSeries</tt> */
    public static final IRI MOVIESERIES;

    /** <tt>http://schema.org/MovieTheater</tt> */
    public static final IRI MOVIETHEATER;

    /** <tt>http://schema.org/MovingCompany</tt> */
    public static final IRI MOVINGCOMPANY;

    /** <tt>http://schema.org/Museum</tt> */
    public static final IRI MUSEUM;

    /** <tt>http://schema.org/MusicAlbum</tt> */
    public static final IRI MUSICALBUM;

    /** <tt>http://schema.org/MusicAlbumProductionType</tt> */
    public static final IRI MUSICALBUMPRODUCTIONTYPE;

    /** <tt>http://schema.org/MusicAlbumReleaseType</tt> */
    public static final IRI MUSICALBUMRELEASETYPE;

    /** <tt>http://schema.org/MusicComposition</tt> */
    public static final IRI MUSICCOMPOSITION;

    /** <tt>http://schema.org/MusicEvent</tt> */
    public static final IRI MUSICEVENT;

    /** <tt>http://schema.org/MusicGroup</tt> */
    public static final IRI MUSICGROUP;

    /** <tt>http://schema.org/MusicPlaylist</tt> */
    public static final IRI MUSICPLAYLIST;

    /** <tt>http://schema.org/MusicRecording</tt> */
    public static final IRI MUSICRECORDING;

    /** <tt>http://schema.org/MusicRelease</tt> */
    public static final IRI MUSICRELEASE;

    /** <tt>http://schema.org/MusicReleaseFormatType</tt> */
    public static final IRI MUSICRELEASEFORMATTYPE;

    /** <tt>http://schema.org/MusicStore</tt> */
    public static final IRI MUSICSTORE;

    /** <tt>http://schema.org/MusicVenue</tt> */
    public static final IRI MUSICVENUE;

    /** <tt>http://schema.org/MusicVideoObject</tt> */
    public static final IRI MUSICVIDEOOBJECT;

    /** <tt>http://schema.org/NGO</tt> */
    public static final IRI NGO;

    /** <tt>http://schema.org/NailSalon</tt> */
    public static final IRI NAILSALON;

    /** <tt>http://schema.org/NewsArticle</tt> */
    public static final IRI NEWSARTICLE;

    /** <tt>http://schema.org/NightClub</tt> */
    public static final IRI NIGHTCLUB;

    /** <tt>http://schema.org/Notary</tt> */
    public static final IRI NOTARY;

    /** <tt>http://schema.org/NoteDigitalDocument</tt> */
    public static final IRI NOTEDIGITALDOCUMENT;

    /** <tt>http://schema.org/Number</tt> */
    public static final IRI NUMBER;

    /** <tt>http://schema.org/NutritionInformation</tt> */
    public static final IRI NUTRITIONINFORMATION;

    /** <tt>http://schema.org/OceanBodyOfWater</tt> */
    public static final IRI OCEANBODYOFWATER;

    /** <tt>http://schema.org/Offer</tt> */
    public static final IRI OFFER;

    /** <tt>http://schema.org/OfferCatalog</tt> */
    public static final IRI OFFERCATALOG;

    /** <tt>http://schema.org/OfferItemCondition</tt> */
    public static final IRI OFFERITEMCONDITION;

    /** <tt>http://schema.org/OfficeEquipmentStore</tt> */
    public static final IRI OFFICEEQUIPMENTSTORE;

    /** <tt>http://schema.org/OnDemandEvent</tt> */
    public static final IRI ONDEMANDEVENT;

    /** <tt>http://schema.org/OpeningHoursSpecification</tt> */
    public static final IRI OPENINGHOURSSPECIFICATION;

    /** <tt>http://schema.org/Order</tt> */
    public static final IRI ORDER;

    /** <tt>http://schema.org/OrderAction</tt> */
    public static final IRI ORDERACTION;

    /** <tt>http://schema.org/OrderItem</tt> */
    public static final IRI ORDERITEM;

    /** <tt>http://schema.org/OrderStatus</tt> */
    public static final IRI ORDERSTATUS;

    /** <tt>http://schema.org/Organization</tt> */
    public static final IRI ORGANIZATION;

    /** <tt>http://schema.org/OrganizationRole</tt> */
    public static final IRI ORGANIZATIONROLE;

    /** <tt>http://schema.org/OrganizeAction</tt> */
    public static final IRI ORGANIZEACTION;

    /** <tt>http://schema.org/OutletStore</tt> */
    public static final IRI OUTLETSTORE;

    /** <tt>http://schema.org/OwnershipInfo</tt> */
    public static final IRI OWNERSHIPINFO;

    /** <tt>http://schema.org/PaintAction</tt> */
    public static final IRI PAINTACTION;

    /** <tt>http://schema.org/Painting</tt> */
    public static final IRI PAINTING;

    /** <tt>http://schema.org/ParcelDelivery</tt> */
    public static final IRI PARCELDELIVERY;

    /** <tt>http://schema.org/ParcelService</tt> */
    public static final IRI PARCELSERVICE;

    /** <tt>http://schema.org/ParentAudience</tt> */
    public static final IRI PARENTAUDIENCE;

    /** <tt>http://schema.org/Park</tt> */
    public static final IRI PARK;

    /** <tt>http://schema.org/ParkingFacility</tt> */
    public static final IRI PARKINGFACILITY;

    /** <tt>http://schema.org/PawnShop</tt> */
    public static final IRI PAWNSHOP;

    /** <tt>http://schema.org/PayAction</tt> */
    public static final IRI PAYACTION;

    /** <tt>http://schema.org/PaymentCard</tt> */
    public static final IRI PAYMENTCARD;

    /** <tt>http://schema.org/PaymentChargeSpecification</tt> */
    public static final IRI PAYMENTCHARGESPECIFICATION;

    /** <tt>http://schema.org/PaymentMethod</tt> */
    public static final IRI PAYMENTMETHOD;

    /** <tt>http://schema.org/PaymentService</tt> */
    public static final IRI PAYMENTSERVICE;

    /** <tt>http://schema.org/PaymentStatusType</tt> */
    public static final IRI PAYMENTSTATUSTYPE;

    /** <tt>http://schema.org/PeopleAudience</tt> */
    public static final IRI PEOPLEAUDIENCE;

    /** <tt>http://schema.org/PerformAction</tt> */
    public static final IRI PERFORMACTION;

    /** <tt>http://schema.org/PerformanceRole</tt> */
    public static final IRI PERFORMANCEROLE;

    /** <tt>http://schema.org/PerformingArtsTheater</tt> */
    public static final IRI PERFORMINGARTSTHEATER;

    /** <tt>http://schema.org/PerformingGroup</tt> */
    public static final IRI PERFORMINGGROUP;

    /** <tt>http://schema.org/Periodical</tt> */
    public static final IRI PERIODICAL;

    /** <tt>http://schema.org/Permit</tt> */
    public static final IRI PERMIT;

    /** <tt>http://schema.org/Person</tt> */
    public static final IRI PERSON;

    /** <tt>http://schema.org/PetStore</tt> */
    public static final IRI PETSTORE;

    /** <tt>http://schema.org/Pharmacy</tt> */
    public static final IRI PHARMACY;

    /** <tt>http://schema.org/Photograph</tt> */
    public static final IRI PHOTOGRAPH;

    /** <tt>http://schema.org/PhotographAction</tt> */
    public static final IRI PHOTOGRAPHACTION;

    /** <tt>http://schema.org/Physician</tt> */
    public static final IRI PHYSICIAN;

    /** <tt>http://schema.org/Place</tt> */
    public static final IRI PLACE;

    /** <tt>http://schema.org/PlaceOfWorship</tt> */
    public static final IRI PLACEOFWORSHIP;

    /** <tt>http://schema.org/PlanAction</tt> */
    public static final IRI PLANACTION;

    /** <tt>http://schema.org/PlayAction</tt> */
    public static final IRI PLAYACTION;

    /** <tt>http://schema.org/Playground</tt> */
    public static final IRI PLAYGROUND;

    /** <tt>http://schema.org/Plumber</tt> */
    public static final IRI PLUMBER;

    /** <tt>http://schema.org/PoliceStation</tt> */
    public static final IRI POLICESTATION;

    /** <tt>http://schema.org/Pond</tt> */
    public static final IRI POND;

    /** <tt>http://schema.org/PostOffice</tt> */
    public static final IRI POSTOFFICE;

    /** <tt>http://schema.org/PostalAddress</tt> */
    public static final IRI POSTALADDRESS;

    /** <tt>http://schema.org/PrependAction</tt> */
    public static final IRI PREPENDACTION;

    /** <tt>http://schema.org/Preschool</tt> */
    public static final IRI PRESCHOOL;

    /** <tt>http://schema.org/PresentationDigitalDocument</tt> */
    public static final IRI PRESENTATIONDIGITALDOCUMENT;

    /** <tt>http://schema.org/PriceSpecification</tt> */
    public static final IRI PRICESPECIFICATION;

    /** <tt>http://schema.org/Product</tt> */
    public static final IRI PRODUCT;

    /** <tt>http://schema.org/ProductModel</tt> */
    public static final IRI PRODUCTMODEL;

    /** <tt>http://schema.org/ProfessionalService</tt> */
    public static final IRI PROFESSIONALSERVICE;

    /** <tt>http://schema.org/ProfilePage</tt> */
    public static final IRI PROFILEPAGE;

    /** <tt>http://schema.org/ProgramMembership</tt> */
    public static final IRI PROGRAMMEMBERSHIP;

    /** <tt>http://schema.org/PropertyValue</tt> */
    public static final IRI PROPERTYVALUE;

    /** <tt>http://schema.org/PropertyValueSpecification</tt> */
    public static final IRI PROPERTYVALUESPECIFICATION;

    /** <tt>http://schema.org/PublicSwimmingPool</tt> */
    public static final IRI PUBLICSWIMMINGPOOL;

    /** <tt>http://schema.org/PublicationEvent</tt> */
    public static final IRI PUBLICATIONEVENT;

    /** <tt>http://schema.org/PublicationIssue</tt> */
    public static final IRI PUBLICATIONISSUE;

    /** <tt>http://schema.org/PublicationVolume</tt> */
    public static final IRI PUBLICATIONVOLUME;

    /** <tt>http://schema.org/QAPage</tt> */
    public static final IRI QAPAGE;

    /** <tt>http://schema.org/QualitativeValue</tt> */
    public static final IRI QUALITATIVEVALUE;

    /** <tt>http://schema.org/QuantitativeValue</tt> */
    public static final IRI QUANTITATIVEVALUE;

    /** <tt>http://schema.org/Quantity</tt> */
    public static final IRI QUANTITY;

    /** <tt>http://schema.org/Question</tt> */
    public static final IRI QUESTION;

    /** <tt>http://schema.org/QuoteAction</tt> */
    public static final IRI QUOTEACTION;

    /** <tt>http://schema.org/RVPark</tt> */
    public static final IRI RVPARK;

    /** <tt>http://schema.org/RadioChannel</tt> */
    public static final IRI RADIOCHANNEL;

    /** <tt>http://schema.org/RadioClip</tt> */
    public static final IRI RADIOCLIP;

    /** <tt>http://schema.org/RadioEpisode</tt> */
    public static final IRI RADIOEPISODE;

    /** <tt>http://schema.org/RadioSeason</tt> */
    public static final IRI RADIOSEASON;

    /** <tt>http://schema.org/RadioSeries</tt> */
    public static final IRI RADIOSERIES;

    /** <tt>http://schema.org/RadioStation</tt> */
    public static final IRI RADIOSTATION;

    /** <tt>http://schema.org/Rating</tt> */
    public static final IRI RATING;

    /** <tt>http://schema.org/ReactAction</tt> */
    public static final IRI REACTACTION;

    /** <tt>http://schema.org/ReadAction</tt> */
    public static final IRI READACTION;

    /** <tt>http://schema.org/RealEstateAgent</tt> */
    public static final IRI REALESTATEAGENT;

    /** <tt>http://schema.org/ReceiveAction</tt> */
    public static final IRI RECEIVEACTION;

    /** <tt>http://schema.org/Recipe</tt> */
    public static final IRI RECIPE;

    /** <tt>http://schema.org/RecyclingCenter</tt> */
    public static final IRI RECYCLINGCENTER;

    /** <tt>http://schema.org/RegisterAction</tt> */
    public static final IRI REGISTERACTION;

    /** <tt>http://schema.org/RejectAction</tt> */
    public static final IRI REJECTACTION;

    /** <tt>http://schema.org/RentAction</tt> */
    public static final IRI RENTACTION;

    /** <tt>http://schema.org/RentalCarReservation</tt> */
    public static final IRI RENTALCARRESERVATION;

    /** <tt>http://schema.org/ReplaceAction</tt> */
    public static final IRI REPLACEACTION;

    /** <tt>http://schema.org/ReplyAction</tt> */
    public static final IRI REPLYACTION;

    /** <tt>http://schema.org/Report</tt> */
    public static final IRI REPORT;

    /** <tt>http://schema.org/Reservation</tt> */
    public static final IRI RESERVATION;

    /** <tt>http://schema.org/ReservationPackage</tt> */
    public static final IRI RESERVATIONPACKAGE;

    /** <tt>http://schema.org/ReservationStatusType</tt> */
    public static final IRI RESERVATIONSTATUSTYPE;

    /** <tt>http://schema.org/ReserveAction</tt> */
    public static final IRI RESERVEACTION;

    /** <tt>http://schema.org/Reservoir</tt> */
    public static final IRI RESERVOIR;

    /** <tt>http://schema.org/Residence</tt> */
    public static final IRI RESIDENCE;

    /** <tt>http://schema.org/Resort</tt> */
    public static final IRI RESORT;

    /** <tt>http://schema.org/Restaurant</tt> */
    public static final IRI RESTAURANT;

    /** <tt>http://schema.org/RestrictedDiet</tt> */
    public static final IRI RESTRICTEDDIET;

    /** <tt>http://schema.org/ResumeAction</tt> */
    public static final IRI RESUMEACTION;

    /** <tt>http://schema.org/ReturnAction</tt> */
    public static final IRI RETURNACTION;

    /** <tt>http://schema.org/Review</tt> */
    public static final IRI REVIEW;

    /** <tt>http://schema.org/ReviewAction</tt> */
    public static final IRI REVIEWACTION;

    /** <tt>http://schema.org/RiverBodyOfWater</tt> */
    public static final IRI RIVERBODYOFWATER;

    /** <tt>http://schema.org/Role</tt> */
    public static final IRI ROLE;

    /** <tt>http://schema.org/RoofingContractor</tt> */
    public static final IRI ROOFINGCONTRACTOR;

    /** <tt>http://schema.org/Room</tt> */
    public static final IRI ROOM;

    /** <tt>http://schema.org/RsvpAction</tt> */
    public static final IRI RSVPACTION;

    /** <tt>http://schema.org/RsvpResponseType</tt> */
    public static final IRI RSVPRESPONSETYPE;

    /** <tt>http://schema.org/SaleEvent</tt> */
    public static final IRI SALEEVENT;

    /** <tt>http://schema.org/ScheduleAction</tt> */
    public static final IRI SCHEDULEACTION;

    /** <tt>http://schema.org/ScholarlyArticle</tt> */
    public static final IRI SCHOLARLYARTICLE;

    /** <tt>http://schema.org/School</tt> */
    public static final IRI SCHOOL;

    /** <tt>http://schema.org/ScreeningEvent</tt> */
    public static final IRI SCREENINGEVENT;

    /** <tt>http://schema.org/Sculpture</tt> */
    public static final IRI SCULPTURE;

    /** <tt>http://schema.org/SeaBodyOfWater</tt> */
    public static final IRI SEABODYOFWATER;

    /** <tt>http://schema.org/SearchAction</tt> */
    public static final IRI SEARCHACTION;

    /** <tt>http://schema.org/SearchResultsPage</tt> */
    public static final IRI SEARCHRESULTSPAGE;

    /** <tt>http://schema.org/Season</tt> */
    public static final IRI SEASON;

    /** <tt>http://schema.org/Seat</tt> */
    public static final IRI SEAT;

    /** <tt>http://schema.org/SelfStorage</tt> */
    public static final IRI SELFSTORAGE;

    /** <tt>http://schema.org/SellAction</tt> */
    public static final IRI SELLACTION;

    /** <tt>http://schema.org/SendAction</tt> */
    public static final IRI SENDACTION;

    /** <tt>http://schema.org/Series</tt> */
    public static final IRI SERIES;

    /** <tt>http://schema.org/Service</tt> */
    public static final IRI SERVICE;

    /** <tt>http://schema.org/ServiceChannel</tt> */
    public static final IRI SERVICECHANNEL;

    /** <tt>http://schema.org/ShareAction</tt> */
    public static final IRI SHAREACTION;

    /** <tt>http://schema.org/ShoeStore</tt> */
    public static final IRI SHOESTORE;

    /** <tt>http://schema.org/ShoppingCenter</tt> */
    public static final IRI SHOPPINGCENTER;

    /** <tt>http://schema.org/SingleFamilyResidence</tt> */
    public static final IRI SINGLEFAMILYRESIDENCE;

    /** <tt>http://schema.org/SiteNavigationElement</tt> */
    public static final IRI SITENAVIGATIONELEMENT;

    /** <tt>http://schema.org/SkiResort</tt> */
    public static final IRI SKIRESORT;

    /** <tt>http://schema.org/SocialEvent</tt> */
    public static final IRI SOCIALEVENT;

    /** <tt>http://schema.org/SocialMediaPosting</tt> */
    public static final IRI SOCIALMEDIAPOSTING;

    /** <tt>http://schema.org/SoftwareApplication</tt> */
    public static final IRI SOFTWAREAPPLICATION;

    /** <tt>http://schema.org/SoftwareSourceCode</tt> */
    public static final IRI SOFTWARESOURCECODE;

    /** <tt>http://schema.org/SomeProducts</tt> */
    public static final IRI SOMEPRODUCTS;

    /** <tt>http://schema.org/Specialty</tt> */
    public static final IRI SPECIALTY;

    /** <tt>http://schema.org/SportingGoodsStore</tt> */
    public static final IRI SPORTINGGOODSSTORE;

    /** <tt>http://schema.org/SportsActivityLocation</tt> */
    public static final IRI SPORTSACTIVITYLOCATION;

    /** <tt>http://schema.org/SportsClub</tt> */
    public static final IRI SPORTSCLUB;

    /** <tt>http://schema.org/SportsEvent</tt> */
    public static final IRI SPORTSEVENT;

    /** <tt>http://schema.org/SportsOrganization</tt> */
    public static final IRI SPORTSORGANIZATION;

    /** <tt>http://schema.org/SportsTeam</tt> */
    public static final IRI SPORTSTEAM;

    /** <tt>http://schema.org/SpreadsheetDigitalDocument</tt> */
    public static final IRI SPREADSHEETDIGITALDOCUMENT;

    /** <tt>http://schema.org/StadiumOrArena</tt> */
    public static final IRI STADIUMORARENA;

    /** <tt>http://schema.org/State</tt> */
    public static final IRI STATE;

    /** <tt>http://schema.org/SteeringPositionValue</tt> */
    public static final IRI STEERINGPOSITIONVALUE;

    /** <tt>http://schema.org/Store</tt> */
    public static final IRI STORE;

    /** <tt>http://schema.org/StructuredValue</tt> */
    public static final IRI STRUCTUREDVALUE;

    /** <tt>http://schema.org/SubscribeAction</tt> */
    public static final IRI SUBSCRIBEACTION;

    /** <tt>http://schema.org/SubwayStation</tt> */
    public static final IRI SUBWAYSTATION;

    /** <tt>http://schema.org/Suite</tt> */
    public static final IRI SUITE;

    /** <tt>http://schema.org/SuspendAction</tt> */
    public static final IRI SUSPENDACTION;

    /** <tt>http://schema.org/Synagogue</tt> */
    public static final IRI SYNAGOGUE;

    /** <tt>http://schema.org/TVClip</tt> */
    public static final IRI TVCLIP;

    /** <tt>http://schema.org/TVEpisode</tt> */
    public static final IRI TVEPISODE;

    /** <tt>http://schema.org/TVSeason</tt> */
    public static final IRI TVSEASON;

    /** <tt>http://schema.org/TVSeries</tt> */
    public static final IRI TVSERIES;

    /** <tt>http://schema.org/Table</tt> */
    public static final IRI TABLE;

    /** <tt>http://schema.org/TakeAction</tt> */
    public static final IRI TAKEACTION;

    /** <tt>http://schema.org/TattooParlor</tt> */
    public static final IRI TATTOOPARLOR;

    /** <tt>http://schema.org/Taxi</tt> */
    public static final IRI TAXI;

    /** <tt>http://schema.org/TaxiReservation</tt> */
    public static final IRI TAXIRESERVATION;

    /** <tt>http://schema.org/TaxiService</tt> */
    public static final IRI TAXISERVICE;

    /** <tt>http://schema.org/TaxiStand</tt> */
    public static final IRI TAXISTAND;

    /** <tt>http://schema.org/TechArticle</tt> */
    public static final IRI TECHARTICLE;

    /** <tt>http://schema.org/TelevisionChannel</tt> */
    public static final IRI TELEVISIONCHANNEL;

    /** <tt>http://schema.org/TelevisionStation</tt> */
    public static final IRI TELEVISIONSTATION;

    /** <tt>http://schema.org/TennisComplex</tt> */
    public static final IRI TENNISCOMPLEX;

    /** <tt>http://schema.org/Text</tt> */
    public static final IRI TEXT;

    /** <tt>http://schema.org/TextDigitalDocument</tt> */
    public static final IRI TEXTDIGITALDOCUMENT;

    /** <tt>http://schema.org/TheaterEvent</tt> */
    public static final IRI THEATEREVENT;

    /** <tt>http://schema.org/TheaterGroup</tt> */
    public static final IRI THEATERGROUP;

    /** <tt>http://schema.org/Thing</tt> */
    public static final IRI THING;

    /** <tt>http://schema.org/Ticket</tt> */
    public static final IRI TICKET;

    /** <tt>http://schema.org/TieAction</tt> */
    public static final IRI TIEACTION;

    /** <tt>http://schema.org/Time</tt> */
    public static final IRI TIME;

    /** <tt>http://schema.org/TipAction</tt> */
    public static final IRI TIPACTION;

    /** <tt>http://schema.org/TireShop</tt> */
    public static final IRI TIRESHOP;

    /** <tt>http://schema.org/TouristAttraction</tt> */
    public static final IRI TOURISTATTRACTION;

    /** <tt>http://schema.org/TouristInformationCenter</tt> */
    public static final IRI TOURISTINFORMATIONCENTER;

    /** <tt>http://schema.org/ToyStore</tt> */
    public static final IRI TOYSTORE;

    /** <tt>http://schema.org/TrackAction</tt> */
    public static final IRI TRACKACTION;

    /** <tt>http://schema.org/TradeAction</tt> */
    public static final IRI TRADEACTION;

    /** <tt>http://schema.org/TrainReservation</tt> */
    public static final IRI TRAINRESERVATION;

    /** <tt>http://schema.org/TrainStation</tt> */
    public static final IRI TRAINSTATION;

    /** <tt>http://schema.org/TrainTrip</tt> */
    public static final IRI TRAINTRIP;

    /** <tt>http://schema.org/TransferAction</tt> */
    public static final IRI TRANSFERACTION;

    /** <tt>http://schema.org/TravelAction</tt> */
    public static final IRI TRAVELACTION;

    /** <tt>http://schema.org/TravelAgency</tt> */
    public static final IRI TRAVELAGENCY;

    /** <tt>http://schema.org/Trip</tt> */
    public static final IRI TRIP;

    /** <tt>http://schema.org/TypeAndQuantityNode</tt> */
    public static final IRI TYPEANDQUANTITYNODE;

    /** <tt>http://schema.org/URL</tt> */
    public static final IRI URL;

    /** <tt>http://schema.org/UnRegisterAction</tt> */
    public static final IRI UNREGISTERACTION;

    /** <tt>http://schema.org/UnitPriceSpecification</tt> */
    public static final IRI UNITPRICESPECIFICATION;

    /** <tt>http://schema.org/UpdateAction</tt> */
    public static final IRI UPDATEACTION;

    /** <tt>http://schema.org/UseAction</tt> */
    public static final IRI USEACTION;

    /** <tt>http://schema.org/UserBlocks</tt> */
    public static final IRI USERBLOCKS;

    /** <tt>http://schema.org/UserCheckins</tt> */
    public static final IRI USERCHECKINS;

    /** <tt>http://schema.org/UserComments</tt> */
    public static final IRI USERCOMMENTS;

    /** <tt>http://schema.org/UserDownloads</tt> */
    public static final IRI USERDOWNLOADS;

    /** <tt>http://schema.org/UserInteraction</tt> */
    public static final IRI USERINTERACTION;

    /** <tt>http://schema.org/UserLikes</tt> */
    public static final IRI USERLIKES;

    /** <tt>http://schema.org/UserPageVisits</tt> */
    public static final IRI USERPAGEVISITS;

    /** <tt>http://schema.org/UserPlays</tt> */
    public static final IRI USERPLAYS;

    /** <tt>http://schema.org/UserPlusOnes</tt> */
    public static final IRI USERPLUSONES;

    /** <tt>http://schema.org/UserTweets</tt> */
    public static final IRI USERTWEETS;

    /** <tt>http://schema.org/Vehicle</tt> */
    public static final IRI VEHICLE;

    /** <tt>http://schema.org/VideoGallery</tt> */
    public static final IRI VIDEOGALLERY;

    /** <tt>http://schema.org/VideoGame</tt> */
    public static final IRI VIDEOGAME;

    /** <tt>http://schema.org/VideoGameClip</tt> */
    public static final IRI VIDEOGAMECLIP;

    /** <tt>http://schema.org/VideoGameSeries</tt> */
    public static final IRI VIDEOGAMESERIES;

    /** <tt>http://schema.org/VideoObject</tt> */
    public static final IRI VIDEOOBJECT;

    /** <tt>http://schema.org/ViewAction</tt> */
    public static final IRI VIEWACTION;

    /** <tt>http://schema.org/VisualArtsEvent</tt> */
    public static final IRI VISUALARTSEVENT;

    /** <tt>http://schema.org/VisualArtwork</tt> */
    public static final IRI VISUALARTWORK;

    /** <tt>http://schema.org/Volcano</tt> */
    public static final IRI VOLCANO;

    /** <tt>http://schema.org/VoteAction</tt> */
    public static final IRI VOTEACTION;

    /** <tt>http://schema.org/WPAdBlock</tt> */
    public static final IRI WPADBLOCK;

    /** <tt>http://schema.org/WPFooter</tt> */
    public static final IRI WPFOOTER;

    /** <tt>http://schema.org/WPHeader</tt> */
    public static final IRI WPHEADER;

    /** <tt>http://schema.org/WPSideBar</tt> */
    public static final IRI WPSIDEBAR;

    /** <tt>http://schema.org/WantAction</tt> */
    public static final IRI WANTACTION;

    /** <tt>http://schema.org/WarrantyPromise</tt> */
    public static final IRI WARRANTYPROMISE;

    /** <tt>http://schema.org/WarrantyScope</tt> */
    public static final IRI WARRANTYSCOPE;

    /** <tt>http://schema.org/WatchAction</tt> */
    public static final IRI WATCHACTION;

    /** <tt>http://schema.org/Waterfall</tt> */
    public static final IRI WATERFALL;

    /** <tt>http://schema.org/WearAction</tt> */
    public static final IRI WEARACTION;

    /** <tt>http://schema.org/WebApplication</tt> */
    public static final IRI WEBAPPLICATION;

    /** <tt>http://schema.org/WebPage</tt> */
    public static final IRI WEBPAGE;

    /** <tt>http://schema.org/WebPageElement</tt> */
    public static final IRI WEBPAGEELEMENT;

    /** <tt>http://schema.org/WebSite</tt> */
    public static final IRI WEBSITE;

    /** <tt>http://schema.org/WholesaleStore</tt> */
    public static final IRI WHOLESALESTORE;

    /** <tt>http://schema.org/WinAction</tt> */
    public static final IRI WINACTION;

    /** <tt>http://schema.org/Winery</tt> */
    public static final IRI WINERY;

    /** <tt>http://schema.org/WriteAction</tt> */
    public static final IRI WRITEACTION;

    /** <tt>http://schema.org/Zoo</tt> */
    public static final IRI ZOO;

    /** <tt>http://schema.org/acceptedAnswer</tt> */
    public static final IRI ACCEPTEDANSWER;

    /** <tt>http://schema.org/acceptedOffer</tt> */
    public static final IRI ACCEPTEDOFFER;

    /** <tt>http://schema.org/acceptedPaymentMethod</tt> */
    public static final IRI ACCEPTEDPAYMENTMETHOD;

    /** <tt>http://schema.org/acceptsReservations</tt> */
    public static final IRI ACCEPTSRESERVATIONS;

    /** <tt>http://schema.org/accessCode</tt> */
    public static final IRI ACCESSCODE;

    /** <tt>http://schema.org/accessMode</tt> */
    public static final IRI ACCESSMODE;

    /** <tt>http://schema.org/accessModeSufficient</tt> */
    public static final IRI ACCESSMODESUFFICIENT;

    /** <tt>http://schema.org/accessibilityAPI</tt> */
    public static final IRI ACCESSIBILITYAPI;

    /** <tt>http://schema.org/accessibilityControl</tt> */
    public static final IRI ACCESSIBILITYCONTROL;

    /** <tt>http://schema.org/accessibilityFeature</tt> */
    public static final IRI ACCESSIBILITYFEATURE;

    /** <tt>http://schema.org/accessibilityHazard</tt> */
    public static final IRI ACCESSIBILITYHAZARD;

    /** <tt>http://schema.org/accessibilitySummary</tt> */
    public static final IRI ACCESSIBILITYSUMMARY;

    /** <tt>http://schema.org/accountId</tt> */
    public static final IRI ACCOUNTID;

    /** <tt>http://schema.org/accountablePerson</tt> */
    public static final IRI ACCOUNTABLEPERSON;

    /** <tt>http://schema.org/acquiredFrom</tt> */
    public static final IRI ACQUIREDFROM;

    /** <tt>http://schema.org/actionPlatform</tt> */
    public static final IRI ACTIONPLATFORM;

    /** <tt>http://schema.org/actionStatus</tt> */
    public static final IRI ACTIONSTATUS;

    /** <tt>http://schema.org/actors</tt> */
    public static final IRI ACTORS;

    /** <tt>http://schema.org/addOn</tt> */
    public static final IRI ADDON;

    /** <tt>http://schema.org/additionalName</tt> */
    public static final IRI ADDITIONALNAME;

    /** <tt>http://schema.org/additionalNumberOfGuests</tt> */
    public static final IRI ADDITIONALNUMBEROFGUESTS;

    /** <tt>http://schema.org/additionalProperty</tt> */
    public static final IRI ADDITIONALPROPERTY;

    /** <tt>http://schema.org/additionalType</tt> */
    public static final IRI ADDITIONALTYPE;

    /** <tt>http://schema.org/address</tt> */
    public static final IRI ADDRESS;

    /** <tt>http://schema.org/addressCountry</tt> */
    public static final IRI ADDRESSCOUNTRY;

    /** <tt>http://schema.org/addressLocality</tt> */
    public static final IRI ADDRESSLOCALITY;

    /** <tt>http://schema.org/addressRegion</tt> */
    public static final IRI ADDRESSREGION;

    /** <tt>http://schema.org/advanceBookingRequirement</tt> */
    public static final IRI ADVANCEBOOKINGREQUIREMENT;

    /** <tt>http://schema.org/affiliation</tt> */
    public static final IRI AFFILIATION;

    /** <tt>http://schema.org/afterMedia</tt> */
    public static final IRI AFTERMEDIA;

    /** <tt>http://schema.org/agent</tt> */
    public static final IRI AGENT;

    /** <tt>http://schema.org/aggregateRating</tt> */
    public static final IRI HAS_AGGREGATERATING;

    /** <tt>http://schema.org/aircraft</tt> */
    public static final IRI AIRCRAFT;

    /** <tt>http://schema.org/albumProductionType</tt> */
    public static final IRI ALBUMPRODUCTIONTYPE;

    /** <tt>http://schema.org/albumReleaseType</tt> */
    public static final IRI ALBUMRELEASETYPE;

    /** <tt>http://schema.org/albums</tt> */
    public static final IRI ALBUMS;

    /** <tt>http://schema.org/alignmentType</tt> */
    public static final IRI ALIGNMENTTYPE;

    /** <tt>http://schema.org/alternateName</tt> */
    public static final IRI ALTERNATENAME;

    /** <tt>http://schema.org/alternativeHeadline</tt> */
    public static final IRI ALTERNATIVEHEADLINE;

    /** <tt>http://schema.org/amenityFeature</tt> */
    public static final IRI AMENITYFEATURE;

    /** <tt>http://schema.org/amount</tt> */
    public static final IRI AMOUNT;

    /** <tt>http://schema.org/amountOfThisGood</tt> */
    public static final IRI AMOUNTOFTHISGOOD;

    /** <tt>http://schema.org/annualPercentageRate</tt> */
    public static final IRI ANNUALPERCENTAGERATE;

    /** <tt>http://schema.org/answerCount</tt> */
    public static final IRI ANSWERCOUNT;

    /** <tt>http://schema.org/application</tt> */
    public static final IRI APPLICATION;

    /** <tt>http://schema.org/applicationCategory</tt> */
    public static final IRI APPLICATIONCATEGORY;

    /** <tt>http://schema.org/applicationSubCategory</tt> */
    public static final IRI APPLICATIONSUBCATEGORY;

    /** <tt>http://schema.org/applicationSuite</tt> */
    public static final IRI APPLICATIONSUITE;

    /** <tt>http://schema.org/appliesToDeliveryMethod</tt> */
    public static final IRI APPLIESTODELIVERYMETHOD;

    /** <tt>http://schema.org/appliesToPaymentMethod</tt> */
    public static final IRI APPLIESTOPAYMENTMETHOD;

    /** <tt>http://schema.org/area</tt> */
    public static final IRI AREA;

    /** <tt>http://schema.org/arrivalAirport</tt> */
    public static final IRI ARRIVALAIRPORT;

    /** <tt>http://schema.org/arrivalBusStop</tt> */
    public static final IRI ARRIVALBUSSTOP;

    /** <tt>http://schema.org/arrivalGate</tt> */
    public static final IRI ARRIVALGATE;

    /** <tt>http://schema.org/arrivalPlatform</tt> */
    public static final IRI ARRIVALPLATFORM;

    /** <tt>http://schema.org/arrivalStation</tt> */
    public static final IRI ARRIVALSTATION;

    /** <tt>http://schema.org/arrivalTerminal</tt> */
    public static final IRI ARRIVALTERMINAL;

    /** <tt>http://schema.org/arrivalTime</tt> */
    public static final IRI ARRIVALTIME;

    /** <tt>http://schema.org/artEdition</tt> */
    public static final IRI ARTEDITION;

    /** <tt>http://schema.org/artMedium</tt> */
    public static final IRI ARTMEDIUM;

    /** <tt>http://schema.org/artform</tt> */
    public static final IRI ARTFORM;

    /** <tt>http://schema.org/articleBody</tt> */
    public static final IRI ARTICLEBODY;

    /** <tt>http://schema.org/articleSection</tt> */
    public static final IRI ARTICLESECTION;

    /** <tt>http://schema.org/assembly</tt> */
    public static final IRI ASSEMBLY;

    /** <tt>http://schema.org/assemblyVersion</tt> */
    public static final IRI ASSEMBLYVERSION;

    /** <tt>http://schema.org/associatedArticle</tt> */
    public static final IRI ASSOCIATEDARTICLE;

    /** <tt>http://schema.org/associatedMedia</tt> */
    public static final IRI ASSOCIATEDMEDIA;

    /** <tt>http://schema.org/athlete</tt> */
    public static final IRI ATHLETE;

    /** <tt>http://schema.org/attendees</tt> */
    public static final IRI ATTENDEES;

    /** <tt>http://schema.org/audienceType</tt> */
    public static final IRI AUDIENCETYPE;

    /** <tt>http://schema.org/audio</tt> */
    public static final IRI AUDIO;

    /** <tt>http://schema.org/author</tt> */
    public static final IRI AUTHOR;

    /** <tt>http://schema.org/availability</tt> */
    public static final IRI AVAILABILITY;

    /** <tt>http://schema.org/availabilityEnds</tt> */
    public static final IRI AVAILABILITYENDS;

    /** <tt>http://schema.org/availabilityStarts</tt> */
    public static final IRI AVAILABILITYSTARTS;

    /** <tt>http://schema.org/availableAtOrFrom</tt> */
    public static final IRI AVAILABLEATORFROM;

    /** <tt>http://schema.org/availableChannel</tt> */
    public static final IRI AVAILABLECHANNEL;

    /** <tt>http://schema.org/availableDeliveryMethod</tt> */
    public static final IRI AVAILABLEDELIVERYMETHOD;

    /** <tt>http://schema.org/availableFrom</tt> */
    public static final IRI AVAILABLEFROM;

    /** <tt>http://schema.org/availableLanguage</tt> */
    public static final IRI AVAILABLELANGUAGE;

    /** <tt>http://schema.org/availableThrough</tt> */
    public static final IRI AVAILABLETHROUGH;

    /** <tt>http://schema.org/awards</tt> */
    public static final IRI AWARDS;

    /** <tt>http://schema.org/awayTeam</tt> */
    public static final IRI AWAYTEAM;

    /** <tt>http://schema.org/baseSalary</tt> */
    public static final IRI BASESALARY;

    /** <tt>http://schema.org/bccRecipient</tt> */
    public static final IRI BCCRECIPIENT;

    /** <tt>http://schema.org/bed</tt> */
    public static final IRI BED;

    /** <tt>http://schema.org/beforeMedia</tt> */
    public static final IRI BEFOREMEDIA;

    /** <tt>http://schema.org/benefits</tt> */
    public static final IRI BENEFITS;

    /** <tt>http://schema.org/bestRating</tt> */
    public static final IRI BESTRATING;

    /** <tt>http://schema.org/billingAddress</tt> */
    public static final IRI BILLINGADDRESS;

    /** <tt>http://schema.org/billingIncrement</tt> */
    public static final IRI BILLINGINCREMENT;

    /** <tt>http://schema.org/billingPeriod</tt> */
    public static final IRI BILLINGPERIOD;

    /** <tt>http://schema.org/birthDate</tt> */
    public static final IRI BIRTHDATE;

    /** <tt>http://schema.org/birthPlace</tt> */
    public static final IRI BIRTHPLACE;

    /** <tt>http://schema.org/bitrate</tt> */
    public static final IRI BITRATE;

    /** <tt>http://schema.org/blogPosts</tt> */
    public static final IRI BLOGPOSTS;

    /** <tt>http://schema.org/boardingGroup</tt> */
    public static final IRI BOARDINGGROUP;

    /** <tt>http://schema.org/boardingPolicy</tt> */
    public static final IRI BOARDINGPOLICY;

    /** <tt>http://schema.org/bookEdition</tt> */
    public static final IRI BOOKEDITION;

    /** <tt>http://schema.org/bookFormat</tt> */
    public static final IRI BOOKFORMAT;

    /** <tt>http://schema.org/bookingAgent</tt> */
    public static final IRI BOOKINGAGENT;

    /** <tt>http://schema.org/bookingTime</tt> */
    public static final IRI BOOKINGTIME;

    /** <tt>http://schema.org/borrower</tt> */
    public static final IRI BORROWER;

    /** <tt>http://schema.org/box</tt> */
    public static final IRI BOX;

    /** <tt>http://schema.org/branchCode</tt> */
    public static final IRI BRANCHCODE;

    /** <tt>http://schema.org/branchOf</tt> */
    public static final IRI BRANCHOF;

    /** <tt>http://schema.org/brand</tt> */
    public static final IRI HAS_BRAND;

    /** <tt>http://schema.org/breadcrumb</tt> */
    public static final IRI BREADCRUMB;

    /** <tt>http://schema.org/broadcastAffiliateOf</tt> */
    public static final IRI BROADCASTAFFILIATEOF;

    /** <tt>http://schema.org/broadcastChannelId</tt> */
    public static final IRI BROADCASTCHANNELID;

    /** <tt>http://schema.org/broadcastDisplayName</tt> */
    public static final IRI BROADCASTDISPLAYNAME;

    /** <tt>http://schema.org/broadcastOfEvent</tt> */
    public static final IRI BROADCASTOFEVENT;

    /** <tt>http://schema.org/broadcastServiceTier</tt> */
    public static final IRI BROADCASTSERVICETIER;

    /** <tt>http://schema.org/broadcastTimezone</tt> */
    public static final IRI BROADCASTTIMEZONE;

    /** <tt>http://schema.org/broadcaster</tt> */
    public static final IRI BROADCASTER;

    /** <tt>http://schema.org/browserRequirements</tt> */
    public static final IRI BROWSERREQUIREMENTS;

    /** <tt>http://schema.org/busName</tt> */
    public static final IRI BUSNAME;

    /** <tt>http://schema.org/busNumber</tt> */
    public static final IRI BUSNUMBER;

    /** <tt>http://schema.org/businessFunction</tt> */
    public static final IRI HAS_BUSINESSFUNCTION;

    /** <tt>http://schema.org/buyer</tt> */
    public static final IRI BUYER;

    /** <tt>http://schema.org/byArtist</tt> */
    public static final IRI BYARTIST;

    /** <tt>http://schema.org/calories</tt> */
    public static final IRI CALORIES;

    /** <tt>http://schema.org/candidate</tt> */
    public static final IRI CANDIDATE;

    /** <tt>http://schema.org/caption</tt> */
    public static final IRI CAPTION;

    /** <tt>http://schema.org/carbohydrateContent</tt> */
    public static final IRI CARBOHYDRATECONTENT;

    /** <tt>http://schema.org/cargoVolume</tt> */
    public static final IRI CARGOVOLUME;

    /** <tt>http://schema.org/carrier</tt> */
    public static final IRI CARRIER;

    /** <tt>http://schema.org/carrierRequirements</tt> */
    public static final IRI CARRIERREQUIREMENTS;

    /** <tt>http://schema.org/catalog</tt> */
    public static final IRI CATALOG;

    /** <tt>http://schema.org/catalogNumber</tt> */
    public static final IRI CATALOGNUMBER;

    /** <tt>http://schema.org/category</tt> */
    public static final IRI CATEGORY;

    /** <tt>http://schema.org/ccRecipient</tt> */
    public static final IRI CCRECIPIENT;

    /** <tt>http://schema.org/character</tt> */
    public static final IRI CHARACTER;

    /** <tt>http://schema.org/characterAttribute</tt> */
    public static final IRI CHARACTERATTRIBUTE;

    /** <tt>http://schema.org/characterName</tt> */
    public static final IRI CHARACTERNAME;

    /** <tt>http://schema.org/cheatCode</tt> */
    public static final IRI CHEATCODE;

    /** <tt>http://schema.org/checkinTime</tt> */
    public static final IRI CHECKINTIME;

    /** <tt>http://schema.org/checkoutTime</tt> */
    public static final IRI CHECKOUTTIME;

    /** <tt>http://schema.org/childMaxAge</tt> */
    public static final IRI CHILDMAXAGE;

    /** <tt>http://schema.org/childMinAge</tt> */
    public static final IRI CHILDMINAGE;

    /** <tt>http://schema.org/children</tt> */
    public static final IRI CHILDREN;

    /** <tt>http://schema.org/cholesterolContent</tt> */
    public static final IRI CHOLESTEROLCONTENT;

    /** <tt>http://schema.org/circle</tt> */
    public static final IRI CIRCLE;

    /** <tt>http://schema.org/citation</tt> */
    public static final IRI CITATION;

    /** <tt>http://schema.org/claimReviewed</tt> */
    public static final IRI CLAIMREVIEWED;

    /** <tt>http://schema.org/clipNumber</tt> */
    public static final IRI CLIPNUMBER;

    /** <tt>http://schema.org/closes</tt> */
    public static final IRI CLOSES;

    /** <tt>http://schema.org/coach</tt> */
    public static final IRI COACH;

    /** <tt>http://schema.org/codeRepository</tt> */
    public static final IRI CODEREPOSITORY;

    /** <tt>http://schema.org/colleagues</tt> */
    public static final IRI COLLEAGUES;

    /** <tt>http://schema.org/collection</tt> */
    public static final IRI COLLECTION;

    /** <tt>http://schema.org/color</tt> */
    public static final IRI COLOR;

    /** <tt>http://schema.org/comment</tt> */
    public static final IRI HAS_COMMENT;

    /** <tt>http://schema.org/commentCount</tt> */
    public static final IRI COMMENTCOUNT;

    /** <tt>http://schema.org/commentText</tt> */
    public static final IRI COMMENTTEXT;

    /** <tt>http://schema.org/commentTime</tt> */
    public static final IRI COMMENTTIME;

    /** <tt>http://schema.org/composer</tt> */
    public static final IRI COMPOSER;

    /** <tt>http://schema.org/confirmationNumber</tt> */
    public static final IRI CONFIRMATIONNUMBER;

    /** <tt>http://schema.org/contactOption</tt> */
    public static final IRI CONTACTOPTION;

    /** <tt>http://schema.org/contactPoints</tt> */
    public static final IRI CONTACTPOINTS;

    /** <tt>http://schema.org/contactType</tt> */
    public static final IRI CONTACTTYPE;

    /** <tt>http://schema.org/containedIn</tt> */
    public static final IRI CONTAINEDIN;

    /** <tt>http://schema.org/contentRating</tt> */
    public static final IRI CONTENTRATING;

    /** <tt>http://schema.org/contentSize</tt> */
    public static final IRI CONTENTSIZE;

    /** <tt>http://schema.org/contentType</tt> */
    public static final IRI CONTENTTYPE;

    /** <tt>http://schema.org/contentUrl</tt> */
    public static final IRI CONTENTURL;

    /** <tt>http://schema.org/contributor</tt> */
    public static final IRI CONTRIBUTOR;

    /** <tt>http://schema.org/cookTime</tt> */
    public static final IRI COOKTIME;

    /** <tt>http://schema.org/cookingMethod</tt> */
    public static final IRI COOKINGMETHOD;

    /** <tt>http://schema.org/copyrightHolder</tt> */
    public static final IRI COPYRIGHTHOLDER;

    /** <tt>http://schema.org/copyrightYear</tt> */
    public static final IRI COPYRIGHTYEAR;

    /** <tt>http://schema.org/countriesNotSupported</tt> */
    public static final IRI COUNTRIESNOTSUPPORTED;

    /** <tt>http://schema.org/countriesSupported</tt> */
    public static final IRI COUNTRIESSUPPORTED;

    /** <tt>http://schema.org/countryOfOrigin</tt> */
    public static final IRI COUNTRYOFORIGIN;

    /** <tt>http://schema.org/course</tt> */
    public static final IRI HAS_COURSE;

    /** <tt>http://schema.org/courseCode</tt> */
    public static final IRI COURSECODE;

    /** <tt>http://schema.org/courseMode</tt> */
    public static final IRI COURSEMODE;

    /** <tt>http://schema.org/coursePrerequisites</tt> */
    public static final IRI COURSEPREREQUISITES;

    /** <tt>http://schema.org/coverageEndTime</tt> */
    public static final IRI COVERAGEENDTIME;

    /** <tt>http://schema.org/coverageStartTime</tt> */
    public static final IRI COVERAGESTARTTIME;

    /** <tt>http://schema.org/creator</tt> */
    public static final IRI CREATOR;

    /** <tt>http://schema.org/creditedTo</tt> */
    public static final IRI CREDITEDTO;

    /** <tt>http://schema.org/currenciesAccepted</tt> */
    public static final IRI CURRENCIESACCEPTED;

    /** <tt>http://schema.org/currency</tt> */
    public static final IRI CURRENCY;

    /** <tt>http://schema.org/customer</tt> */
    public static final IRI CUSTOMER;

    /** <tt>http://schema.org/dataFeedElement</tt> */
    public static final IRI DATAFEEDELEMENT;

    /** <tt>http://schema.org/datasetTimeInterval</tt> */
    public static final IRI DATASETTIMEINTERVAL;

    /** <tt>http://schema.org/dateCreated</tt> */
    public static final IRI DATECREATED;

    /** <tt>http://schema.org/dateDeleted</tt> */
    public static final IRI DATEDELETED;

    /** <tt>http://schema.org/dateIssued</tt> */
    public static final IRI DATEISSUED;

    /** <tt>http://schema.org/dateModified</tt> */
    public static final IRI DATEMODIFIED;

    /** <tt>http://schema.org/datePosted</tt> */
    public static final IRI DATEPOSTED;

    /** <tt>http://schema.org/datePublished</tt> */
    public static final IRI DATEPUBLISHED;

    /** <tt>http://schema.org/dateRead</tt> */
    public static final IRI DATEREAD;

    /** <tt>http://schema.org/dateReceived</tt> */
    public static final IRI DATERECEIVED;

    /** <tt>http://schema.org/dateSent</tt> */
    public static final IRI DATESENT;

    /** <tt>http://schema.org/dateVehicleFirstRegistered</tt> */
    public static final IRI DATEVEHICLEFIRSTREGISTERED;

    /** <tt>http://schema.org/dateline</tt> */
    public static final IRI DATELINE;

    /** <tt>http://schema.org/dayOfWeek</tt> */
    public static final IRI HAS_DAYOFWEEK;

    /** <tt>http://schema.org/deathDate</tt> */
    public static final IRI DEATHDATE;

    /** <tt>http://schema.org/deathPlace</tt> */
    public static final IRI DEATHPLACE;

    /** <tt>http://schema.org/defaultValue</tt> */
    public static final IRI DEFAULTVALUE;

    /** <tt>http://schema.org/deliveryAddress</tt> */
    public static final IRI DELIVERYADDRESS;

    /** <tt>http://schema.org/deliveryLeadTime</tt> */
    public static final IRI DELIVERYLEADTIME;

    /** <tt>http://schema.org/deliveryMethod</tt> */
    public static final IRI HAS_DELIVERYMETHOD;

    /** <tt>http://schema.org/deliveryStatus</tt> */
    public static final IRI DELIVERYSTATUS;

    /** <tt>http://schema.org/department</tt> */
    public static final IRI DEPARTMENT;

    /** <tt>http://schema.org/departureAirport</tt> */
    public static final IRI DEPARTUREAIRPORT;

    /** <tt>http://schema.org/departureBusStop</tt> */
    public static final IRI DEPARTUREBUSSTOP;

    /** <tt>http://schema.org/departureGate</tt> */
    public static final IRI DEPARTUREGATE;

    /** <tt>http://schema.org/departurePlatform</tt> */
    public static final IRI DEPARTUREPLATFORM;

    /** <tt>http://schema.org/departureStation</tt> */
    public static final IRI DEPARTURESTATION;

    /** <tt>http://schema.org/departureTerminal</tt> */
    public static final IRI DEPARTURETERMINAL;

    /** <tt>http://schema.org/departureTime</tt> */
    public static final IRI DEPARTURETIME;

    /** <tt>http://schema.org/dependencies</tt> */
    public static final IRI DEPENDENCIES;

    /** <tt>http://schema.org/depth</tt> */
    public static final IRI DEPTH;

    /** <tt>http://schema.org/device</tt> */
    public static final IRI DEVICE;

    /** <tt>http://schema.org/directors</tt> */
    public static final IRI DIRECTORS;

    /** <tt>http://schema.org/disambiguatingDescription</tt> */
    public static final IRI DISAMBIGUATINGDESCRIPTION;

    /** <tt>http://schema.org/discount</tt> */
    public static final IRI DISCOUNT;

    /** <tt>http://schema.org/discountCode</tt> */
    public static final IRI DISCOUNTCODE;

    /** <tt>http://schema.org/discountCurrency</tt> */
    public static final IRI DISCOUNTCURRENCY;

    /** <tt>http://schema.org/discusses</tt> */
    public static final IRI DISCUSSES;

    /** <tt>http://schema.org/discussionUrl</tt> */
    public static final IRI DISCUSSIONURL;

    /** <tt>http://schema.org/dissolutionDate</tt> */
    public static final IRI DISSOLUTIONDATE;

    /** <tt>http://schema.org/distance</tt> */
    public static final IRI HAS_DISTANCE;

    /** <tt>http://schema.org/distribution</tt> */
    public static final IRI DISTRIBUTION;

    /** <tt>http://schema.org/doorTime</tt> */
    public static final IRI DOORTIME;

    /** <tt>http://schema.org/downloadUrl</tt> */
    public static final IRI DOWNLOADURL;

    /** <tt>http://schema.org/downvoteCount</tt> */
    public static final IRI DOWNVOTECOUNT;

    /** <tt>http://schema.org/driveWheelConfiguration</tt> */
    public static final IRI DRIVEWHEELCONFIGURATION;

    /** <tt>http://schema.org/dropoffLocation</tt> */
    public static final IRI DROPOFFLOCATION;

    /** <tt>http://schema.org/dropoffTime</tt> */
    public static final IRI DROPOFFTIME;

    /** <tt>http://schema.org/duns</tt> */
    public static final IRI DUNS;

    /** <tt>http://schema.org/durationOfWarranty</tt> */
    public static final IRI DURATIONOFWARRANTY;

    /** <tt>http://schema.org/duringMedia</tt> */
    public static final IRI DURINGMEDIA;

    /** <tt>http://schema.org/editor</tt> */
    public static final IRI EDITOR;

    /** <tt>http://schema.org/educationRequirements</tt> */
    public static final IRI EDUCATIONREQUIREMENTS;

    /** <tt>http://schema.org/educationalAlignment</tt> */
    public static final IRI EDUCATIONALALIGNMENT;

    /** <tt>http://schema.org/educationalFramework</tt> */
    public static final IRI EDUCATIONALFRAMEWORK;

    /** <tt>http://schema.org/educationalRole</tt> */
    public static final IRI EDUCATIONALROLE;

    /** <tt>http://schema.org/educationalUse</tt> */
    public static final IRI EDUCATIONALUSE;

    /** <tt>http://schema.org/elevation</tt> */
    public static final IRI ELEVATION;

    /** <tt>http://schema.org/eligibleCustomerType</tt> */
    public static final IRI ELIGIBLECUSTOMERTYPE;

    /** <tt>http://schema.org/eligibleDuration</tt> */
    public static final IRI ELIGIBLEDURATION;

    /** <tt>http://schema.org/eligibleQuantity</tt> */
    public static final IRI ELIGIBLEQUANTITY;

    /** <tt>http://schema.org/eligibleRegion</tt> */
    public static final IRI ELIGIBLEREGION;

    /** <tt>http://schema.org/eligibleTransactionVolume</tt> */
    public static final IRI ELIGIBLETRANSACTIONVOLUME;

    /** <tt>http://schema.org/email</tt> */
    public static final IRI EMAIL;

    /** <tt>http://schema.org/embedUrl</tt> */
    public static final IRI EMBEDURL;

    /** <tt>http://schema.org/employees</tt> */
    public static final IRI EMPLOYEES;

    /** <tt>http://schema.org/employmentType</tt> */
    public static final IRI EMPLOYMENTTYPE;

    /** <tt>http://schema.org/encodesCreativeWork</tt> */
    public static final IRI ENCODESCREATIVEWORK;

    /** <tt>http://schema.org/encodingType</tt> */
    public static final IRI ENCODINGTYPE;

    /** <tt>http://schema.org/encodings</tt> */
    public static final IRI ENCODINGS;

    /** <tt>http://schema.org/endDate</tt> */
    public static final IRI ENDDATE;

    /** <tt>http://schema.org/endTime</tt> */
    public static final IRI ENDTIME;

    /** <tt>http://schema.org/endorsee</tt> */
    public static final IRI ENDORSEE;

    /** <tt>http://schema.org/entertainmentBusiness</tt> */
    public static final IRI HAS_ENTERTAINMENTBUSINESS;

    /** <tt>http://schema.org/episodeNumber</tt> */
    public static final IRI EPISODENUMBER;

    /** <tt>http://schema.org/episodes</tt> */
    public static final IRI EPISODES;

    /** <tt>http://schema.org/equal</tt> */
    public static final IRI EQUAL;

    /** <tt>http://schema.org/error</tt> */
    public static final IRI ERROR;

    /** <tt>http://schema.org/estimatedCost</tt> */
    public static final IRI ESTIMATEDCOST;

    /** <tt>http://schema.org/estimatedFlightDuration</tt> */
    public static final IRI ESTIMATEDFLIGHTDURATION;

    /** <tt>http://schema.org/eventStatus</tt> */
    public static final IRI EVENTSTATUS;

    /** <tt>http://schema.org/events</tt> */
    public static final IRI EVENTS;

    /** <tt>http://schema.org/exifData</tt> */
    public static final IRI EXIFDATA;

    /** <tt>http://schema.org/expectedArrivalFrom</tt> */
    public static final IRI EXPECTEDARRIVALFROM;

    /** <tt>http://schema.org/expectedArrivalUntil</tt> */
    public static final IRI EXPECTEDARRIVALUNTIL;

    /** <tt>http://schema.org/expectsAcceptanceOf</tt> */
    public static final IRI EXPECTSACCEPTANCEOF;

    /** <tt>http://schema.org/experienceRequirements</tt> */
    public static final IRI EXPERIENCEREQUIREMENTS;

    /** <tt>http://schema.org/expires</tt> */
    public static final IRI EXPIRES;

    /** <tt>http://schema.org/familyName</tt> */
    public static final IRI FAMILYNAME;

    /** <tt>http://schema.org/fatContent</tt> */
    public static final IRI FATCONTENT;

    /** <tt>http://schema.org/faxNumber</tt> */
    public static final IRI FAXNUMBER;

    /** <tt>http://schema.org/featureList</tt> */
    public static final IRI FEATURELIST;

    /** <tt>http://schema.org/feesAndCommissionsSpecification</tt> */
    public static final IRI FEESANDCOMMISSIONSSPECIFICATION;

    /** <tt>http://schema.org/fiberContent</tt> */
    public static final IRI FIBERCONTENT;

    /** <tt>http://schema.org/fileFormat</tt> */
    public static final IRI FILEFORMAT;

    /** <tt>http://schema.org/fileSize</tt> */
    public static final IRI FILESIZE;

    /** <tt>http://schema.org/firstPerformance</tt> */
    public static final IRI FIRSTPERFORMANCE;

    /** <tt>http://schema.org/flightDistance</tt> */
    public static final IRI FLIGHTDISTANCE;

    /** <tt>http://schema.org/flightNumber</tt> */
    public static final IRI FLIGHTNUMBER;

    /** <tt>http://schema.org/floorSize</tt> */
    public static final IRI FLOORSIZE;

    /** <tt>http://schema.org/followee</tt> */
    public static final IRI FOLLOWEE;

    /** <tt>http://schema.org/follows</tt> */
    public static final IRI FOLLOWS;

    /** <tt>http://schema.org/foodEstablishment</tt> */
    public static final IRI HAS_FOODESTABLISHMENT;

    /** <tt>http://schema.org/foodEvent</tt> */
    public static final IRI HAS_FOODEVENT;

    /** <tt>http://schema.org/founders</tt> */
    public static final IRI FOUNDERS;

    /** <tt>http://schema.org/foundingDate</tt> */
    public static final IRI FOUNDINGDATE;

    /** <tt>http://schema.org/foundingLocation</tt> */
    public static final IRI FOUNDINGLOCATION;

    /** <tt>http://schema.org/free</tt> */
    public static final IRI FREE;

    /** <tt>http://schema.org/fromLocation</tt> */
    public static final IRI FROMLOCATION;

    /** <tt>http://schema.org/fuelConsumption</tt> */
    public static final IRI FUELCONSUMPTION;

    /** <tt>http://schema.org/fuelEfficiency</tt> */
    public static final IRI FUELEFFICIENCY;

    /** <tt>http://schema.org/fuelType</tt> */
    public static final IRI FUELTYPE;

    /** <tt>http://schema.org/funder</tt> */
    public static final IRI FUNDER;

    /** <tt>http://schema.org/gameItem</tt> */
    public static final IRI GAMEITEM;

    /** <tt>http://schema.org/gameLocation</tt> */
    public static final IRI GAMELOCATION;

    /** <tt>http://schema.org/gamePlatform</tt> */
    public static final IRI GAMEPLATFORM;

    /** <tt>http://schema.org/gameTip</tt> */
    public static final IRI GAMETIP;

    /** <tt>http://schema.org/gender</tt> */
    public static final IRI GENDER;

    /** <tt>http://schema.org/genre</tt> */
    public static final IRI GENRE;

    /** <tt>http://schema.org/geo</tt> */
    public static final IRI GEO;

    /** <tt>http://schema.org/geoMidpoint</tt> */
    public static final IRI GEOMIDPOINT;

    /** <tt>http://schema.org/geoRadius</tt> */
    public static final IRI GEORADIUS;

    /** <tt>http://schema.org/geographicArea</tt> */
    public static final IRI GEOGRAPHICAREA;

    /** <tt>http://schema.org/givenName</tt> */
    public static final IRI GIVENNAME;

    /** <tt>http://schema.org/globalLocationNumber</tt> */
    public static final IRI GLOBALLOCATIONNUMBER;

    /** <tt>http://schema.org/grantee</tt> */
    public static final IRI GRANTEE;

    /** <tt>http://schema.org/greater</tt> */
    public static final IRI GREATER;

    /** <tt>http://schema.org/greaterOrEqual</tt> */
    public static final IRI GREATEROREQUAL;

    /** <tt>http://schema.org/gtin12</tt> */
    public static final IRI GTIN12;

    /** <tt>http://schema.org/gtin13</tt> */
    public static final IRI GTIN13;

    /** <tt>http://schema.org/gtin14</tt> */
    public static final IRI GTIN14;

    /** <tt>http://schema.org/gtin8</tt> */
    public static final IRI GTIN8;

    /** <tt>http://schema.org/hasCourseInstance</tt> */
    public static final IRI HASCOURSEINSTANCE;

    /** <tt>http://schema.org/hasDeliveryMethod</tt> */
    public static final IRI HASDELIVERYMETHOD;

    /** <tt>http://schema.org/hasDigitalDocumentPermission</tt> */
    public static final IRI HASDIGITALDOCUMENTPERMISSION;

    /** <tt>http://schema.org/hasMenuItem</tt> */
    public static final IRI HASMENUITEM;

    /** <tt>http://schema.org/hasMenuSection</tt> */
    public static final IRI HASMENUSECTION;

    /** <tt>http://schema.org/hasOfferCatalog</tt> */
    public static final IRI HASOFFERCATALOG;

    /** <tt>http://schema.org/hasPOS</tt> */
    public static final IRI HASPOS;

    /** <tt>http://schema.org/headline</tt> */
    public static final IRI HEADLINE;

    /** <tt>http://schema.org/height</tt> */
    public static final IRI HEIGHT;

    /** <tt>http://schema.org/highPrice</tt> */
    public static final IRI HIGHPRICE;

    /** <tt>http://schema.org/hiringOrganization</tt> */
    public static final IRI HIRINGORGANIZATION;

    /** <tt>http://schema.org/homeLocation</tt> */
    public static final IRI HOMELOCATION;

    /** <tt>http://schema.org/homeTeam</tt> */
    public static final IRI HOMETEAM;

    /** <tt>http://schema.org/honorificPrefix</tt> */
    public static final IRI HONORIFICPREFIX;

    /** <tt>http://schema.org/honorificSuffix</tt> */
    public static final IRI HONORIFICSUFFIX;

    /** <tt>http://schema.org/hostingOrganization</tt> */
    public static final IRI HOSTINGORGANIZATION;

    /** <tt>http://schema.org/hoursAvailable</tt> */
    public static final IRI HOURSAVAILABLE;

    /** <tt>http://schema.org/httpMethod</tt> */
    public static final IRI HTTPMETHOD;

    /** <tt>http://schema.org/iataCode</tt> */
    public static final IRI IATACODE;

    /** <tt>http://schema.org/icaoCode</tt> */
    public static final IRI ICAOCODE;

    /** <tt>http://schema.org/illustrator</tt> */
    public static final IRI ILLUSTRATOR;

    /** <tt>http://schema.org/inAlbum</tt> */
    public static final IRI INALBUM;

    /** <tt>http://schema.org/inBroadcastLineup</tt> */
    public static final IRI INBROADCASTLINEUP;

    /** <tt>http://schema.org/inPlaylist</tt> */
    public static final IRI INPLAYLIST;

    /** <tt>http://schema.org/incentives</tt> */
    public static final IRI INCENTIVES;

    /** <tt>http://schema.org/includedComposition</tt> */
    public static final IRI INCLUDEDCOMPOSITION;

    /** <tt>http://schema.org/includedDataCatalog</tt> */
    public static final IRI INCLUDEDDATACATALOG;

    /** <tt>http://schema.org/includesObject</tt> */
    public static final IRI INCLUDESOBJECT;

    /** <tt>http://schema.org/industry</tt> */
    public static final IRI INDUSTRY;

    /** <tt>http://schema.org/ineligibleRegion</tt> */
    public static final IRI INELIGIBLEREGION;

    /** <tt>http://schema.org/ingredients</tt> */
    public static final IRI INGREDIENTS;

    /** <tt>http://schema.org/installUrl</tt> */
    public static final IRI INSTALLURL;

    /** <tt>http://schema.org/instructor</tt> */
    public static final IRI INSTRUCTOR;

    /** <tt>http://schema.org/interactionCount</tt> */
    public static final IRI INTERACTIONCOUNT;

    /** <tt>http://schema.org/interactionService</tt> */
    public static final IRI INTERACTIONSERVICE;

    /** <tt>http://schema.org/interactionType</tt> */
    public static final IRI INTERACTIONTYPE;

    /** <tt>http://schema.org/interactivityType</tt> */
    public static final IRI INTERACTIVITYTYPE;

    /** <tt>http://schema.org/interestRate</tt> */
    public static final IRI INTERESTRATE;

    /** <tt>http://schema.org/inventoryLevel</tt> */
    public static final IRI INVENTORYLEVEL;

    /** <tt>http://schema.org/isAccessoryOrSparePartFor</tt> */
    public static final IRI ISACCESSORYORSPAREPARTFOR;

    /** <tt>http://schema.org/isBasedOnUrl</tt> */
    public static final IRI ISBASEDONURL;

    /** <tt>http://schema.org/isConsumableFor</tt> */
    public static final IRI ISCONSUMABLEFOR;

    /** <tt>http://schema.org/isFamilyFriendly</tt> */
    public static final IRI ISFAMILYFRIENDLY;

    /** <tt>http://schema.org/isGift</tt> */
    public static final IRI ISGIFT;

    /** <tt>http://schema.org/isLiveBroadcast</tt> */
    public static final IRI ISLIVEBROADCAST;

    /** <tt>http://schema.org/isRelatedTo</tt> */
    public static final IRI ISRELATEDTO;

    /** <tt>http://schema.org/isSimilarTo</tt> */
    public static final IRI ISSIMILARTO;

    /** <tt>http://schema.org/isVariantOf</tt> */
    public static final IRI ISVARIANTOF;

    /** <tt>http://schema.org/isbn</tt> */
    public static final IRI ISBN;

    /** <tt>http://schema.org/isicV4</tt> */
    public static final IRI ISICV4;

    /** <tt>http://schema.org/isrcCode</tt> */
    public static final IRI ISRCCODE;

    /** <tt>http://schema.org/issn</tt> */
    public static final IRI ISSN;

    /** <tt>http://schema.org/issueNumber</tt> */
    public static final IRI ISSUENUMBER;

    /** <tt>http://schema.org/issuedBy</tt> */
    public static final IRI ISSUEDBY;

    /** <tt>http://schema.org/issuedThrough</tt> */
    public static final IRI ISSUEDTHROUGH;

    /** <tt>http://schema.org/iswcCode</tt> */
    public static final IRI ISWCCODE;

    /** <tt>http://schema.org/item</tt> */
    public static final IRI ITEM;

    /** <tt>http://schema.org/itemCondition</tt> */
    public static final IRI ITEMCONDITION;

    /** <tt>http://schema.org/itemListElement</tt> */
    public static final IRI ITEMLISTELEMENT;

    /** <tt>http://schema.org/itemListOrder</tt> */
    public static final IRI ITEMLISTORDER;

    /** <tt>http://schema.org/itemOffered</tt> */
    public static final IRI ITEMOFFERED;

    /** <tt>http://schema.org/itemReviewed</tt> */
    public static final IRI ITEMREVIEWED;

    /** <tt>http://schema.org/itemShipped</tt> */
    public static final IRI ITEMSHIPPED;

    /** <tt>http://schema.org/jobLocation</tt> */
    public static final IRI JOBLOCATION;

    /** <tt>http://schema.org/jobTitle</tt> */
    public static final IRI JOBTITLE;

    /** <tt>http://schema.org/keywords</tt> */
    public static final IRI KEYWORDS;

    /** <tt>http://schema.org/knownVehicleDamages</tt> */
    public static final IRI KNOWNVEHICLEDAMAGES;

    /** <tt>http://schema.org/knows</tt> */
    public static final IRI KNOWS;

    /** <tt>http://schema.org/landlord</tt> */
    public static final IRI LANDLORD;

    /** <tt>http://schema.org/language</tt> */
    public static final IRI HAS_LANGUAGE;

    /** <tt>http://schema.org/lastReviewed</tt> */
    public static final IRI LASTREVIEWED;

    /** <tt>http://schema.org/latitude</tt> */
    public static final IRI LATITUDE;

    /** <tt>http://schema.org/learningResourceType</tt> */
    public static final IRI LEARNINGRESOURCETYPE;

    /** <tt>http://schema.org/legalName</tt> */
    public static final IRI LEGALNAME;

    /** <tt>http://schema.org/leiCode</tt> */
    public static final IRI LEICODE;

    /** <tt>http://schema.org/lender</tt> */
    public static final IRI LENDER;

    /** <tt>http://schema.org/lesser</tt> */
    public static final IRI LESSER;

    /** <tt>http://schema.org/lesserOrEqual</tt> */
    public static final IRI LESSEROREQUAL;

    /** <tt>http://schema.org/license</tt> */
    public static final IRI LICENSE;

    /** <tt>http://schema.org/line</tt> */
    public static final IRI LINE;

    /** <tt>http://schema.org/liveBlogUpdate</tt> */
    public static final IRI LIVEBLOGUPDATE;

    /** <tt>http://schema.org/loanTerm</tt> */
    public static final IRI LOANTERM;

    /** <tt>http://schema.org/locationCreated</tt> */
    public static final IRI LOCATIONCREATED;

    /** <tt>http://schema.org/lodgingUnitDescription</tt> */
    public static final IRI LODGINGUNITDESCRIPTION;

    /** <tt>http://schema.org/lodgingUnitType</tt> */
    public static final IRI LODGINGUNITTYPE;

    /** <tt>http://schema.org/logo</tt> */
    public static final IRI LOGO;

    /** <tt>http://schema.org/longitude</tt> */
    public static final IRI LONGITUDE;

    /** <tt>http://schema.org/loser</tt> */
    public static final IRI LOSER;

    /** <tt>http://schema.org/lowPrice</tt> */
    public static final IRI LOWPRICE;

    /** <tt>http://schema.org/lyricist</tt> */
    public static final IRI LYRICIST;

    /** <tt>http://schema.org/lyrics</tt> */
    public static final IRI LYRICS;

    /** <tt>http://schema.org/mainContentOfPage</tt> */
    public static final IRI MAINCONTENTOFPAGE;

    /** <tt>http://schema.org/manufacturer</tt> */
    public static final IRI MANUFACTURER;

    /** <tt>http://schema.org/map</tt> */
    public static final IRI HAS_MAP;

    /** <tt>http://schema.org/mapType</tt> */
    public static final IRI MAPTYPE;

    /** <tt>http://schema.org/maps</tt> */
    public static final IRI MAPS;

    /** <tt>http://schema.org/maxPrice</tt> */
    public static final IRI MAXPRICE;

    /** <tt>http://schema.org/maxValue</tt> */
    public static final IRI MAXVALUE;

    /** <tt>http://schema.org/maximumAttendeeCapacity</tt> */
    public static final IRI MAXIMUMATTENDEECAPACITY;

    /** <tt>http://schema.org/mealService</tt> */
    public static final IRI MEALSERVICE;

    /** <tt>http://schema.org/members</tt> */
    public static final IRI MEMBERS;

    /** <tt>http://schema.org/membershipNumber</tt> */
    public static final IRI MEMBERSHIPNUMBER;

    /** <tt>http://schema.org/memoryRequirements</tt> */
    public static final IRI MEMORYREQUIREMENTS;

    /** <tt>http://schema.org/mentions</tt> */
    public static final IRI MENTIONS;

    /** <tt>http://schema.org/menu</tt> */
    public static final IRI HAS_MENU;

    /** <tt>http://schema.org/merchant</tt> */
    public static final IRI MERCHANT;

    /** <tt>http://schema.org/messageAttachment</tt> */
    public static final IRI MESSAGEATTACHMENT;

    /** <tt>http://schema.org/mileageFromOdometer</tt> */
    public static final IRI MILEAGEFROMODOMETER;

    /** <tt>http://schema.org/minPrice</tt> */
    public static final IRI MINPRICE;

    /** <tt>http://schema.org/minValue</tt> */
    public static final IRI MINVALUE;

    /** <tt>http://schema.org/minimumPaymentDue</tt> */
    public static final IRI MINIMUMPAYMENTDUE;

    /** <tt>http://schema.org/model</tt> */
    public static final IRI MODEL;

    /** <tt>http://schema.org/modifiedTime</tt> */
    public static final IRI MODIFIEDTIME;

    /** <tt>http://schema.org/mpn</tt> */
    public static final IRI MPN;

    /** <tt>http://schema.org/multipleValues</tt> */
    public static final IRI MULTIPLEVALUES;

    /** <tt>http://schema.org/musicArrangement</tt> */
    public static final IRI MUSICARRANGEMENT;

    /** <tt>http://schema.org/musicBy</tt> */
    public static final IRI MUSICBY;

    /** <tt>http://schema.org/musicCompositionForm</tt> */
    public static final IRI MUSICCOMPOSITIONFORM;

    /** <tt>http://schema.org/musicGroupMember</tt> */
    public static final IRI MUSICGROUPMEMBER;

    /** <tt>http://schema.org/musicReleaseFormat</tt> */
    public static final IRI MUSICRELEASEFORMAT;

    /** <tt>http://schema.org/musicalKey</tt> */
    public static final IRI MUSICALKEY;

    /** <tt>http://schema.org/naics</tt> */
    public static final IRI NAICS;

    /** <tt>http://schema.org/name</tt> */
    public static final IRI NAME;

    /** <tt>http://schema.org/namedPosition</tt> */
    public static final IRI NAMEDPOSITION;

    /** <tt>http://schema.org/nationality</tt> */
    public static final IRI NATIONALITY;

    /** <tt>http://schema.org/netWorth</tt> */
    public static final IRI NETWORTH;

    /** <tt>http://schema.org/nextItem</tt> */
    public static final IRI NEXTITEM;

    /** <tt>http://schema.org/nonEqual</tt> */
    public static final IRI NONEQUAL;

    /** <tt>http://schema.org/numAdults</tt> */
    public static final IRI NUMADULTS;

    /** <tt>http://schema.org/numChildren</tt> */
    public static final IRI NUMCHILDREN;

    /** <tt>http://schema.org/numTracks</tt> */
    public static final IRI NUMTRACKS;

    /** <tt>http://schema.org/numberOfAirbags</tt> */
    public static final IRI NUMBEROFAIRBAGS;

    /** <tt>http://schema.org/numberOfAxles</tt> */
    public static final IRI NUMBEROFAXLES;

    /** <tt>http://schema.org/numberOfBeds</tt> */
    public static final IRI NUMBEROFBEDS;

    /** <tt>http://schema.org/numberOfDoors</tt> */
    public static final IRI NUMBEROFDOORS;

    /** <tt>http://schema.org/numberOfEmployees</tt> */
    public static final IRI NUMBEROFEMPLOYEES;

    /** <tt>http://schema.org/numberOfEpisodes</tt> */
    public static final IRI NUMBEROFEPISODES;

    /** <tt>http://schema.org/numberOfForwardGears</tt> */
    public static final IRI NUMBEROFFORWARDGEARS;

    /** <tt>http://schema.org/numberOfItems</tt> */
    public static final IRI NUMBEROFITEMS;

    /** <tt>http://schema.org/numberOfPages</tt> */
    public static final IRI NUMBEROFPAGES;

    /** <tt>http://schema.org/numberOfPlayers</tt> */
    public static final IRI NUMBEROFPLAYERS;

    /** <tt>http://schema.org/numberOfPreviousOwners</tt> */
    public static final IRI NUMBEROFPREVIOUSOWNERS;

    /** <tt>http://schema.org/numberOfRooms</tt> */
    public static final IRI NUMBEROFROOMS;

    /** <tt>http://schema.org/numberOfSeasons</tt> */
    public static final IRI NUMBEROFSEASONS;

    /** <tt>http://schema.org/numberedPosition</tt> */
    public static final IRI NUMBEREDPOSITION;

    /** <tt>http://schema.org/nutrition</tt> */
    public static final IRI NUTRITION;

    /** <tt>http://schema.org/occupancy</tt> */
    public static final IRI OCCUPANCY;

    /** <tt>http://schema.org/occupationalCategory</tt> */
    public static final IRI OCCUPATIONALCATEGORY;

    /** <tt>http://schema.org/offerCount</tt> */
    public static final IRI OFFERCOUNT;

    /** <tt>http://schema.org/offers</tt> */
    public static final IRI OFFERS;

    /** <tt>http://schema.org/openingHours</tt> */
    public static final IRI OPENINGHOURS;

    /** <tt>http://schema.org/openingHoursSpecification</tt> */
    public static final IRI HAS_OPENINGHOURSSPECIFICATION;

    /** <tt>http://schema.org/opens</tt> */
    public static final IRI OPENS;

    /** <tt>http://schema.org/operatingSystem</tt> */
    public static final IRI OPERATINGSYSTEM;

    /** <tt>http://schema.org/opponent</tt> */
    public static final IRI OPPONENT;

    /** <tt>http://schema.org/option</tt> */
    public static final IRI OPTION;

    /** <tt>http://schema.org/orderDate</tt> */
    public static final IRI ORDERDATE;

    /** <tt>http://schema.org/orderDelivery</tt> */
    public static final IRI ORDERDELIVERY;

    /** <tt>http://schema.org/orderItemNumber</tt> */
    public static final IRI ORDERITEMNUMBER;

    /** <tt>http://schema.org/orderItemStatus</tt> */
    public static final IRI ORDERITEMSTATUS;

    /** <tt>http://schema.org/orderNumber</tt> */
    public static final IRI ORDERNUMBER;

    /** <tt>http://schema.org/orderQuantity</tt> */
    public static final IRI ORDERQUANTITY;

    /** <tt>http://schema.org/orderStatus</tt> */
    public static final IRI HAS_ORDERSTATUS;

    /** <tt>http://schema.org/orderedItem</tt> */
    public static final IRI ORDEREDITEM;

    /** <tt>http://schema.org/organizer</tt> */
    public static final IRI ORGANIZER;

    /** <tt>http://schema.org/originAddress</tt> */
    public static final IRI ORIGINADDRESS;

    /** <tt>http://schema.org/ownedFrom</tt> */
    public static final IRI OWNEDFROM;

    /** <tt>http://schema.org/ownedThrough</tt> */
    public static final IRI OWNEDTHROUGH;

    /** <tt>http://schema.org/owns</tt> */
    public static final IRI OWNS;

    /** <tt>http://schema.org/pageEnd</tt> */
    public static final IRI PAGEEND;

    /** <tt>http://schema.org/pageStart</tt> */
    public static final IRI PAGESTART;

    /** <tt>http://schema.org/pagination</tt> */
    public static final IRI PAGINATION;

    /** <tt>http://schema.org/parentItem</tt> */
    public static final IRI PARENTITEM;

    /** <tt>http://schema.org/parentService</tt> */
    public static final IRI PARENTSERVICE;

    /** <tt>http://schema.org/parents</tt> */
    public static final IRI PARENTS;

    /** <tt>http://schema.org/partOfEpisode</tt> */
    public static final IRI PARTOFEPISODE;

    /** <tt>http://schema.org/partOfInvoice</tt> */
    public static final IRI PARTOFINVOICE;

    /** <tt>http://schema.org/partOfOrder</tt> */
    public static final IRI PARTOFORDER;

    /** <tt>http://schema.org/partOfSeason</tt> */
    public static final IRI PARTOFSEASON;

    /** <tt>http://schema.org/partOfTVSeries</tt> */
    public static final IRI PARTOFTVSERIES;

    /** <tt>http://schema.org/partySize</tt> */
    public static final IRI PARTYSIZE;

    /** <tt>http://schema.org/passengerPriorityStatus</tt> */
    public static final IRI PASSENGERPRIORITYSTATUS;

    /** <tt>http://schema.org/passengerSequenceNumber</tt> */
    public static final IRI PASSENGERSEQUENCENUMBER;

    /** <tt>http://schema.org/paymentAccepted</tt> */
    public static final IRI PAYMENTACCEPTED;

    /** <tt>http://schema.org/paymentDue</tt> */
    public static final IRI PAYMENTDUE;

    /** <tt>http://schema.org/paymentMethod</tt> */
    public static final IRI HAS_PAYMENTMETHOD;

    /** <tt>http://schema.org/paymentMethodId</tt> */
    public static final IRI PAYMENTMETHODID;

    /** <tt>http://schema.org/paymentStatus</tt> */
    public static final IRI PAYMENTSTATUS;

    /** <tt>http://schema.org/paymentUrl</tt> */
    public static final IRI PAYMENTURL;

    /** <tt>http://schema.org/performerIn</tt> */
    public static final IRI PERFORMERIN;

    /** <tt>http://schema.org/performers</tt> */
    public static final IRI PERFORMERS;

    /** <tt>http://schema.org/permissionType</tt> */
    public static final IRI PERMISSIONTYPE;

    /** <tt>http://schema.org/permissions</tt> */
    public static final IRI PERMISSIONS;

    /** <tt>http://schema.org/permitAudience</tt> */
    public static final IRI PERMITAUDIENCE;

    /** <tt>http://schema.org/permittedUsage</tt> */
    public static final IRI PERMITTEDUSAGE;

    /** <tt>http://schema.org/petsAllowed</tt> */
    public static final IRI PETSALLOWED;

    /** <tt>http://schema.org/photos</tt> */
    public static final IRI PHOTOS;

    /** <tt>http://schema.org/pickupLocation</tt> */
    public static final IRI PICKUPLOCATION;

    /** <tt>http://schema.org/pickupTime</tt> */
    public static final IRI PICKUPTIME;

    /** <tt>http://schema.org/playMode</tt> */
    public static final IRI PLAYMODE;

    /** <tt>http://schema.org/playerType</tt> */
    public static final IRI PLAYERTYPE;

    /** <tt>http://schema.org/playersOnline</tt> */
    public static final IRI PLAYERSONLINE;

    /** <tt>http://schema.org/polygon</tt> */
    public static final IRI POLYGON;

    /** <tt>http://schema.org/postOfficeBoxNumber</tt> */
    public static final IRI POSTOFFICEBOXNUMBER;

    /** <tt>http://schema.org/postalCode</tt> */
    public static final IRI POSTALCODE;

    /** <tt>http://schema.org/potentialAction</tt> */
    public static final IRI POTENTIALACTION;

    /** <tt>http://schema.org/predecessorOf</tt> */
    public static final IRI PREDECESSOROF;

    /** <tt>http://schema.org/prepTime</tt> */
    public static final IRI PREPTIME;

    /** <tt>http://schema.org/previousItem</tt> */
    public static final IRI PREVIOUSITEM;

    /** <tt>http://schema.org/previousStartDate</tt> */
    public static final IRI PREVIOUSSTARTDATE;

    /** <tt>http://schema.org/price</tt> */
    public static final IRI PRICE;

    /** <tt>http://schema.org/priceComponent</tt> */
    public static final IRI PRICECOMPONENT;

    /** <tt>http://schema.org/priceCurrency</tt> */
    public static final IRI PRICECURRENCY;

    /** <tt>http://schema.org/priceRange</tt> */
    public static final IRI PRICERANGE;

    /** <tt>http://schema.org/priceSpecification</tt> */
    public static final IRI HAS_PRICESPECIFICATION;

    /** <tt>http://schema.org/priceType</tt> */
    public static final IRI PRICETYPE;

    /** <tt>http://schema.org/priceValidUntil</tt> */
    public static final IRI PRICEVALIDUNTIL;

    /** <tt>http://schema.org/primaryImageOfPage</tt> */
    public static final IRI PRIMARYIMAGEOFPAGE;

    /** <tt>http://schema.org/printColumn</tt> */
    public static final IRI PRINTCOLUMN;

    /** <tt>http://schema.org/printEdition</tt> */
    public static final IRI PRINTEDITION;

    /** <tt>http://schema.org/printPage</tt> */
    public static final IRI PRINTPAGE;

    /** <tt>http://schema.org/printSection</tt> */
    public static final IRI PRINTSECTION;

    /** <tt>http://schema.org/processingTime</tt> */
    public static final IRI PROCESSINGTIME;

    /** <tt>http://schema.org/processorRequirements</tt> */
    public static final IRI PROCESSORREQUIREMENTS;

    /** <tt>http://schema.org/producer</tt> */
    public static final IRI PRODUCER;

    /** <tt>http://schema.org/produces</tt> */
    public static final IRI PRODUCES;

    /** <tt>http://schema.org/productID</tt> */
    public static final IRI PRODUCTID;

    /** <tt>http://schema.org/productSupported</tt> */
    public static final IRI PRODUCTSUPPORTED;

    /** <tt>http://schema.org/productionCompany</tt> */
    public static final IRI PRODUCTIONCOMPANY;

    /** <tt>http://schema.org/productionDate</tt> */
    public static final IRI PRODUCTIONDATE;

    /** <tt>http://schema.org/proficiencyLevel</tt> */
    public static final IRI PROFICIENCYLEVEL;

    /** <tt>http://schema.org/programMembershipUsed</tt> */
    public static final IRI PROGRAMMEMBERSHIPUSED;

    /** <tt>http://schema.org/programName</tt> */
    public static final IRI PROGRAMNAME;

    /** <tt>http://schema.org/programmingLanguage</tt> */
    public static final IRI PROGRAMMINGLANGUAGE;

    /** <tt>http://schema.org/programmingModel</tt> */
    public static final IRI PROGRAMMINGMODEL;

    /** <tt>http://schema.org/propertyID</tt> */
    public static final IRI PROPERTYID;

    /** <tt>http://schema.org/proteinContent</tt> */
    public static final IRI PROTEINCONTENT;

    /** <tt>http://schema.org/providerMobility</tt> */
    public static final IRI PROVIDERMOBILITY;

    /** <tt>http://schema.org/providesBroadcastService</tt> */
    public static final IRI PROVIDESBROADCASTSERVICE;

    /** <tt>http://schema.org/providesService</tt> */
    public static final IRI PROVIDESSERVICE;

    /** <tt>http://schema.org/publicAccess</tt> */
    public static final IRI PUBLICACCESS;

    /** <tt>http://schema.org/publication</tt> */
    public static final IRI PUBLICATION;

    /** <tt>http://schema.org/publishedOn</tt> */
    public static final IRI PUBLISHEDON;

    /** <tt>http://schema.org/publisher</tt> */
    public static final IRI PUBLISHER;

    /** <tt>http://schema.org/publishingPrinciples</tt> */
    public static final IRI PUBLISHINGPRINCIPLES;

    /** <tt>http://schema.org/purchaseDate</tt> */
    public static final IRI PURCHASEDATE;

    /** <tt>http://schema.org/qualifications</tt> */
    public static final IRI QUALIFICATIONS;

    /** <tt>http://schema.org/query</tt> */
    public static final IRI QUERY;

    /** <tt>http://schema.org/quest</tt> */
    public static final IRI QUEST;

    /** <tt>http://schema.org/question</tt> */
    public static final IRI HAS_QUESTION;

    /** <tt>http://schema.org/ratingCount</tt> */
    public static final IRI RATINGCOUNT;

    /** <tt>http://schema.org/ratingValue</tt> */
    public static final IRI RATINGVALUE;

    /** <tt>http://schema.org/readonlyValue</tt> */
    public static final IRI READONLYVALUE;

    /** <tt>http://schema.org/realEstateAgent</tt> */
    public static final IRI HAS_REALESTATEAGENT;

    /** <tt>http://schema.org/recipe</tt> */
    public static final IRI HAS_RECIPE;

    /** <tt>http://schema.org/recipeCategory</tt> */
    public static final IRI RECIPECATEGORY;

    /** <tt>http://schema.org/recipeCuisine</tt> */
    public static final IRI RECIPECUISINE;

    /** <tt>http://schema.org/recipeInstructions</tt> */
    public static final IRI RECIPEINSTRUCTIONS;

    /** <tt>http://schema.org/recipeYield</tt> */
    public static final IRI RECIPEYIELD;

    /** <tt>http://schema.org/recordLabel</tt> */
    public static final IRI RECORDLABEL;

    /** <tt>http://schema.org/referenceQuantity</tt> */
    public static final IRI REFERENCEQUANTITY;

    /** <tt>http://schema.org/referencesOrder</tt> */
    public static final IRI REFERENCESORDER;

    /** <tt>http://schema.org/regionsAllowed</tt> */
    public static final IRI REGIONSALLOWED;

    /** <tt>http://schema.org/relatedLink</tt> */
    public static final IRI RELATEDLINK;

    /** <tt>http://schema.org/relatedTo</tt> */
    public static final IRI RELATEDTO;

    /** <tt>http://schema.org/releaseDate</tt> */
    public static final IRI RELEASEDATE;

    /** <tt>http://schema.org/releaseNotes</tt> */
    public static final IRI RELEASENOTES;

    /** <tt>http://schema.org/releasedEvent</tt> */
    public static final IRI RELEASEDEVENT;

    /** <tt>http://schema.org/remainingAttendeeCapacity</tt> */
    public static final IRI REMAININGATTENDEECAPACITY;

    /** <tt>http://schema.org/replacee</tt> */
    public static final IRI REPLACEE;

    /** <tt>http://schema.org/replacer</tt> */
    public static final IRI REPLACER;

    /** <tt>http://schema.org/replyToUrl</tt> */
    public static final IRI REPLYTOURL;

    /** <tt>http://schema.org/reportNumber</tt> */
    public static final IRI REPORTNUMBER;

    /** <tt>http://schema.org/representativeOfPage</tt> */
    public static final IRI REPRESENTATIVEOFPAGE;

    /** <tt>http://schema.org/requiredCollateral</tt> */
    public static final IRI REQUIREDCOLLATERAL;

    /** <tt>http://schema.org/requiredGender</tt> */
    public static final IRI REQUIREDGENDER;

    /** <tt>http://schema.org/requiredMaxAge</tt> */
    public static final IRI REQUIREDMAXAGE;

    /** <tt>http://schema.org/requiredMinAge</tt> */
    public static final IRI REQUIREDMINAGE;

    /** <tt>http://schema.org/requiredQuantity</tt> */
    public static final IRI REQUIREDQUANTITY;

    /** <tt>http://schema.org/requirements</tt> */
    public static final IRI REQUIREMENTS;

    /** <tt>http://schema.org/requiresSubscription</tt> */
    public static final IRI REQUIRESSUBSCRIPTION;

    /** <tt>http://schema.org/reservationFor</tt> */
    public static final IRI RESERVATIONFOR;

    /** <tt>http://schema.org/reservationId</tt> */
    public static final IRI RESERVATIONID;

    /** <tt>http://schema.org/reservationStatus</tt> */
    public static final IRI RESERVATIONSTATUS;

    /** <tt>http://schema.org/reservedTicket</tt> */
    public static final IRI RESERVEDTICKET;

    /** <tt>http://schema.org/responsibilities</tt> */
    public static final IRI RESPONSIBILITIES;

    /** <tt>http://schema.org/resultComment</tt> */
    public static final IRI RESULTCOMMENT;

    /** <tt>http://schema.org/resultReview</tt> */
    public static final IRI RESULTREVIEW;

    /** <tt>http://schema.org/reviewBody</tt> */
    public static final IRI REVIEWBODY;

    /** <tt>http://schema.org/reviewCount</tt> */
    public static final IRI REVIEWCOUNT;

    /** <tt>http://schema.org/reviewRating</tt> */
    public static final IRI REVIEWRATING;

    /** <tt>http://schema.org/reviewedBy</tt> */
    public static final IRI REVIEWEDBY;

    /** <tt>http://schema.org/reviews</tt> */
    public static final IRI REVIEWS;

    /** <tt>http://schema.org/rsvpResponse</tt> */
    public static final IRI RSVPRESPONSE;

    /** <tt>http://schema.org/runtime</tt> */
    public static final IRI RUNTIME;

    /** <tt>http://schema.org/salaryCurrency</tt> */
    public static final IRI SALARYCURRENCY;

    /** <tt>http://schema.org/sameAs</tt> */
    public static final IRI SAMEAS;

    /** <tt>http://schema.org/sampleType</tt> */
    public static final IRI SAMPLETYPE;

    /** <tt>http://schema.org/saturatedFatContent</tt> */
    public static final IRI SATURATEDFATCONTENT;

    /** <tt>http://schema.org/scheduledPaymentDate</tt> */
    public static final IRI SCHEDULEDPAYMENTDATE;

    /** <tt>http://schema.org/scheduledTime</tt> */
    public static final IRI SCHEDULEDTIME;

    /** <tt>http://schema.org/schemaVersion</tt> */
    public static final IRI SCHEMAVERSION;

    /** <tt>http://schema.org/screenCount</tt> */
    public static final IRI SCREENCOUNT;

    /** <tt>http://schema.org/screenshot</tt> */
    public static final IRI SCREENSHOT;

    /** <tt>http://schema.org/seasonNumber</tt> */
    public static final IRI SEASONNUMBER;

    /** <tt>http://schema.org/seasons</tt> */
    public static final IRI SEASONS;

    /** <tt>http://schema.org/seatNumber</tt> */
    public static final IRI SEATNUMBER;

    /** <tt>http://schema.org/seatRow</tt> */
    public static final IRI SEATROW;

    /** <tt>http://schema.org/seatSection</tt> */
    public static final IRI SEATSECTION;

    /** <tt>http://schema.org/seatingType</tt> */
    public static final IRI SEATINGTYPE;

    /** <tt>http://schema.org/securityScreening</tt> */
    public static final IRI SECURITYSCREENING;

    /** <tt>http://schema.org/seeks</tt> */
    public static final IRI SEEKS;

    /** <tt>http://schema.org/sender</tt> */
    public static final IRI SENDER;

    /** <tt>http://schema.org/serverStatus</tt> */
    public static final IRI SERVERSTATUS;

    /** <tt>http://schema.org/servesCuisine</tt> */
    public static final IRI SERVESCUISINE;

    /** <tt>http://schema.org/serviceAudience</tt> */
    public static final IRI SERVICEAUDIENCE;

    /** <tt>http://schema.org/serviceLocation</tt> */
    public static final IRI SERVICELOCATION;

    /** <tt>http://schema.org/serviceOperator</tt> */
    public static final IRI SERVICEOPERATOR;

    /** <tt>http://schema.org/servicePhone</tt> */
    public static final IRI SERVICEPHONE;

    /** <tt>http://schema.org/servicePostalAddress</tt> */
    public static final IRI SERVICEPOSTALADDRESS;

    /** <tt>http://schema.org/serviceSmsNumber</tt> */
    public static final IRI SERVICESMSNUMBER;

    /** <tt>http://schema.org/serviceType</tt> */
    public static final IRI SERVICETYPE;

    /** <tt>http://schema.org/serviceUrl</tt> */
    public static final IRI SERVICEURL;

    /** <tt>http://schema.org/servingSize</tt> */
    public static final IRI SERVINGSIZE;

    /** <tt>http://schema.org/sharedContent</tt> */
    public static final IRI SHAREDCONTENT;

    /** <tt>http://schema.org/siblings</tt> */
    public static final IRI SIBLINGS;

    /** <tt>http://schema.org/significantLinks</tt> */
    public static final IRI SIGNIFICANTLINKS;

    /** <tt>http://schema.org/skills</tt> */
    public static final IRI SKILLS;

    /** <tt>http://schema.org/sku</tt> */
    public static final IRI SKU;

    /** <tt>http://schema.org/smokingAllowed</tt> */
    public static final IRI SMOKINGALLOWED;

    /** <tt>http://schema.org/sodiumContent</tt> */
    public static final IRI SODIUMCONTENT;

    /** <tt>http://schema.org/softwareAddOn</tt> */
    public static final IRI SOFTWAREADDON;

    /** <tt>http://schema.org/softwareHelp</tt> */
    public static final IRI SOFTWAREHELP;

    /** <tt>http://schema.org/softwareVersion</tt> */
    public static final IRI SOFTWAREVERSION;

    /** <tt>http://schema.org/sourceOrganization</tt> */
    public static final IRI SOURCEORGANIZATION;

    /** <tt>http://schema.org/spatial</tt> */
    public static final IRI SPATIAL;

    /** <tt>http://schema.org/specialCommitments</tt> */
    public static final IRI SPECIALCOMMITMENTS;

    /** <tt>http://schema.org/specialOpeningHoursSpecification</tt> */
    public static final IRI SPECIALOPENINGHOURSSPECIFICATION;

    /** <tt>http://schema.org/specialty</tt> */
    public static final IRI HAS_SPECIALTY;

    /** <tt>http://schema.org/sport</tt> */
    public static final IRI SPORT;

    /** <tt>http://schema.org/sportsActivityLocation</tt> */
    public static final IRI HAS_SPORTSACTIVITYLOCATION;

    /** <tt>http://schema.org/sportsEvent</tt> */
    public static final IRI HAS_SPORTSEVENT;

    /** <tt>http://schema.org/sportsTeam</tt> */
    public static final IRI HAS_SPORTSTEAM;

    /** <tt>http://schema.org/spouse</tt> */
    public static final IRI SPOUSE;

    /** <tt>http://schema.org/starRating</tt> */
    public static final IRI STARRATING;

    /** <tt>http://schema.org/startDate</tt> */
    public static final IRI STARTDATE;

    /** <tt>http://schema.org/startTime</tt> */
    public static final IRI STARTTIME;

    /** <tt>http://schema.org/steeringPosition</tt> */
    public static final IRI STEERINGPOSITION;

    /** <tt>http://schema.org/stepValue</tt> */
    public static final IRI STEPVALUE;

    /** <tt>http://schema.org/steps</tt> */
    public static final IRI STEPS;

    /** <tt>http://schema.org/storageRequirements</tt> */
    public static final IRI STORAGEREQUIREMENTS;

    /** <tt>http://schema.org/streetAddress</tt> */
    public static final IRI STREETADDRESS;

    /** <tt>http://schema.org/subEvents</tt> */
    public static final IRI SUBEVENTS;

    /** <tt>http://schema.org/subReservation</tt> */
    public static final IRI SUBRESERVATION;

    /** <tt>http://schema.org/subtitleLanguage</tt> */
    public static final IRI SUBTITLELANGUAGE;

    /** <tt>http://schema.org/successorOf</tt> */
    public static final IRI SUCCESSOROF;

    /** <tt>http://schema.org/sugarContent</tt> */
    public static final IRI SUGARCONTENT;

    /** <tt>http://schema.org/suggestedGender</tt> */
    public static final IRI SUGGESTEDGENDER;

    /** <tt>http://schema.org/suggestedMaxAge</tt> */
    public static final IRI SUGGESTEDMAXAGE;

    /** <tt>http://schema.org/suggestedMinAge</tt> */
    public static final IRI SUGGESTEDMINAGE;

    /** <tt>http://schema.org/suitableForDiet</tt> */
    public static final IRI SUITABLEFORDIET;

    /** <tt>http://schema.org/supportingData</tt> */
    public static final IRI SUPPORTINGDATA;

    /** <tt>http://schema.org/surface</tt> */
    public static final IRI SURFACE;

    /** <tt>http://schema.org/target</tt> */
    public static final IRI TARGET;

    /** <tt>http://schema.org/targetDescription</tt> */
    public static final IRI TARGETDESCRIPTION;

    /** <tt>http://schema.org/targetName</tt> */
    public static final IRI TARGETNAME;

    /** <tt>http://schema.org/targetPlatform</tt> */
    public static final IRI TARGETPLATFORM;

    /** <tt>http://schema.org/targetProduct</tt> */
    public static final IRI TARGETPRODUCT;

    /** <tt>http://schema.org/targetUrl</tt> */
    public static final IRI TARGETURL;

    /** <tt>http://schema.org/taxID</tt> */
    public static final IRI TAXID;

    /** <tt>http://schema.org/telephone</tt> */
    public static final IRI TELEPHONE;

    /** <tt>http://schema.org/temporal</tt> */
    public static final IRI TEMPORAL;

    /** <tt>http://schema.org/text</tt> */
    public static final IRI HAS_TEXT;

    /** <tt>http://schema.org/thumbnail</tt> */
    public static final IRI THUMBNAIL;

    /** <tt>http://schema.org/thumbnailUrl</tt> */
    public static final IRI THUMBNAILURL;

    /** <tt>http://schema.org/tickerSymbol</tt> */
    public static final IRI TICKERSYMBOL;

    /** <tt>http://schema.org/ticketNumber</tt> */
    public static final IRI TICKETNUMBER;

    /** <tt>http://schema.org/ticketToken</tt> */
    public static final IRI TICKETTOKEN;

    /** <tt>http://schema.org/ticketedSeat</tt> */
    public static final IRI TICKETEDSEAT;

    /** <tt>http://schema.org/timeRequired</tt> */
    public static final IRI TIMEREQUIRED;

    /** <tt>http://schema.org/title</tt> */
    public static final IRI TITLE;

    /** <tt>http://schema.org/toLocation</tt> */
    public static final IRI TOLOCATION;

    /** <tt>http://schema.org/toRecipient</tt> */
    public static final IRI TORECIPIENT;

    /** <tt>http://schema.org/tool</tt> */
    public static final IRI TOOL;

    /** <tt>http://schema.org/totalPaymentDue</tt> */
    public static final IRI TOTALPAYMENTDUE;

    /** <tt>http://schema.org/totalPrice</tt> */
    public static final IRI TOTALPRICE;

    /** <tt>http://schema.org/totalTime</tt> */
    public static final IRI TOTALTIME;

    /** <tt>http://schema.org/touristType</tt> */
    public static final IRI TOURISTTYPE;

    /** <tt>http://schema.org/trackingNumber</tt> */
    public static final IRI TRACKINGNUMBER;

    /** <tt>http://schema.org/trackingUrl</tt> */
    public static final IRI TRACKINGURL;

    /** <tt>http://schema.org/tracks</tt> */
    public static final IRI TRACKS;

    /** <tt>http://schema.org/trailer</tt> */
    public static final IRI TRAILER;

    /** <tt>http://schema.org/trainName</tt> */
    public static final IRI TRAINNAME;

    /** <tt>http://schema.org/trainNumber</tt> */
    public static final IRI TRAINNUMBER;

    /** <tt>http://schema.org/transFatContent</tt> */
    public static final IRI TRANSFATCONTENT;

    /** <tt>http://schema.org/transcript</tt> */
    public static final IRI TRANSCRIPT;

    /** <tt>http://schema.org/translator</tt> */
    public static final IRI TRANSLATOR;

    /** <tt>http://schema.org/typeOfBed</tt> */
    public static final IRI TYPEOFBED;

    /** <tt>http://schema.org/typeOfGood</tt> */
    public static final IRI TYPEOFGOOD;

    /** <tt>http://schema.org/typicalAgeRange</tt> */
    public static final IRI TYPICALAGERANGE;

    /** <tt>http://schema.org/underName</tt> */
    public static final IRI UNDERNAME;

    /** <tt>http://schema.org/unitCode</tt> */
    public static final IRI UNITCODE;

    /** <tt>http://schema.org/unitText</tt> */
    public static final IRI UNITTEXT;

    /** <tt>http://schema.org/unsaturatedFatContent</tt> */
    public static final IRI UNSATURATEDFATCONTENT;

    /** <tt>http://schema.org/uploadDate</tt> */
    public static final IRI UPLOADDATE;

    /** <tt>http://schema.org/upvoteCount</tt> */
    public static final IRI UPVOTECOUNT;

    /** <tt>http://schema.org/url</tt> */
    public static final IRI HAS_URL;

    /** <tt>http://schema.org/urlTemplate</tt> */
    public static final IRI URLTEMPLATE;

    /** <tt>http://schema.org/userInteractionCount</tt> */
    public static final IRI USERINTERACTIONCOUNT;

    /** <tt>http://schema.org/validFor</tt> */
    public static final IRI VALIDFOR;

    /** <tt>http://schema.org/validFrom</tt> */
    public static final IRI VALIDFROM;

    /** <tt>http://schema.org/validIn</tt> */
    public static final IRI VALIDIN;

    /** <tt>http://schema.org/validThrough</tt> */
    public static final IRI VALIDTHROUGH;

    /** <tt>http://schema.org/validUntil</tt> */
    public static final IRI VALIDUNTIL;

    /** <tt>http://schema.org/value</tt> */
    public static final IRI VALUE;

    /** <tt>http://schema.org/valueAddedTaxIncluded</tt> */
    public static final IRI VALUEADDEDTAXINCLUDED;

    /** <tt>http://schema.org/valueMaxLength</tt> */
    public static final IRI VALUEMAXLENGTH;

    /** <tt>http://schema.org/valueMinLength</tt> */
    public static final IRI VALUEMINLENGTH;

    /** <tt>http://schema.org/valueName</tt> */
    public static final IRI VALUENAME;

    /** <tt>http://schema.org/valuePattern</tt> */
    public static final IRI VALUEPATTERN;

    /** <tt>http://schema.org/valueReference</tt> */
    public static final IRI VALUEREFERENCE;

    /** <tt>http://schema.org/valueRequired</tt> */
    public static final IRI VALUEREQUIRED;

    /** <tt>http://schema.org/vatID</tt> */
    public static final IRI VATID;

    /** <tt>http://schema.org/vehicleConfiguration</tt> */
    public static final IRI VEHICLECONFIGURATION;

    /** <tt>http://schema.org/vehicleEngine</tt> */
    public static final IRI VEHICLEENGINE;

    /** <tt>http://schema.org/vehicleIdentificationNumber</tt> */
    public static final IRI VEHICLEIDENTIFICATIONNUMBER;

    /** <tt>http://schema.org/vehicleInteriorColor</tt> */
    public static final IRI VEHICLEINTERIORCOLOR;

    /** <tt>http://schema.org/vehicleInteriorType</tt> */
    public static final IRI VEHICLEINTERIORTYPE;

    /** <tt>http://schema.org/vehicleModelDate</tt> */
    public static final IRI VEHICLEMODELDATE;

    /** <tt>http://schema.org/vehicleSeatingCapacity</tt> */
    public static final IRI VEHICLESEATINGCAPACITY;

    /** <tt>http://schema.org/vehicleSpecialUsage</tt> */
    public static final IRI VEHICLESPECIALUSAGE;

    /** <tt>http://schema.org/vehicleTransmission</tt> */
    public static final IRI VEHICLETRANSMISSION;

    /** <tt>http://schema.org/vendor</tt> */
    public static final IRI VENDOR;

    /** <tt>http://schema.org/version</tt> */
    public static final IRI VERSION;

    /** <tt>http://schema.org/video</tt> */
    public static final IRI VIDEO;

    /** <tt>http://schema.org/videoFormat</tt> */
    public static final IRI VIDEOFORMAT;

    /** <tt>http://schema.org/videoFrameSize</tt> */
    public static final IRI VIDEOFRAMESIZE;

    /** <tt>http://schema.org/videoQuality</tt> */
    public static final IRI VIDEOQUALITY;

    /** <tt>http://schema.org/volumeNumber</tt> */
    public static final IRI VOLUMENUMBER;

    /** <tt>http://schema.org/warrantyPromise</tt> */
    public static final IRI HAS_WARRANTYPROMISE;

    /** <tt>http://schema.org/warrantyScope</tt> */
    public static final IRI HAS_WARRANTYSCOPE;

    /** <tt>http://schema.org/webCheckinTime</tt> */
    public static final IRI WEBCHECKINTIME;

    /** <tt>http://schema.org/weight</tt> */
    public static final IRI WEIGHT;

    /** <tt>http://schema.org/width</tt> */
    public static final IRI WIDTH;

    /** <tt>http://schema.org/winner</tt> */
    public static final IRI WINNER;

    /** <tt>http://schema.org/wordCount</tt> */
    public static final IRI WORDCOUNT;

    /** <tt>http://schema.org/workHours</tt> */
    public static final IRI WORKHOURS;

    /** <tt>http://schema.org/workLocation</tt> */
    public static final IRI WORKLOCATION;

    /** <tt>http://schema.org/workPerformed</tt> */
    public static final IRI WORKPERFORMED;

    /** <tt>http://schema.org/workPresented</tt> */
    public static final IRI WORKPRESENTED;

    /** <tt>http://schema.org/worksFor</tt> */
    public static final IRI WORKSFOR;

    /** <tt>http://schema.org/worstRating</tt> */
    public static final IRI WORSTRATING;

    /** <tt>http://schema.org/yearlyRevenue</tt> */
    public static final IRI YEARLYREVENUE;

    /** <tt>http://schema.org/yearsInOperation</tt> */
    public static final IRI YEARSINOPERATION;

    /** <tt>http://schema.org/about</tt> */
    public static final IRI ABOUT;

    /** <tt>http://schema.org/actionApplication</tt> */
    public static final IRI ACTIONAPPLICATION;

    /** <tt>http://schema.org/actionOption</tt> */
    public static final IRI ACTIONOPTION;

    /** <tt>http://schema.org/actor</tt> */
    public static final IRI ACTOR;

    /** <tt>http://schema.org/album</tt> */
    public static final IRI ALBUM;

    /** <tt>http://schema.org/albumRelease</tt> */
    public static final IRI ALBUMRELEASE;

    /** <tt>http://schema.org/alumni</tt> */
    public static final IRI ALUMNI;

    /** <tt>http://schema.org/alumniOf</tt> */
    public static final IRI ALUMNIOF;

    /** <tt>http://schema.org/artworkSurface</tt> */
    public static final IRI ARTWORKSURFACE;

    /** <tt>http://schema.org/attendee</tt> */
    public static final IRI ATTENDEE;

    /** <tt>http://schema.org/audience</tt> */
    public static final IRI HAS_AUDIENCE;

    /** <tt>http://schema.org/availableOnDevice</tt> */
    public static final IRI AVAILABLEONDEVICE;

    /** <tt>http://schema.org/award</tt> */
    public static final IRI AWARD;

    /** <tt>http://schema.org/blogPost</tt> */
    public static final IRI BLOGPOST;

    /** <tt>http://schema.org/broker</tt> */
    public static final IRI BROKER;

    /** <tt>http://schema.org/codeSampleType</tt> */
    public static final IRI CODESAMPLETYPE;

    /** <tt>http://schema.org/colleague</tt> */
    public static final IRI COLLEAGUE;

    /** <tt>http://schema.org/contactPoint</tt> */
    public static final IRI HAS_CONTACTPOINT;

    /** <tt>http://schema.org/containsPlace</tt> */
    public static final IRI CONTAINSPLACE;

    /** <tt>http://schema.org/containsSeason</tt> */
    public static final IRI CONTAINSSEASON;

    /** <tt>http://schema.org/contentLocation</tt> */
    public static final IRI CONTENTLOCATION;

    /** <tt>http://schema.org/dataset</tt> */
    public static final IRI HAS_DATASET;

    /** <tt>http://schema.org/description</tt> */
    public static final IRI DESCRIPTION;

    /** <tt>http://schema.org/director</tt> */
    public static final IRI DIRECTOR;

    /** <tt>http://schema.org/duration</tt> */
    public static final IRI HAS_DURATION;

    /** <tt>http://schema.org/employee</tt> */
    public static final IRI EMPLOYEE;

    /** <tt>http://schema.org/encoding</tt> */
    public static final IRI ENCODING;

    /** <tt>http://schema.org/encodingFormat</tt> */
    public static final IRI ENCODINGFORMAT;

    /** <tt>http://schema.org/episode</tt> */
    public static final IRI HAS_EPISODE;

    /** <tt>http://schema.org/event</tt> */
    public static final IRI HAS_EVENT;

    /** <tt>http://schema.org/exampleOfWork</tt> */
    public static final IRI EXAMPLEOFWORK;

    /** <tt>http://schema.org/executableLibraryName</tt> */
    public static final IRI EXECUTABLELIBRARYNAME;

    /** <tt>http://schema.org/exerciseCourse</tt> */
    public static final IRI EXERCISECOURSE;

    /** <tt>http://schema.org/founder</tt> */
    public static final IRI FOUNDER;

    /** <tt>http://schema.org/game</tt> */
    public static final IRI HAS_GAME;

    /** <tt>http://schema.org/gameServer</tt> */
    public static final IRI HAS_GAMESERVER;

    /** <tt>http://schema.org/hasMenu</tt> */
    public static final IRI HASMENU;

    /** <tt>http://schema.org/inLanguage</tt> */
    public static final IRI INLANGUAGE;

    /** <tt>http://schema.org/incentiveCompensation</tt> */
    public static final IRI INCENTIVECOMPENSATION;

    /** <tt>http://schema.org/interactionStatistic</tt> */
    public static final IRI INTERACTIONSTATISTIC;

    /** <tt>http://schema.org/isAccessibleForFree</tt> */
    public static final IRI ISACCESSIBLEFORFREE;

    /** <tt>http://schema.org/isBasedOn</tt> */
    public static final IRI ISBASEDON;

    /** <tt>http://schema.org/jobBenefits</tt> */
    public static final IRI JOBBENEFITS;

    /** <tt>http://schema.org/mainEntity</tt> */
    public static final IRI MAINENTITY;

    /** <tt>http://schema.org/mainEntityOfPage</tt> */
    public static final IRI MAINENTITYOFPAGE;

    /** <tt>http://schema.org/makesOffer</tt> */
    public static final IRI MAKESOFFER;

    /** <tt>http://schema.org/offeredBy</tt> */
    public static final IRI OFFEREDBY;

    /** <tt>http://schema.org/parent</tt> */
    public static final IRI PARENT;

    /** <tt>http://schema.org/partOfSeries</tt> */
    public static final IRI PARTOFSERIES;

    /** <tt>http://schema.org/paymentDueDate</tt> */
    public static final IRI PAYMENTDUEDATE;

    /** <tt>http://schema.org/performTime</tt> */
    public static final IRI PERFORMTIME;

    /** <tt>http://schema.org/performer</tt> */
    public static final IRI PERFORMER;

    /** <tt>http://schema.org/photo</tt> */
    public static final IRI PHOTO;

    /** <tt>http://schema.org/provider</tt> */
    public static final IRI PROVIDER;

    /** <tt>http://schema.org/recipeIngredient</tt> */
    public static final IRI RECIPEINGREDIENT;

    /** <tt>http://schema.org/recordedAs</tt> */
    public static final IRI RECORDEDAS;

    /** <tt>http://schema.org/recordedAt</tt> */
    public static final IRI RECORDEDAT;

    /** <tt>http://schema.org/recordedIn</tt> */
    public static final IRI RECORDEDIN;

    /** <tt>http://schema.org/recordingOf</tt> */
    public static final IRI RECORDINGOF;

    /** <tt>http://schema.org/releaseOf</tt> */
    public static final IRI RELEASEOF;

    /** <tt>http://schema.org/review</tt> */
    public static final IRI HAS_REVIEW;

    /** <tt>http://schema.org/roleName</tt> */
    public static final IRI ROLENAME;

    /** <tt>http://schema.org/runtimePlatform</tt> */
    public static final IRI RUNTIMEPLATFORM;

    /** <tt>http://schema.org/season</tt> */
    public static final IRI HAS_SEASON;

    /** <tt>http://schema.org/serialNumber</tt> */
    public static final IRI SERIALNUMBER;

    /** <tt>http://schema.org/serviceArea</tt> */
    public static final IRI SERVICEAREA;

    /** <tt>http://schema.org/serviceOutput</tt> */
    public static final IRI SERVICEOUTPUT;

    /** <tt>http://schema.org/sibling</tt> */
    public static final IRI SIBLING;

    /** <tt>http://schema.org/significantLink</tt> */
    public static final IRI SIGNIFICANTLINK;

    /** <tt>http://schema.org/softwareRequirements</tt> */
    public static final IRI SOFTWAREREQUIREMENTS;

    /** <tt>http://schema.org/spatialCoverage</tt> */
    public static final IRI SPATIALCOVERAGE;

    /** <tt>http://schema.org/sponsor</tt> */
    public static final IRI SPONSOR;

    /** <tt>http://schema.org/subOrganization</tt> */
    public static final IRI SUBORGANIZATION;

    /** <tt>http://schema.org/suggestedAnswer</tt> */
    public static final IRI SUGGESTEDANSWER;

    /** <tt>http://schema.org/superEvent</tt> */
    public static final IRI SUPEREVENT;

    /** <tt>http://schema.org/targetCollection</tt> */
    public static final IRI TARGETCOLLECTION;

    /** <tt>http://schema.org/track</tt> */
    public static final IRI TRACK;

    /** <tt>http://schema.org/warranty</tt> */
    public static final IRI WARRANTY;

    /** <tt>http://schema.org/workExample</tt> */
    public static final IRI WORKEXAMPLE;

    /** <tt>http://schema.org/yield</tt> */
    public static final IRI YIELD;

    /** <tt>http://schema.org/competitor</tt> */
    public static final IRI COMPETITOR;

    /** <tt>http://schema.org/containedInPlace</tt> */
    public static final IRI CONTAINEDINPLACE;

    /** <tt>http://schema.org/hasMap</tt> */
    public static final IRI HASMAP;

    /** <tt>http://schema.org/image</tt> */
    public static final IRI IMAGE;

    /** <tt>http://schema.org/material</tt> */
    public static final IRI MATERIAL;

    /** <tt>http://schema.org/memberOf</tt> */
    public static final IRI MEMBEROF;

    /** <tt>http://schema.org/parentOrganization</tt> */
    public static final IRI PARENTORGANIZATION;

    /** <tt>http://schema.org/result</tt> */
    public static final IRI RESULT;

    /** <tt>http://schema.org/seller</tt> */
    public static final IRI SELLER;

    /** <tt>http://schema.org/step</tt> */
    public static final IRI STEP;

    /** <tt>http://schema.org/subEvent</tt> */
    public static final IRI SUBEVENT;

    /** <tt>http://schema.org/supply</tt> */
    public static final IRI SUPPLY;

    /** <tt>http://schema.org/temporalCoverage</tt> */
    public static final IRI TEMPORALCOVERAGE;

    /** <tt>http://schema.org/workFeatured</tt> */
    public static final IRI WORKFEATURED;

    /** <tt>http://schema.org/areaServed</tt> */
    public static final IRI AREASERVED;

    /** <tt>http://schema.org/includedInDataCatalog</tt> */
    public static final IRI INCLUDEDINDATACATALOG;

    /** <tt>http://schema.org/member</tt> */
    public static final IRI MEMBER;

    /** <tt>http://schema.org/recipient</tt> */
    public static final IRI RECIPIENT;

    /** <tt>http://schema.org/hasPart</tt> */
    public static final IRI HASPART;

    /** <tt>http://schema.org/isPartOf</tt> */
    public static final IRI ISPARTOF;

    /** <tt>http://schema.org/position</tt> */
    public static final IRI POSITION;

    /** <tt>http://schema.org/instrument</tt> */
    public static final IRI INSTRUMENT;

    /** <tt>http://schema.org/object</tt> */
    public static final IRI OBJECT;

    /** <tt>http://schema.org/location</tt> */
    public static final IRI LOCATION;

    /** <tt>http://schema.org/participant</tt> */
    public static final IRI PARTICIPANT;

    /** <tt>http://schema.org/identifier</tt> */
    public static final IRI IDENTIFIER;


    static {
        ValueFactory VF = SimpleValueFactory.getInstance();

        APIREFERENCE = VF.createIRI("http://schema.org/APIReference");
        ABOUTPAGE = VF.createIRI("http://schema.org/AboutPage");
        ACCEPTACTION = VF.createIRI("http://schema.org/AcceptAction");
        ACCOMMODATION = VF.createIRI("http://schema.org/Accommodation");
        ACCOUNTINGSERVICE = VF.createIRI("http://schema.org/AccountingService");
        ACHIEVEACTION = VF.createIRI("http://schema.org/AchieveAction");
        ACTION = VF.createIRI("http://schema.org/Action");
        ACTIONSTATUSTYPE = VF.createIRI("http://schema.org/ActionStatusType");
        ACTIVATEACTION = VF.createIRI("http://schema.org/ActivateAction");
        ADDACTION = VF.createIRI("http://schema.org/AddAction");
        ADMINISTRATIVEAREA = VF.createIRI("http://schema.org/AdministrativeArea");
        ADULTENTERTAINMENT = VF.createIRI("http://schema.org/AdultEntertainment");
        AGGREGATEOFFER = VF.createIRI("http://schema.org/AggregateOffer");
        AGGREGATERATING = VF.createIRI("http://schema.org/AggregateRating");
        AGREEACTION = VF.createIRI("http://schema.org/AgreeAction");
        AIRLINE = VF.createIRI("http://schema.org/Airline");
        AIRPORT = VF.createIRI("http://schema.org/Airport");
        ALIGNMENTOBJECT = VF.createIRI("http://schema.org/AlignmentObject");
        ALLOCATEACTION = VF.createIRI("http://schema.org/AllocateAction");
        AMUSEMENTPARK = VF.createIRI("http://schema.org/AmusementPark");
        ANIMALSHELTER = VF.createIRI("http://schema.org/AnimalShelter");
        ANSWER = VF.createIRI("http://schema.org/Answer");
        APARTMENT = VF.createIRI("http://schema.org/Apartment");
        APARTMENTCOMPLEX = VF.createIRI("http://schema.org/ApartmentComplex");
        APPENDACTION = VF.createIRI("http://schema.org/AppendAction");
        APPLYACTION = VF.createIRI("http://schema.org/ApplyAction");
        AQUARIUM = VF.createIRI("http://schema.org/Aquarium");
        ARRIVEACTION = VF.createIRI("http://schema.org/ArriveAction");
        ARTGALLERY = VF.createIRI("http://schema.org/ArtGallery");
        ARTICLE = VF.createIRI("http://schema.org/Article");
        ASKACTION = VF.createIRI("http://schema.org/AskAction");
        ASSESSACTION = VF.createIRI("http://schema.org/AssessAction");
        ASSIGNACTION = VF.createIRI("http://schema.org/AssignAction");
        ATTORNEY = VF.createIRI("http://schema.org/Attorney");
        AUDIENCE = VF.createIRI("http://schema.org/Audience");
        AUDIOOBJECT = VF.createIRI("http://schema.org/AudioObject");
        AUTHORIZEACTION = VF.createIRI("http://schema.org/AuthorizeAction");
        AUTOBODYSHOP = VF.createIRI("http://schema.org/AutoBodyShop");
        AUTODEALER = VF.createIRI("http://schema.org/AutoDealer");
        AUTOPARTSSTORE = VF.createIRI("http://schema.org/AutoPartsStore");
        AUTORENTAL = VF.createIRI("http://schema.org/AutoRental");
        AUTOREPAIR = VF.createIRI("http://schema.org/AutoRepair");
        AUTOWASH = VF.createIRI("http://schema.org/AutoWash");
        AUTOMATEDTELLER = VF.createIRI("http://schema.org/AutomatedTeller");
        AUTOMOTIVEBUSINESS = VF.createIRI("http://schema.org/AutomotiveBusiness");
        BAKERY = VF.createIRI("http://schema.org/Bakery");
        BANKACCOUNT = VF.createIRI("http://schema.org/BankAccount");
        BANKORCREDITUNION = VF.createIRI("http://schema.org/BankOrCreditUnion");
        BARORPUB = VF.createIRI("http://schema.org/BarOrPub");
        BARCODE = VF.createIRI("http://schema.org/Barcode");
        BEACH = VF.createIRI("http://schema.org/Beach");
        BEAUTYSALON = VF.createIRI("http://schema.org/BeautySalon");
        BEDANDBREAKFAST = VF.createIRI("http://schema.org/BedAndBreakfast");
        BEDDETAILS = VF.createIRI("http://schema.org/BedDetails");
        BEFRIENDACTION = VF.createIRI("http://schema.org/BefriendAction");
        BIKESTORE = VF.createIRI("http://schema.org/BikeStore");
        BLOG = VF.createIRI("http://schema.org/Blog");
        BLOGPOSTING = VF.createIRI("http://schema.org/BlogPosting");
        BOARDINGPOLICYTYPE = VF.createIRI("http://schema.org/BoardingPolicyType");
        BODYOFWATER = VF.createIRI("http://schema.org/BodyOfWater");
        BOOK = VF.createIRI("http://schema.org/Book");
        BOOKFORMATTYPE = VF.createIRI("http://schema.org/BookFormatType");
        BOOKSERIES = VF.createIRI("http://schema.org/BookSeries");
        BOOKSTORE = VF.createIRI("http://schema.org/BookStore");
        BOOKMARKACTION = VF.createIRI("http://schema.org/BookmarkAction");
        BOOLEAN = VF.createIRI("http://schema.org/Boolean");
        BORROWACTION = VF.createIRI("http://schema.org/BorrowAction");
        BOWLINGALLEY = VF.createIRI("http://schema.org/BowlingAlley");
        BRAND = VF.createIRI("http://schema.org/Brand");
        BREADCRUMBLIST = VF.createIRI("http://schema.org/BreadcrumbList");
        BREWERY = VF.createIRI("http://schema.org/Brewery");
        BRIDGE = VF.createIRI("http://schema.org/Bridge");
        BROADCASTCHANNEL = VF.createIRI("http://schema.org/BroadcastChannel");
        BROADCASTEVENT = VF.createIRI("http://schema.org/BroadcastEvent");
        BROADCASTSERVICE = VF.createIRI("http://schema.org/BroadcastService");
        BUDDHISTTEMPLE = VF.createIRI("http://schema.org/BuddhistTemple");
        BUSRESERVATION = VF.createIRI("http://schema.org/BusReservation");
        BUSSTATION = VF.createIRI("http://schema.org/BusStation");
        BUSSTOP = VF.createIRI("http://schema.org/BusStop");
        BUSTRIP = VF.createIRI("http://schema.org/BusTrip");
        BUSINESSAUDIENCE = VF.createIRI("http://schema.org/BusinessAudience");
        BUSINESSENTITYTYPE = VF.createIRI("http://schema.org/BusinessEntityType");
        BUSINESSEVENT = VF.createIRI("http://schema.org/BusinessEvent");
        BUSINESSFUNCTION = VF.createIRI("http://schema.org/BusinessFunction");
        BUYACTION = VF.createIRI("http://schema.org/BuyAction");
        CABLEORSATELLITESERVICE = VF.createIRI("http://schema.org/CableOrSatelliteService");
        CAFEORCOFFEESHOP = VF.createIRI("http://schema.org/CafeOrCoffeeShop");
        CAMPGROUND = VF.createIRI("http://schema.org/Campground");
        CAMPINGPITCH = VF.createIRI("http://schema.org/CampingPitch");
        CANAL = VF.createIRI("http://schema.org/Canal");
        CANCELACTION = VF.createIRI("http://schema.org/CancelAction");
        CAR = VF.createIRI("http://schema.org/Car");
        CASINO = VF.createIRI("http://schema.org/Casino");
        CATHOLICCHURCH = VF.createIRI("http://schema.org/CatholicChurch");
        CEMETERY = VF.createIRI("http://schema.org/Cemetery");
        CHECKACTION = VF.createIRI("http://schema.org/CheckAction");
        CHECKINACTION = VF.createIRI("http://schema.org/CheckInAction");
        CHECKOUTACTION = VF.createIRI("http://schema.org/CheckOutAction");
        CHECKOUTPAGE = VF.createIRI("http://schema.org/CheckoutPage");
        CHILDCARE = VF.createIRI("http://schema.org/ChildCare");
        CHILDRENSEVENT = VF.createIRI("http://schema.org/ChildrensEvent");
        CHOOSEACTION = VF.createIRI("http://schema.org/ChooseAction");
        CHURCH = VF.createIRI("http://schema.org/Church");
        CITY = VF.createIRI("http://schema.org/City");
        CITYHALL = VF.createIRI("http://schema.org/CityHall");
        CIVICSTRUCTURE = VF.createIRI("http://schema.org/CivicStructure");
        CLAIMREVIEW = VF.createIRI("http://schema.org/ClaimReview");
        CLIP = VF.createIRI("http://schema.org/Clip");
        CLOTHINGSTORE = VF.createIRI("http://schema.org/ClothingStore");
        CODE = VF.createIRI("http://schema.org/Code");
        COLLECTIONPAGE = VF.createIRI("http://schema.org/CollectionPage");
        COLLEGEORUNIVERSITY = VF.createIRI("http://schema.org/CollegeOrUniversity");
        COMEDYCLUB = VF.createIRI("http://schema.org/ComedyClub");
        COMEDYEVENT = VF.createIRI("http://schema.org/ComedyEvent");
        COMMENT = VF.createIRI("http://schema.org/Comment");
        COMMENTACTION = VF.createIRI("http://schema.org/CommentAction");
        COMMUNICATEACTION = VF.createIRI("http://schema.org/CommunicateAction");
        COMPOUNDPRICESPECIFICATION = VF.createIRI("http://schema.org/CompoundPriceSpecification");
        COMPUTERLANGUAGE = VF.createIRI("http://schema.org/ComputerLanguage");
        COMPUTERSTORE = VF.createIRI("http://schema.org/ComputerStore");
        CONFIRMACTION = VF.createIRI("http://schema.org/ConfirmAction");
        CONSUMEACTION = VF.createIRI("http://schema.org/ConsumeAction");
        CONTACTPAGE = VF.createIRI("http://schema.org/ContactPage");
        CONTACTPOINT = VF.createIRI("http://schema.org/ContactPoint");
        CONTACTPOINTOPTION = VF.createIRI("http://schema.org/ContactPointOption");
        CONTINENT = VF.createIRI("http://schema.org/Continent");
        CONTROLACTION = VF.createIRI("http://schema.org/ControlAction");
        CONVENIENCESTORE = VF.createIRI("http://schema.org/ConvenienceStore");
        CONVERSATION = VF.createIRI("http://schema.org/Conversation");
        COOKACTION = VF.createIRI("http://schema.org/CookAction");
        CORPORATION = VF.createIRI("http://schema.org/Corporation");
        COUNTRY = VF.createIRI("http://schema.org/Country");
        COURSE = VF.createIRI("http://schema.org/Course");
        COURSEINSTANCE = VF.createIRI("http://schema.org/CourseInstance");
        COURTHOUSE = VF.createIRI("http://schema.org/Courthouse");
        CREATEACTION = VF.createIRI("http://schema.org/CreateAction");
        CREATIVEWORK = VF.createIRI("http://schema.org/CreativeWork");
        CREATIVEWORKSEASON = VF.createIRI("http://schema.org/CreativeWorkSeason");
        CREATIVEWORKSERIES = VF.createIRI("http://schema.org/CreativeWorkSeries");
        CREDITCARD = VF.createIRI("http://schema.org/CreditCard");
        CREMATORIUM = VF.createIRI("http://schema.org/Crematorium");
        CURRENCYCONVERSIONSERVICE = VF.createIRI("http://schema.org/CurrencyConversionService");
        DANCEEVENT = VF.createIRI("http://schema.org/DanceEvent");
        DANCEGROUP = VF.createIRI("http://schema.org/DanceGroup");
        DATACATALOG = VF.createIRI("http://schema.org/DataCatalog");
        DATADOWNLOAD = VF.createIRI("http://schema.org/DataDownload");
        DATAFEED = VF.createIRI("http://schema.org/DataFeed");
        DATAFEEDITEM = VF.createIRI("http://schema.org/DataFeedItem");
        DATATYPE = VF.createIRI("http://schema.org/DataType");
        DATASET = VF.createIRI("http://schema.org/Dataset");
        DATE = VF.createIRI("http://schema.org/Date");
        DATETIME = VF.createIRI("http://schema.org/DateTime");
        DATEDMONEYSPECIFICATION = VF.createIRI("http://schema.org/DatedMoneySpecification");
        DAYOFWEEK = VF.createIRI("http://schema.org/DayOfWeek");
        DAYSPA = VF.createIRI("http://schema.org/DaySpa");
        DEACTIVATEACTION = VF.createIRI("http://schema.org/DeactivateAction");
        DEFENCEESTABLISHMENT = VF.createIRI("http://schema.org/DefenceEstablishment");
        DELETEACTION = VF.createIRI("http://schema.org/DeleteAction");
        DELIVERYCHARGESPECIFICATION = VF.createIRI("http://schema.org/DeliveryChargeSpecification");
        DELIVERYEVENT = VF.createIRI("http://schema.org/DeliveryEvent");
        DELIVERYMETHOD = VF.createIRI("http://schema.org/DeliveryMethod");
        DEMAND = VF.createIRI("http://schema.org/Demand");
        DENTIST = VF.createIRI("http://schema.org/Dentist");
        DEPARTACTION = VF.createIRI("http://schema.org/DepartAction");
        DEPARTMENTSTORE = VF.createIRI("http://schema.org/DepartmentStore");
        DEPOSITACCOUNT = VF.createIRI("http://schema.org/DepositAccount");
        DIGITALDOCUMENT = VF.createIRI("http://schema.org/DigitalDocument");
        DIGITALDOCUMENTPERMISSION = VF.createIRI("http://schema.org/DigitalDocumentPermission");
        DIGITALDOCUMENTPERMISSIONTYPE = VF.createIRI("http://schema.org/DigitalDocumentPermissionType");
        DISAGREEACTION = VF.createIRI("http://schema.org/DisagreeAction");
        DISCOVERACTION = VF.createIRI("http://schema.org/DiscoverAction");
        DISCUSSIONFORUMPOSTING = VF.createIRI("http://schema.org/DiscussionForumPosting");
        DISLIKEACTION = VF.createIRI("http://schema.org/DislikeAction");
        DISTANCE = VF.createIRI("http://schema.org/Distance");
        DONATEACTION = VF.createIRI("http://schema.org/DonateAction");
        DOWNLOADACTION = VF.createIRI("http://schema.org/DownloadAction");
        DRAWACTION = VF.createIRI("http://schema.org/DrawAction");
        DRINKACTION = VF.createIRI("http://schema.org/DrinkAction");
        DRIVEWHEELCONFIGURATIONVALUE = VF.createIRI("http://schema.org/DriveWheelConfigurationValue");
        DRYCLEANINGORLAUNDRY = VF.createIRI("http://schema.org/DryCleaningOrLaundry");
        DURATION = VF.createIRI("http://schema.org/Duration");
        EATACTION = VF.createIRI("http://schema.org/EatAction");
        EDUCATIONEVENT = VF.createIRI("http://schema.org/EducationEvent");
        EDUCATIONALAUDIENCE = VF.createIRI("http://schema.org/EducationalAudience");
        EDUCATIONALORGANIZATION = VF.createIRI("http://schema.org/EducationalOrganization");
        ELECTRICIAN = VF.createIRI("http://schema.org/Electrician");
        ELECTRONICSSTORE = VF.createIRI("http://schema.org/ElectronicsStore");
        ELEMENTARYSCHOOL = VF.createIRI("http://schema.org/ElementarySchool");
        EMAILMESSAGE = VF.createIRI("http://schema.org/EmailMessage");
        EMBASSY = VF.createIRI("http://schema.org/Embassy");
        EMERGENCYSERVICE = VF.createIRI("http://schema.org/EmergencyService");
        EMPLOYEEROLE = VF.createIRI("http://schema.org/EmployeeRole");
        EMPLOYMENTAGENCY = VF.createIRI("http://schema.org/EmploymentAgency");
        ENDORSEACTION = VF.createIRI("http://schema.org/EndorseAction");
        ENERGY = VF.createIRI("http://schema.org/Energy");
        ENGINESPECIFICATION = VF.createIRI("http://schema.org/EngineSpecification");
        ENTERTAINMENTBUSINESS = VF.createIRI("http://schema.org/EntertainmentBusiness");
        ENTRYPOINT = VF.createIRI("http://schema.org/EntryPoint");
        ENUMERATION = VF.createIRI("http://schema.org/Enumeration");
        EPISODE = VF.createIRI("http://schema.org/Episode");
        EVENT = VF.createIRI("http://schema.org/Event");
        EVENTRESERVATION = VF.createIRI("http://schema.org/EventReservation");
        EVENTSTATUSTYPE = VF.createIRI("http://schema.org/EventStatusType");
        EVENTVENUE = VF.createIRI("http://schema.org/EventVenue");
        EXERCISEACTION = VF.createIRI("http://schema.org/ExerciseAction");
        EXERCISEGYM = VF.createIRI("http://schema.org/ExerciseGym");
        EXHIBITIONEVENT = VF.createIRI("http://schema.org/ExhibitionEvent");
        FASTFOODRESTAURANT = VF.createIRI("http://schema.org/FastFoodRestaurant");
        FESTIVAL = VF.createIRI("http://schema.org/Festival");
        FILMACTION = VF.createIRI("http://schema.org/FilmAction");
        FINANCIALPRODUCT = VF.createIRI("http://schema.org/FinancialProduct");
        FINANCIALSERVICE = VF.createIRI("http://schema.org/FinancialService");
        FINDACTION = VF.createIRI("http://schema.org/FindAction");
        FIRESTATION = VF.createIRI("http://schema.org/FireStation");
        FLIGHT = VF.createIRI("http://schema.org/Flight");
        FLIGHTRESERVATION = VF.createIRI("http://schema.org/FlightReservation");
        FLOAT = VF.createIRI("http://schema.org/Float");
        FLORIST = VF.createIRI("http://schema.org/Florist");
        FOLLOWACTION = VF.createIRI("http://schema.org/FollowAction");
        FOODESTABLISHMENT = VF.createIRI("http://schema.org/FoodEstablishment");
        FOODESTABLISHMENTRESERVATION = VF.createIRI("http://schema.org/FoodEstablishmentReservation");
        FOODEVENT = VF.createIRI("http://schema.org/FoodEvent");
        FOODSERVICE = VF.createIRI("http://schema.org/FoodService");
        FURNITURESTORE = VF.createIRI("http://schema.org/FurnitureStore");
        GAME = VF.createIRI("http://schema.org/Game");
        GAMEPLAYMODE = VF.createIRI("http://schema.org/GamePlayMode");
        GAMESERVER = VF.createIRI("http://schema.org/GameServer");
        GAMESERVERSTATUS = VF.createIRI("http://schema.org/GameServerStatus");
        GARDENSTORE = VF.createIRI("http://schema.org/GardenStore");
        GASSTATION = VF.createIRI("http://schema.org/GasStation");
        GATEDRESIDENCECOMMUNITY = VF.createIRI("http://schema.org/GatedResidenceCommunity");
        GENDERTYPE = VF.createIRI("http://schema.org/GenderType");
        GENERALCONTRACTOR = VF.createIRI("http://schema.org/GeneralContractor");
        GEOCIRCLE = VF.createIRI("http://schema.org/GeoCircle");
        GEOCOORDINATES = VF.createIRI("http://schema.org/GeoCoordinates");
        GEOSHAPE = VF.createIRI("http://schema.org/GeoShape");
        GIVEACTION = VF.createIRI("http://schema.org/GiveAction");
        GOLFCOURSE = VF.createIRI("http://schema.org/GolfCourse");
        GOVERNMENTBUILDING = VF.createIRI("http://schema.org/GovernmentBuilding");
        GOVERNMENTOFFICE = VF.createIRI("http://schema.org/GovernmentOffice");
        GOVERNMENTORGANIZATION = VF.createIRI("http://schema.org/GovernmentOrganization");
        GOVERNMENTPERMIT = VF.createIRI("http://schema.org/GovernmentPermit");
        GOVERNMENTSERVICE = VF.createIRI("http://schema.org/GovernmentService");
        GROCERYSTORE = VF.createIRI("http://schema.org/GroceryStore");
        HVACBUSINESS = VF.createIRI("http://schema.org/HVACBusiness");
        HAIRSALON = VF.createIRI("http://schema.org/HairSalon");
        HARDWARESTORE = VF.createIRI("http://schema.org/HardwareStore");
        HEALTHANDBEAUTYBUSINESS = VF.createIRI("http://schema.org/HealthAndBeautyBusiness");
        HEALTHCLUB = VF.createIRI("http://schema.org/HealthClub");
        HIGHSCHOOL = VF.createIRI("http://schema.org/HighSchool");
        HINDUTEMPLE = VF.createIRI("http://schema.org/HinduTemple");
        HOBBYSHOP = VF.createIRI("http://schema.org/HobbyShop");
        HOMEANDCONSTRUCTIONBUSINESS = VF.createIRI("http://schema.org/HomeAndConstructionBusiness");
        HOMEGOODSSTORE = VF.createIRI("http://schema.org/HomeGoodsStore");
        HOSPITAL = VF.createIRI("http://schema.org/Hospital");
        HOSTEL = VF.createIRI("http://schema.org/Hostel");
        HOTEL = VF.createIRI("http://schema.org/Hotel");
        HOTELROOM = VF.createIRI("http://schema.org/HotelRoom");
        HOUSE = VF.createIRI("http://schema.org/House");
        HOUSEPAINTER = VF.createIRI("http://schema.org/HousePainter");
        HOWTO = VF.createIRI("http://schema.org/HowTo");
        HOWTODIRECTION = VF.createIRI("http://schema.org/HowToDirection");
        HOWTOITEM = VF.createIRI("http://schema.org/HowToItem");
        HOWTOSECTION = VF.createIRI("http://schema.org/HowToSection");
        HOWTOSTEP = VF.createIRI("http://schema.org/HowToStep");
        HOWTOSUPPLY = VF.createIRI("http://schema.org/HowToSupply");
        HOWTOTIP = VF.createIRI("http://schema.org/HowToTip");
        HOWTOTOOL = VF.createIRI("http://schema.org/HowToTool");
        ICECREAMSHOP = VF.createIRI("http://schema.org/IceCreamShop");
        IGNOREACTION = VF.createIRI("http://schema.org/IgnoreAction");
        IMAGEGALLERY = VF.createIRI("http://schema.org/ImageGallery");
        IMAGEOBJECT = VF.createIRI("http://schema.org/ImageObject");
        INDIVIDUALPRODUCT = VF.createIRI("http://schema.org/IndividualProduct");
        INFORMACTION = VF.createIRI("http://schema.org/InformAction");
        INSERTACTION = VF.createIRI("http://schema.org/InsertAction");
        INSTALLACTION = VF.createIRI("http://schema.org/InstallAction");
        INSURANCEAGENCY = VF.createIRI("http://schema.org/InsuranceAgency");
        INTANGIBLE = VF.createIRI("http://schema.org/Intangible");
        INTEGER = VF.createIRI("http://schema.org/Integer");
        INTERACTACTION = VF.createIRI("http://schema.org/InteractAction");
        INTERACTIONCOUNTER = VF.createIRI("http://schema.org/InteractionCounter");
        INTERNETCAFE = VF.createIRI("http://schema.org/InternetCafe");
        INVESTMENTORDEPOSIT = VF.createIRI("http://schema.org/InvestmentOrDeposit");
        INVITEACTION = VF.createIRI("http://schema.org/InviteAction");
        INVOICE = VF.createIRI("http://schema.org/Invoice");
        ITEMAVAILABILITY = VF.createIRI("http://schema.org/ItemAvailability");
        ITEMLIST = VF.createIRI("http://schema.org/ItemList");
        ITEMLISTORDERTYPE = VF.createIRI("http://schema.org/ItemListOrderType");
        ITEMPAGE = VF.createIRI("http://schema.org/ItemPage");
        JEWELRYSTORE = VF.createIRI("http://schema.org/JewelryStore");
        JOBPOSTING = VF.createIRI("http://schema.org/JobPosting");
        JOINACTION = VF.createIRI("http://schema.org/JoinAction");
        LAKEBODYOFWATER = VF.createIRI("http://schema.org/LakeBodyOfWater");
        LANDFORM = VF.createIRI("http://schema.org/Landform");
        LANDMARKSORHISTORICALBUILDINGS = VF.createIRI("http://schema.org/LandmarksOrHistoricalBuildings");
        LANGUAGE = VF.createIRI("http://schema.org/Language");
        LEAVEACTION = VF.createIRI("http://schema.org/LeaveAction");
        LEGALSERVICE = VF.createIRI("http://schema.org/LegalService");
        LEGISLATIVEBUILDING = VF.createIRI("http://schema.org/LegislativeBuilding");
        LENDACTION = VF.createIRI("http://schema.org/LendAction");
        LIBRARY = VF.createIRI("http://schema.org/Library");
        LIKEACTION = VF.createIRI("http://schema.org/LikeAction");
        LIQUORSTORE = VF.createIRI("http://schema.org/LiquorStore");
        LISTITEM = VF.createIRI("http://schema.org/ListItem");
        LISTENACTION = VF.createIRI("http://schema.org/ListenAction");
        LITERARYEVENT = VF.createIRI("http://schema.org/LiteraryEvent");
        LIVEBLOGPOSTING = VF.createIRI("http://schema.org/LiveBlogPosting");
        LOANORCREDIT = VF.createIRI("http://schema.org/LoanOrCredit");
        LOCALBUSINESS = VF.createIRI("http://schema.org/LocalBusiness");
        LOCATIONFEATURESPECIFICATION = VF.createIRI("http://schema.org/LocationFeatureSpecification");
        LOCKERDELIVERY = VF.createIRI("http://schema.org/LockerDelivery");
        LOCKSMITH = VF.createIRI("http://schema.org/Locksmith");
        LODGINGBUSINESS = VF.createIRI("http://schema.org/LodgingBusiness");
        LODGINGRESERVATION = VF.createIRI("http://schema.org/LodgingReservation");
        LOSEACTION = VF.createIRI("http://schema.org/LoseAction");
        MAP = VF.createIRI("http://schema.org/Map");
        MAPCATEGORYTYPE = VF.createIRI("http://schema.org/MapCategoryType");
        MARRYACTION = VF.createIRI("http://schema.org/MarryAction");
        MASS = VF.createIRI("http://schema.org/Mass");
        MEDIAOBJECT = VF.createIRI("http://schema.org/MediaObject");
        MEDICALORGANIZATION = VF.createIRI("http://schema.org/MedicalOrganization");
        MEETINGROOM = VF.createIRI("http://schema.org/MeetingRoom");
        MENSCLOTHINGSTORE = VF.createIRI("http://schema.org/MensClothingStore");
        MENU = VF.createIRI("http://schema.org/Menu");
        MENUITEM = VF.createIRI("http://schema.org/MenuItem");
        MENUSECTION = VF.createIRI("http://schema.org/MenuSection");
        MESSAGE = VF.createIRI("http://schema.org/Message");
        MIDDLESCHOOL = VF.createIRI("http://schema.org/MiddleSchool");
        MOBILEAPPLICATION = VF.createIRI("http://schema.org/MobileApplication");
        MOBILEPHONESTORE = VF.createIRI("http://schema.org/MobilePhoneStore");
        MONETARYAMOUNT = VF.createIRI("http://schema.org/MonetaryAmount");
        MOSQUE = VF.createIRI("http://schema.org/Mosque");
        MOTEL = VF.createIRI("http://schema.org/Motel");
        MOTORCYCLEDEALER = VF.createIRI("http://schema.org/MotorcycleDealer");
        MOTORCYCLEREPAIR = VF.createIRI("http://schema.org/MotorcycleRepair");
        MOUNTAIN = VF.createIRI("http://schema.org/Mountain");
        MOVEACTION = VF.createIRI("http://schema.org/MoveAction");
        MOVIE = VF.createIRI("http://schema.org/Movie");
        MOVIECLIP = VF.createIRI("http://schema.org/MovieClip");
        MOVIERENTALSTORE = VF.createIRI("http://schema.org/MovieRentalStore");
        MOVIESERIES = VF.createIRI("http://schema.org/MovieSeries");
        MOVIETHEATER = VF.createIRI("http://schema.org/MovieTheater");
        MOVINGCOMPANY = VF.createIRI("http://schema.org/MovingCompany");
        MUSEUM = VF.createIRI("http://schema.org/Museum");
        MUSICALBUM = VF.createIRI("http://schema.org/MusicAlbum");
        MUSICALBUMPRODUCTIONTYPE = VF.createIRI("http://schema.org/MusicAlbumProductionType");
        MUSICALBUMRELEASETYPE = VF.createIRI("http://schema.org/MusicAlbumReleaseType");
        MUSICCOMPOSITION = VF.createIRI("http://schema.org/MusicComposition");
        MUSICEVENT = VF.createIRI("http://schema.org/MusicEvent");
        MUSICGROUP = VF.createIRI("http://schema.org/MusicGroup");
        MUSICPLAYLIST = VF.createIRI("http://schema.org/MusicPlaylist");
        MUSICRECORDING = VF.createIRI("http://schema.org/MusicRecording");
        MUSICRELEASE = VF.createIRI("http://schema.org/MusicRelease");
        MUSICRELEASEFORMATTYPE = VF.createIRI("http://schema.org/MusicReleaseFormatType");
        MUSICSTORE = VF.createIRI("http://schema.org/MusicStore");
        MUSICVENUE = VF.createIRI("http://schema.org/MusicVenue");
        MUSICVIDEOOBJECT = VF.createIRI("http://schema.org/MusicVideoObject");
        NGO = VF.createIRI("http://schema.org/NGO");
        NAILSALON = VF.createIRI("http://schema.org/NailSalon");
        NEWSARTICLE = VF.createIRI("http://schema.org/NewsArticle");
        NIGHTCLUB = VF.createIRI("http://schema.org/NightClub");
        NOTARY = VF.createIRI("http://schema.org/Notary");
        NOTEDIGITALDOCUMENT = VF.createIRI("http://schema.org/NoteDigitalDocument");
        NUMBER = VF.createIRI("http://schema.org/Number");
        NUTRITIONINFORMATION = VF.createIRI("http://schema.org/NutritionInformation");
        OCEANBODYOFWATER = VF.createIRI("http://schema.org/OceanBodyOfWater");
        OFFER = VF.createIRI("http://schema.org/Offer");
        OFFERCATALOG = VF.createIRI("http://schema.org/OfferCatalog");
        OFFERITEMCONDITION = VF.createIRI("http://schema.org/OfferItemCondition");
        OFFICEEQUIPMENTSTORE = VF.createIRI("http://schema.org/OfficeEquipmentStore");
        ONDEMANDEVENT = VF.createIRI("http://schema.org/OnDemandEvent");
        OPENINGHOURSSPECIFICATION = VF.createIRI("http://schema.org/OpeningHoursSpecification");
        ORDER = VF.createIRI("http://schema.org/Order");
        ORDERACTION = VF.createIRI("http://schema.org/OrderAction");
        ORDERITEM = VF.createIRI("http://schema.org/OrderItem");
        ORDERSTATUS = VF.createIRI("http://schema.org/OrderStatus");
        ORGANIZATION = VF.createIRI("http://schema.org/Organization");
        ORGANIZATIONROLE = VF.createIRI("http://schema.org/OrganizationRole");
        ORGANIZEACTION = VF.createIRI("http://schema.org/OrganizeAction");
        OUTLETSTORE = VF.createIRI("http://schema.org/OutletStore");
        OWNERSHIPINFO = VF.createIRI("http://schema.org/OwnershipInfo");
        PAINTACTION = VF.createIRI("http://schema.org/PaintAction");
        PAINTING = VF.createIRI("http://schema.org/Painting");
        PARCELDELIVERY = VF.createIRI("http://schema.org/ParcelDelivery");
        PARCELSERVICE = VF.createIRI("http://schema.org/ParcelService");
        PARENTAUDIENCE = VF.createIRI("http://schema.org/ParentAudience");
        PARK = VF.createIRI("http://schema.org/Park");
        PARKINGFACILITY = VF.createIRI("http://schema.org/ParkingFacility");
        PAWNSHOP = VF.createIRI("http://schema.org/PawnShop");
        PAYACTION = VF.createIRI("http://schema.org/PayAction");
        PAYMENTCARD = VF.createIRI("http://schema.org/PaymentCard");
        PAYMENTCHARGESPECIFICATION = VF.createIRI("http://schema.org/PaymentChargeSpecification");
        PAYMENTMETHOD = VF.createIRI("http://schema.org/PaymentMethod");
        PAYMENTSERVICE = VF.createIRI("http://schema.org/PaymentService");
        PAYMENTSTATUSTYPE = VF.createIRI("http://schema.org/PaymentStatusType");
        PEOPLEAUDIENCE = VF.createIRI("http://schema.org/PeopleAudience");
        PERFORMACTION = VF.createIRI("http://schema.org/PerformAction");
        PERFORMANCEROLE = VF.createIRI("http://schema.org/PerformanceRole");
        PERFORMINGARTSTHEATER = VF.createIRI("http://schema.org/PerformingArtsTheater");
        PERFORMINGGROUP = VF.createIRI("http://schema.org/PerformingGroup");
        PERIODICAL = VF.createIRI("http://schema.org/Periodical");
        PERMIT = VF.createIRI("http://schema.org/Permit");
        PERSON = VF.createIRI("http://schema.org/Person");
        PETSTORE = VF.createIRI("http://schema.org/PetStore");
        PHARMACY = VF.createIRI("http://schema.org/Pharmacy");
        PHOTOGRAPH = VF.createIRI("http://schema.org/Photograph");
        PHOTOGRAPHACTION = VF.createIRI("http://schema.org/PhotographAction");
        PHYSICIAN = VF.createIRI("http://schema.org/Physician");
        PLACE = VF.createIRI("http://schema.org/Place");
        PLACEOFWORSHIP = VF.createIRI("http://schema.org/PlaceOfWorship");
        PLANACTION = VF.createIRI("http://schema.org/PlanAction");
        PLAYACTION = VF.createIRI("http://schema.org/PlayAction");
        PLAYGROUND = VF.createIRI("http://schema.org/Playground");
        PLUMBER = VF.createIRI("http://schema.org/Plumber");
        POLICESTATION = VF.createIRI("http://schema.org/PoliceStation");
        POND = VF.createIRI("http://schema.org/Pond");
        POSTOFFICE = VF.createIRI("http://schema.org/PostOffice");
        POSTALADDRESS = VF.createIRI("http://schema.org/PostalAddress");
        PREPENDACTION = VF.createIRI("http://schema.org/PrependAction");
        PRESCHOOL = VF.createIRI("http://schema.org/Preschool");
        PRESENTATIONDIGITALDOCUMENT = VF.createIRI("http://schema.org/PresentationDigitalDocument");
        PRICESPECIFICATION = VF.createIRI("http://schema.org/PriceSpecification");
        PRODUCT = VF.createIRI("http://schema.org/Product");
        PRODUCTMODEL = VF.createIRI("http://schema.org/ProductModel");
        PROFESSIONALSERVICE = VF.createIRI("http://schema.org/ProfessionalService");
        PROFILEPAGE = VF.createIRI("http://schema.org/ProfilePage");
        PROGRAMMEMBERSHIP = VF.createIRI("http://schema.org/ProgramMembership");
        PROPERTYVALUE = VF.createIRI("http://schema.org/PropertyValue");
        PROPERTYVALUESPECIFICATION = VF.createIRI("http://schema.org/PropertyValueSpecification");
        PUBLICSWIMMINGPOOL = VF.createIRI("http://schema.org/PublicSwimmingPool");
        PUBLICATIONEVENT = VF.createIRI("http://schema.org/PublicationEvent");
        PUBLICATIONISSUE = VF.createIRI("http://schema.org/PublicationIssue");
        PUBLICATIONVOLUME = VF.createIRI("http://schema.org/PublicationVolume");
        QAPAGE = VF.createIRI("http://schema.org/QAPage");
        QUALITATIVEVALUE = VF.createIRI("http://schema.org/QualitativeValue");
        QUANTITATIVEVALUE = VF.createIRI("http://schema.org/QuantitativeValue");
        QUANTITY = VF.createIRI("http://schema.org/Quantity");
        QUESTION = VF.createIRI("http://schema.org/Question");
        QUOTEACTION = VF.createIRI("http://schema.org/QuoteAction");
        RVPARK = VF.createIRI("http://schema.org/RVPark");
        RADIOCHANNEL = VF.createIRI("http://schema.org/RadioChannel");
        RADIOCLIP = VF.createIRI("http://schema.org/RadioClip");
        RADIOEPISODE = VF.createIRI("http://schema.org/RadioEpisode");
        RADIOSEASON = VF.createIRI("http://schema.org/RadioSeason");
        RADIOSERIES = VF.createIRI("http://schema.org/RadioSeries");
        RADIOSTATION = VF.createIRI("http://schema.org/RadioStation");
        RATING = VF.createIRI("http://schema.org/Rating");
        REACTACTION = VF.createIRI("http://schema.org/ReactAction");
        READACTION = VF.createIRI("http://schema.org/ReadAction");
        REALESTATEAGENT = VF.createIRI("http://schema.org/RealEstateAgent");
        RECEIVEACTION = VF.createIRI("http://schema.org/ReceiveAction");
        RECIPE = VF.createIRI("http://schema.org/Recipe");
        RECYCLINGCENTER = VF.createIRI("http://schema.org/RecyclingCenter");
        REGISTERACTION = VF.createIRI("http://schema.org/RegisterAction");
        REJECTACTION = VF.createIRI("http://schema.org/RejectAction");
        RENTACTION = VF.createIRI("http://schema.org/RentAction");
        RENTALCARRESERVATION = VF.createIRI("http://schema.org/RentalCarReservation");
        REPLACEACTION = VF.createIRI("http://schema.org/ReplaceAction");
        REPLYACTION = VF.createIRI("http://schema.org/ReplyAction");
        REPORT = VF.createIRI("http://schema.org/Report");
        RESERVATION = VF.createIRI("http://schema.org/Reservation");
        RESERVATIONPACKAGE = VF.createIRI("http://schema.org/ReservationPackage");
        RESERVATIONSTATUSTYPE = VF.createIRI("http://schema.org/ReservationStatusType");
        RESERVEACTION = VF.createIRI("http://schema.org/ReserveAction");
        RESERVOIR = VF.createIRI("http://schema.org/Reservoir");
        RESIDENCE = VF.createIRI("http://schema.org/Residence");
        RESORT = VF.createIRI("http://schema.org/Resort");
        RESTAURANT = VF.createIRI("http://schema.org/Restaurant");
        RESTRICTEDDIET = VF.createIRI("http://schema.org/RestrictedDiet");
        RESUMEACTION = VF.createIRI("http://schema.org/ResumeAction");
        RETURNACTION = VF.createIRI("http://schema.org/ReturnAction");
        REVIEW = VF.createIRI("http://schema.org/Review");
        REVIEWACTION = VF.createIRI("http://schema.org/ReviewAction");
        RIVERBODYOFWATER = VF.createIRI("http://schema.org/RiverBodyOfWater");
        ROLE = VF.createIRI("http://schema.org/Role");
        ROOFINGCONTRACTOR = VF.createIRI("http://schema.org/RoofingContractor");
        ROOM = VF.createIRI("http://schema.org/Room");
        RSVPACTION = VF.createIRI("http://schema.org/RsvpAction");
        RSVPRESPONSETYPE = VF.createIRI("http://schema.org/RsvpResponseType");
        SALEEVENT = VF.createIRI("http://schema.org/SaleEvent");
        SCHEDULEACTION = VF.createIRI("http://schema.org/ScheduleAction");
        SCHOLARLYARTICLE = VF.createIRI("http://schema.org/ScholarlyArticle");
        SCHOOL = VF.createIRI("http://schema.org/School");
        SCREENINGEVENT = VF.createIRI("http://schema.org/ScreeningEvent");
        SCULPTURE = VF.createIRI("http://schema.org/Sculpture");
        SEABODYOFWATER = VF.createIRI("http://schema.org/SeaBodyOfWater");
        SEARCHACTION = VF.createIRI("http://schema.org/SearchAction");
        SEARCHRESULTSPAGE = VF.createIRI("http://schema.org/SearchResultsPage");
        SEASON = VF.createIRI("http://schema.org/Season");
        SEAT = VF.createIRI("http://schema.org/Seat");
        SELFSTORAGE = VF.createIRI("http://schema.org/SelfStorage");
        SELLACTION = VF.createIRI("http://schema.org/SellAction");
        SENDACTION = VF.createIRI("http://schema.org/SendAction");
        SERIES = VF.createIRI("http://schema.org/Series");
        SERVICE = VF.createIRI("http://schema.org/Service");
        SERVICECHANNEL = VF.createIRI("http://schema.org/ServiceChannel");
        SHAREACTION = VF.createIRI("http://schema.org/ShareAction");
        SHOESTORE = VF.createIRI("http://schema.org/ShoeStore");
        SHOPPINGCENTER = VF.createIRI("http://schema.org/ShoppingCenter");
        SINGLEFAMILYRESIDENCE = VF.createIRI("http://schema.org/SingleFamilyResidence");
        SITENAVIGATIONELEMENT = VF.createIRI("http://schema.org/SiteNavigationElement");
        SKIRESORT = VF.createIRI("http://schema.org/SkiResort");
        SOCIALEVENT = VF.createIRI("http://schema.org/SocialEvent");
        SOCIALMEDIAPOSTING = VF.createIRI("http://schema.org/SocialMediaPosting");
        SOFTWAREAPPLICATION = VF.createIRI("http://schema.org/SoftwareApplication");
        SOFTWARESOURCECODE = VF.createIRI("http://schema.org/SoftwareSourceCode");
        SOMEPRODUCTS = VF.createIRI("http://schema.org/SomeProducts");
        SPECIALTY = VF.createIRI("http://schema.org/Specialty");
        SPORTINGGOODSSTORE = VF.createIRI("http://schema.org/SportingGoodsStore");
        SPORTSACTIVITYLOCATION = VF.createIRI("http://schema.org/SportsActivityLocation");
        SPORTSCLUB = VF.createIRI("http://schema.org/SportsClub");
        SPORTSEVENT = VF.createIRI("http://schema.org/SportsEvent");
        SPORTSORGANIZATION = VF.createIRI("http://schema.org/SportsOrganization");
        SPORTSTEAM = VF.createIRI("http://schema.org/SportsTeam");
        SPREADSHEETDIGITALDOCUMENT = VF.createIRI("http://schema.org/SpreadsheetDigitalDocument");
        STADIUMORARENA = VF.createIRI("http://schema.org/StadiumOrArena");
        STATE = VF.createIRI("http://schema.org/State");
        STEERINGPOSITIONVALUE = VF.createIRI("http://schema.org/SteeringPositionValue");
        STORE = VF.createIRI("http://schema.org/Store");
        STRUCTUREDVALUE = VF.createIRI("http://schema.org/StructuredValue");
        SUBSCRIBEACTION = VF.createIRI("http://schema.org/SubscribeAction");
        SUBWAYSTATION = VF.createIRI("http://schema.org/SubwayStation");
        SUITE = VF.createIRI("http://schema.org/Suite");
        SUSPENDACTION = VF.createIRI("http://schema.org/SuspendAction");
        SYNAGOGUE = VF.createIRI("http://schema.org/Synagogue");
        TVCLIP = VF.createIRI("http://schema.org/TVClip");
        TVEPISODE = VF.createIRI("http://schema.org/TVEpisode");
        TVSEASON = VF.createIRI("http://schema.org/TVSeason");
        TVSERIES = VF.createIRI("http://schema.org/TVSeries");
        TABLE = VF.createIRI("http://schema.org/Table");
        TAKEACTION = VF.createIRI("http://schema.org/TakeAction");
        TATTOOPARLOR = VF.createIRI("http://schema.org/TattooParlor");
        TAXI = VF.createIRI("http://schema.org/Taxi");
        TAXIRESERVATION = VF.createIRI("http://schema.org/TaxiReservation");
        TAXISERVICE = VF.createIRI("http://schema.org/TaxiService");
        TAXISTAND = VF.createIRI("http://schema.org/TaxiStand");
        TECHARTICLE = VF.createIRI("http://schema.org/TechArticle");
        TELEVISIONCHANNEL = VF.createIRI("http://schema.org/TelevisionChannel");
        TELEVISIONSTATION = VF.createIRI("http://schema.org/TelevisionStation");
        TENNISCOMPLEX = VF.createIRI("http://schema.org/TennisComplex");
        TEXT = VF.createIRI("http://schema.org/Text");
        TEXTDIGITALDOCUMENT = VF.createIRI("http://schema.org/TextDigitalDocument");
        THEATEREVENT = VF.createIRI("http://schema.org/TheaterEvent");
        THEATERGROUP = VF.createIRI("http://schema.org/TheaterGroup");
        THING = VF.createIRI("http://schema.org/Thing");
        TICKET = VF.createIRI("http://schema.org/Ticket");
        TIEACTION = VF.createIRI("http://schema.org/TieAction");
        TIME = VF.createIRI("http://schema.org/Time");
        TIPACTION = VF.createIRI("http://schema.org/TipAction");
        TIRESHOP = VF.createIRI("http://schema.org/TireShop");
        TOURISTATTRACTION = VF.createIRI("http://schema.org/TouristAttraction");
        TOURISTINFORMATIONCENTER = VF.createIRI("http://schema.org/TouristInformationCenter");
        TOYSTORE = VF.createIRI("http://schema.org/ToyStore");
        TRACKACTION = VF.createIRI("http://schema.org/TrackAction");
        TRADEACTION = VF.createIRI("http://schema.org/TradeAction");
        TRAINRESERVATION = VF.createIRI("http://schema.org/TrainReservation");
        TRAINSTATION = VF.createIRI("http://schema.org/TrainStation");
        TRAINTRIP = VF.createIRI("http://schema.org/TrainTrip");
        TRANSFERACTION = VF.createIRI("http://schema.org/TransferAction");
        TRAVELACTION = VF.createIRI("http://schema.org/TravelAction");
        TRAVELAGENCY = VF.createIRI("http://schema.org/TravelAgency");
        TRIP = VF.createIRI("http://schema.org/Trip");
        TYPEANDQUANTITYNODE = VF.createIRI("http://schema.org/TypeAndQuantityNode");
        URL = VF.createIRI("http://schema.org/URL");
        UNREGISTERACTION = VF.createIRI("http://schema.org/UnRegisterAction");
        UNITPRICESPECIFICATION = VF.createIRI("http://schema.org/UnitPriceSpecification");
        UPDATEACTION = VF.createIRI("http://schema.org/UpdateAction");
        USEACTION = VF.createIRI("http://schema.org/UseAction");
        USERBLOCKS = VF.createIRI("http://schema.org/UserBlocks");
        USERCHECKINS = VF.createIRI("http://schema.org/UserCheckins");
        USERCOMMENTS = VF.createIRI("http://schema.org/UserComments");
        USERDOWNLOADS = VF.createIRI("http://schema.org/UserDownloads");
        USERINTERACTION = VF.createIRI("http://schema.org/UserInteraction");
        USERLIKES = VF.createIRI("http://schema.org/UserLikes");
        USERPAGEVISITS = VF.createIRI("http://schema.org/UserPageVisits");
        USERPLAYS = VF.createIRI("http://schema.org/UserPlays");
        USERPLUSONES = VF.createIRI("http://schema.org/UserPlusOnes");
        USERTWEETS = VF.createIRI("http://schema.org/UserTweets");
        VEHICLE = VF.createIRI("http://schema.org/Vehicle");
        VIDEOGALLERY = VF.createIRI("http://schema.org/VideoGallery");
        VIDEOGAME = VF.createIRI("http://schema.org/VideoGame");
        VIDEOGAMECLIP = VF.createIRI("http://schema.org/VideoGameClip");
        VIDEOGAMESERIES = VF.createIRI("http://schema.org/VideoGameSeries");
        VIDEOOBJECT = VF.createIRI("http://schema.org/VideoObject");
        VIEWACTION = VF.createIRI("http://schema.org/ViewAction");
        VISUALARTSEVENT = VF.createIRI("http://schema.org/VisualArtsEvent");
        VISUALARTWORK = VF.createIRI("http://schema.org/VisualArtwork");
        VOLCANO = VF.createIRI("http://schema.org/Volcano");
        VOTEACTION = VF.createIRI("http://schema.org/VoteAction");
        WPADBLOCK = VF.createIRI("http://schema.org/WPAdBlock");
        WPFOOTER = VF.createIRI("http://schema.org/WPFooter");
        WPHEADER = VF.createIRI("http://schema.org/WPHeader");
        WPSIDEBAR = VF.createIRI("http://schema.org/WPSideBar");
        WANTACTION = VF.createIRI("http://schema.org/WantAction");
        WARRANTYPROMISE = VF.createIRI("http://schema.org/WarrantyPromise");
        WARRANTYSCOPE = VF.createIRI("http://schema.org/WarrantyScope");
        WATCHACTION = VF.createIRI("http://schema.org/WatchAction");
        WATERFALL = VF.createIRI("http://schema.org/Waterfall");
        WEARACTION = VF.createIRI("http://schema.org/WearAction");
        WEBAPPLICATION = VF.createIRI("http://schema.org/WebApplication");
        WEBPAGE = VF.createIRI("http://schema.org/WebPage");
        WEBPAGEELEMENT = VF.createIRI("http://schema.org/WebPageElement");
        WEBSITE = VF.createIRI("http://schema.org/WebSite");
        WHOLESALESTORE = VF.createIRI("http://schema.org/WholesaleStore");
        WINACTION = VF.createIRI("http://schema.org/WinAction");
        WINERY = VF.createIRI("http://schema.org/Winery");
        WRITEACTION = VF.createIRI("http://schema.org/WriteAction");
        ZOO = VF.createIRI("http://schema.org/Zoo");
        ACCEPTEDANSWER = VF.createIRI("http://schema.org/acceptedAnswer");
        ACCEPTEDOFFER = VF.createIRI("http://schema.org/acceptedOffer");
        ACCEPTEDPAYMENTMETHOD = VF.createIRI("http://schema.org/acceptedPaymentMethod");
        ACCEPTSRESERVATIONS = VF.createIRI("http://schema.org/acceptsReservations");
        ACCESSCODE = VF.createIRI("http://schema.org/accessCode");
        ACCESSMODE = VF.createIRI("http://schema.org/accessMode");
        ACCESSMODESUFFICIENT = VF.createIRI("http://schema.org/accessModeSufficient");
        ACCESSIBILITYAPI = VF.createIRI("http://schema.org/accessibilityAPI");
        ACCESSIBILITYCONTROL = VF.createIRI("http://schema.org/accessibilityControl");
        ACCESSIBILITYFEATURE = VF.createIRI("http://schema.org/accessibilityFeature");
        ACCESSIBILITYHAZARD = VF.createIRI("http://schema.org/accessibilityHazard");
        ACCESSIBILITYSUMMARY = VF.createIRI("http://schema.org/accessibilitySummary");
        ACCOUNTID = VF.createIRI("http://schema.org/accountId");
        ACCOUNTABLEPERSON = VF.createIRI("http://schema.org/accountablePerson");
        ACQUIREDFROM = VF.createIRI("http://schema.org/acquiredFrom");
        ACTIONPLATFORM = VF.createIRI("http://schema.org/actionPlatform");
        ACTIONSTATUS = VF.createIRI("http://schema.org/actionStatus");
        ACTORS = VF.createIRI("http://schema.org/actors");
        ADDON = VF.createIRI("http://schema.org/addOn");
        ADDITIONALNAME = VF.createIRI("http://schema.org/additionalName");
        ADDITIONALNUMBEROFGUESTS = VF.createIRI("http://schema.org/additionalNumberOfGuests");
        ADDITIONALPROPERTY = VF.createIRI("http://schema.org/additionalProperty");
        ADDITIONALTYPE = VF.createIRI("http://schema.org/additionalType");
        ADDRESS = VF.createIRI("http://schema.org/address");
        ADDRESSCOUNTRY = VF.createIRI("http://schema.org/addressCountry");
        ADDRESSLOCALITY = VF.createIRI("http://schema.org/addressLocality");
        ADDRESSREGION = VF.createIRI("http://schema.org/addressRegion");
        ADVANCEBOOKINGREQUIREMENT = VF.createIRI("http://schema.org/advanceBookingRequirement");
        AFFILIATION = VF.createIRI("http://schema.org/affiliation");
        AFTERMEDIA = VF.createIRI("http://schema.org/afterMedia");
        AGENT = VF.createIRI("http://schema.org/agent");
        HAS_AGGREGATERATING = VF.createIRI("http://schema.org/aggregateRating");
        AIRCRAFT = VF.createIRI("http://schema.org/aircraft");
        ALBUMPRODUCTIONTYPE = VF.createIRI("http://schema.org/albumProductionType");
        ALBUMRELEASETYPE = VF.createIRI("http://schema.org/albumReleaseType");
        ALBUMS = VF.createIRI("http://schema.org/albums");
        ALIGNMENTTYPE = VF.createIRI("http://schema.org/alignmentType");
        ALTERNATENAME = VF.createIRI("http://schema.org/alternateName");
        ALTERNATIVEHEADLINE = VF.createIRI("http://schema.org/alternativeHeadline");
        AMENITYFEATURE = VF.createIRI("http://schema.org/amenityFeature");
        AMOUNT = VF.createIRI("http://schema.org/amount");
        AMOUNTOFTHISGOOD = VF.createIRI("http://schema.org/amountOfThisGood");
        ANNUALPERCENTAGERATE = VF.createIRI("http://schema.org/annualPercentageRate");
        ANSWERCOUNT = VF.createIRI("http://schema.org/answerCount");
        APPLICATION = VF.createIRI("http://schema.org/application");
        APPLICATIONCATEGORY = VF.createIRI("http://schema.org/applicationCategory");
        APPLICATIONSUBCATEGORY = VF.createIRI("http://schema.org/applicationSubCategory");
        APPLICATIONSUITE = VF.createIRI("http://schema.org/applicationSuite");
        APPLIESTODELIVERYMETHOD = VF.createIRI("http://schema.org/appliesToDeliveryMethod");
        APPLIESTOPAYMENTMETHOD = VF.createIRI("http://schema.org/appliesToPaymentMethod");
        AREA = VF.createIRI("http://schema.org/area");
        ARRIVALAIRPORT = VF.createIRI("http://schema.org/arrivalAirport");
        ARRIVALBUSSTOP = VF.createIRI("http://schema.org/arrivalBusStop");
        ARRIVALGATE = VF.createIRI("http://schema.org/arrivalGate");
        ARRIVALPLATFORM = VF.createIRI("http://schema.org/arrivalPlatform");
        ARRIVALSTATION = VF.createIRI("http://schema.org/arrivalStation");
        ARRIVALTERMINAL = VF.createIRI("http://schema.org/arrivalTerminal");
        ARRIVALTIME = VF.createIRI("http://schema.org/arrivalTime");
        ARTEDITION = VF.createIRI("http://schema.org/artEdition");
        ARTMEDIUM = VF.createIRI("http://schema.org/artMedium");
        ARTFORM = VF.createIRI("http://schema.org/artform");
        ARTICLEBODY = VF.createIRI("http://schema.org/articleBody");
        ARTICLESECTION = VF.createIRI("http://schema.org/articleSection");
        ASSEMBLY = VF.createIRI("http://schema.org/assembly");
        ASSEMBLYVERSION = VF.createIRI("http://schema.org/assemblyVersion");
        ASSOCIATEDARTICLE = VF.createIRI("http://schema.org/associatedArticle");
        ASSOCIATEDMEDIA = VF.createIRI("http://schema.org/associatedMedia");
        ATHLETE = VF.createIRI("http://schema.org/athlete");
        ATTENDEES = VF.createIRI("http://schema.org/attendees");
        AUDIENCETYPE = VF.createIRI("http://schema.org/audienceType");
        AUDIO = VF.createIRI("http://schema.org/audio");
        AUTHOR = VF.createIRI("http://schema.org/author");
        AVAILABILITY = VF.createIRI("http://schema.org/availability");
        AVAILABILITYENDS = VF.createIRI("http://schema.org/availabilityEnds");
        AVAILABILITYSTARTS = VF.createIRI("http://schema.org/availabilityStarts");
        AVAILABLEATORFROM = VF.createIRI("http://schema.org/availableAtOrFrom");
        AVAILABLECHANNEL = VF.createIRI("http://schema.org/availableChannel");
        AVAILABLEDELIVERYMETHOD = VF.createIRI("http://schema.org/availableDeliveryMethod");
        AVAILABLEFROM = VF.createIRI("http://schema.org/availableFrom");
        AVAILABLELANGUAGE = VF.createIRI("http://schema.org/availableLanguage");
        AVAILABLETHROUGH = VF.createIRI("http://schema.org/availableThrough");
        AWARDS = VF.createIRI("http://schema.org/awards");
        AWAYTEAM = VF.createIRI("http://schema.org/awayTeam");
        BASESALARY = VF.createIRI("http://schema.org/baseSalary");
        BCCRECIPIENT = VF.createIRI("http://schema.org/bccRecipient");
        BED = VF.createIRI("http://schema.org/bed");
        BEFOREMEDIA = VF.createIRI("http://schema.org/beforeMedia");
        BENEFITS = VF.createIRI("http://schema.org/benefits");
        BESTRATING = VF.createIRI("http://schema.org/bestRating");
        BILLINGADDRESS = VF.createIRI("http://schema.org/billingAddress");
        BILLINGINCREMENT = VF.createIRI("http://schema.org/billingIncrement");
        BILLINGPERIOD = VF.createIRI("http://schema.org/billingPeriod");
        BIRTHDATE = VF.createIRI("http://schema.org/birthDate");
        BIRTHPLACE = VF.createIRI("http://schema.org/birthPlace");
        BITRATE = VF.createIRI("http://schema.org/bitrate");
        BLOGPOSTS = VF.createIRI("http://schema.org/blogPosts");
        BOARDINGGROUP = VF.createIRI("http://schema.org/boardingGroup");
        BOARDINGPOLICY = VF.createIRI("http://schema.org/boardingPolicy");
        BOOKEDITION = VF.createIRI("http://schema.org/bookEdition");
        BOOKFORMAT = VF.createIRI("http://schema.org/bookFormat");
        BOOKINGAGENT = VF.createIRI("http://schema.org/bookingAgent");
        BOOKINGTIME = VF.createIRI("http://schema.org/bookingTime");
        BORROWER = VF.createIRI("http://schema.org/borrower");
        BOX = VF.createIRI("http://schema.org/box");
        BRANCHCODE = VF.createIRI("http://schema.org/branchCode");
        BRANCHOF = VF.createIRI("http://schema.org/branchOf");
        HAS_BRAND = VF.createIRI("http://schema.org/brand");
        BREADCRUMB = VF.createIRI("http://schema.org/breadcrumb");
        BROADCASTAFFILIATEOF = VF.createIRI("http://schema.org/broadcastAffiliateOf");
        BROADCASTCHANNELID = VF.createIRI("http://schema.org/broadcastChannelId");
        BROADCASTDISPLAYNAME = VF.createIRI("http://schema.org/broadcastDisplayName");
        BROADCASTOFEVENT = VF.createIRI("http://schema.org/broadcastOfEvent");
        BROADCASTSERVICETIER = VF.createIRI("http://schema.org/broadcastServiceTier");
        BROADCASTTIMEZONE = VF.createIRI("http://schema.org/broadcastTimezone");
        BROADCASTER = VF.createIRI("http://schema.org/broadcaster");
        BROWSERREQUIREMENTS = VF.createIRI("http://schema.org/browserRequirements");
        BUSNAME = VF.createIRI("http://schema.org/busName");
        BUSNUMBER = VF.createIRI("http://schema.org/busNumber");
        HAS_BUSINESSFUNCTION = VF.createIRI("http://schema.org/businessFunction");
        BUYER = VF.createIRI("http://schema.org/buyer");
        BYARTIST = VF.createIRI("http://schema.org/byArtist");
        CALORIES = VF.createIRI("http://schema.org/calories");
        CANDIDATE = VF.createIRI("http://schema.org/candidate");
        CAPTION = VF.createIRI("http://schema.org/caption");
        CARBOHYDRATECONTENT = VF.createIRI("http://schema.org/carbohydrateContent");
        CARGOVOLUME = VF.createIRI("http://schema.org/cargoVolume");
        CARRIER = VF.createIRI("http://schema.org/carrier");
        CARRIERREQUIREMENTS = VF.createIRI("http://schema.org/carrierRequirements");
        CATALOG = VF.createIRI("http://schema.org/catalog");
        CATALOGNUMBER = VF.createIRI("http://schema.org/catalogNumber");
        CATEGORY = VF.createIRI("http://schema.org/category");
        CCRECIPIENT = VF.createIRI("http://schema.org/ccRecipient");
        CHARACTER = VF.createIRI("http://schema.org/character");
        CHARACTERATTRIBUTE = VF.createIRI("http://schema.org/characterAttribute");
        CHARACTERNAME = VF.createIRI("http://schema.org/characterName");
        CHEATCODE = VF.createIRI("http://schema.org/cheatCode");
        CHECKINTIME = VF.createIRI("http://schema.org/checkinTime");
        CHECKOUTTIME = VF.createIRI("http://schema.org/checkoutTime");
        CHILDMAXAGE = VF.createIRI("http://schema.org/childMaxAge");
        CHILDMINAGE = VF.createIRI("http://schema.org/childMinAge");
        CHILDREN = VF.createIRI("http://schema.org/children");
        CHOLESTEROLCONTENT = VF.createIRI("http://schema.org/cholesterolContent");
        CIRCLE = VF.createIRI("http://schema.org/circle");
        CITATION = VF.createIRI("http://schema.org/citation");
        CLAIMREVIEWED = VF.createIRI("http://schema.org/claimReviewed");
        CLIPNUMBER = VF.createIRI("http://schema.org/clipNumber");
        CLOSES = VF.createIRI("http://schema.org/closes");
        COACH = VF.createIRI("http://schema.org/coach");
        CODEREPOSITORY = VF.createIRI("http://schema.org/codeRepository");
        COLLEAGUES = VF.createIRI("http://schema.org/colleagues");
        COLLECTION = VF.createIRI("http://schema.org/collection");
        COLOR = VF.createIRI("http://schema.org/color");
        HAS_COMMENT = VF.createIRI("http://schema.org/comment");
        COMMENTCOUNT = VF.createIRI("http://schema.org/commentCount");
        COMMENTTEXT = VF.createIRI("http://schema.org/commentText");
        COMMENTTIME = VF.createIRI("http://schema.org/commentTime");
        COMPOSER = VF.createIRI("http://schema.org/composer");
        CONFIRMATIONNUMBER = VF.createIRI("http://schema.org/confirmationNumber");
        CONTACTOPTION = VF.createIRI("http://schema.org/contactOption");
        CONTACTPOINTS = VF.createIRI("http://schema.org/contactPoints");
        CONTACTTYPE = VF.createIRI("http://schema.org/contactType");
        CONTAINEDIN = VF.createIRI("http://schema.org/containedIn");
        CONTENTRATING = VF.createIRI("http://schema.org/contentRating");
        CONTENTSIZE = VF.createIRI("http://schema.org/contentSize");
        CONTENTTYPE = VF.createIRI("http://schema.org/contentType");
        CONTENTURL = VF.createIRI("http://schema.org/contentUrl");
        CONTRIBUTOR = VF.createIRI("http://schema.org/contributor");
        COOKTIME = VF.createIRI("http://schema.org/cookTime");
        COOKINGMETHOD = VF.createIRI("http://schema.org/cookingMethod");
        COPYRIGHTHOLDER = VF.createIRI("http://schema.org/copyrightHolder");
        COPYRIGHTYEAR = VF.createIRI("http://schema.org/copyrightYear");
        COUNTRIESNOTSUPPORTED = VF.createIRI("http://schema.org/countriesNotSupported");
        COUNTRIESSUPPORTED = VF.createIRI("http://schema.org/countriesSupported");
        COUNTRYOFORIGIN = VF.createIRI("http://schema.org/countryOfOrigin");
        HAS_COURSE = VF.createIRI("http://schema.org/course");
        COURSECODE = VF.createIRI("http://schema.org/courseCode");
        COURSEMODE = VF.createIRI("http://schema.org/courseMode");
        COURSEPREREQUISITES = VF.createIRI("http://schema.org/coursePrerequisites");
        COVERAGEENDTIME = VF.createIRI("http://schema.org/coverageEndTime");
        COVERAGESTARTTIME = VF.createIRI("http://schema.org/coverageStartTime");
        CREATOR = VF.createIRI("http://schema.org/creator");
        CREDITEDTO = VF.createIRI("http://schema.org/creditedTo");
        CURRENCIESACCEPTED = VF.createIRI("http://schema.org/currenciesAccepted");
        CURRENCY = VF.createIRI("http://schema.org/currency");
        CUSTOMER = VF.createIRI("http://schema.org/customer");
        DATAFEEDELEMENT = VF.createIRI("http://schema.org/dataFeedElement");
        DATASETTIMEINTERVAL = VF.createIRI("http://schema.org/datasetTimeInterval");
        DATECREATED = VF.createIRI("http://schema.org/dateCreated");
        DATEDELETED = VF.createIRI("http://schema.org/dateDeleted");
        DATEISSUED = VF.createIRI("http://schema.org/dateIssued");
        DATEMODIFIED = VF.createIRI("http://schema.org/dateModified");
        DATEPOSTED = VF.createIRI("http://schema.org/datePosted");
        DATEPUBLISHED = VF.createIRI("http://schema.org/datePublished");
        DATEREAD = VF.createIRI("http://schema.org/dateRead");
        DATERECEIVED = VF.createIRI("http://schema.org/dateReceived");
        DATESENT = VF.createIRI("http://schema.org/dateSent");
        DATEVEHICLEFIRSTREGISTERED = VF.createIRI("http://schema.org/dateVehicleFirstRegistered");
        DATELINE = VF.createIRI("http://schema.org/dateline");
        HAS_DAYOFWEEK = VF.createIRI("http://schema.org/dayOfWeek");
        DEATHDATE = VF.createIRI("http://schema.org/deathDate");
        DEATHPLACE = VF.createIRI("http://schema.org/deathPlace");
        DEFAULTVALUE = VF.createIRI("http://schema.org/defaultValue");
        DELIVERYADDRESS = VF.createIRI("http://schema.org/deliveryAddress");
        DELIVERYLEADTIME = VF.createIRI("http://schema.org/deliveryLeadTime");
        HAS_DELIVERYMETHOD = VF.createIRI("http://schema.org/deliveryMethod");
        DELIVERYSTATUS = VF.createIRI("http://schema.org/deliveryStatus");
        DEPARTMENT = VF.createIRI("http://schema.org/department");
        DEPARTUREAIRPORT = VF.createIRI("http://schema.org/departureAirport");
        DEPARTUREBUSSTOP = VF.createIRI("http://schema.org/departureBusStop");
        DEPARTUREGATE = VF.createIRI("http://schema.org/departureGate");
        DEPARTUREPLATFORM = VF.createIRI("http://schema.org/departurePlatform");
        DEPARTURESTATION = VF.createIRI("http://schema.org/departureStation");
        DEPARTURETERMINAL = VF.createIRI("http://schema.org/departureTerminal");
        DEPARTURETIME = VF.createIRI("http://schema.org/departureTime");
        DEPENDENCIES = VF.createIRI("http://schema.org/dependencies");
        DEPTH = VF.createIRI("http://schema.org/depth");
        DEVICE = VF.createIRI("http://schema.org/device");
        DIRECTORS = VF.createIRI("http://schema.org/directors");
        DISAMBIGUATINGDESCRIPTION = VF.createIRI("http://schema.org/disambiguatingDescription");
        DISCOUNT = VF.createIRI("http://schema.org/discount");
        DISCOUNTCODE = VF.createIRI("http://schema.org/discountCode");
        DISCOUNTCURRENCY = VF.createIRI("http://schema.org/discountCurrency");
        DISCUSSES = VF.createIRI("http://schema.org/discusses");
        DISCUSSIONURL = VF.createIRI("http://schema.org/discussionUrl");
        DISSOLUTIONDATE = VF.createIRI("http://schema.org/dissolutionDate");
        HAS_DISTANCE = VF.createIRI("http://schema.org/distance");
        DISTRIBUTION = VF.createIRI("http://schema.org/distribution");
        DOORTIME = VF.createIRI("http://schema.org/doorTime");
        DOWNLOADURL = VF.createIRI("http://schema.org/downloadUrl");
        DOWNVOTECOUNT = VF.createIRI("http://schema.org/downvoteCount");
        DRIVEWHEELCONFIGURATION = VF.createIRI("http://schema.org/driveWheelConfiguration");
        DROPOFFLOCATION = VF.createIRI("http://schema.org/dropoffLocation");
        DROPOFFTIME = VF.createIRI("http://schema.org/dropoffTime");
        DUNS = VF.createIRI("http://schema.org/duns");
        DURATIONOFWARRANTY = VF.createIRI("http://schema.org/durationOfWarranty");
        DURINGMEDIA = VF.createIRI("http://schema.org/duringMedia");
        EDITOR = VF.createIRI("http://schema.org/editor");
        EDUCATIONREQUIREMENTS = VF.createIRI("http://schema.org/educationRequirements");
        EDUCATIONALALIGNMENT = VF.createIRI("http://schema.org/educationalAlignment");
        EDUCATIONALFRAMEWORK = VF.createIRI("http://schema.org/educationalFramework");
        EDUCATIONALROLE = VF.createIRI("http://schema.org/educationalRole");
        EDUCATIONALUSE = VF.createIRI("http://schema.org/educationalUse");
        ELEVATION = VF.createIRI("http://schema.org/elevation");
        ELIGIBLECUSTOMERTYPE = VF.createIRI("http://schema.org/eligibleCustomerType");
        ELIGIBLEDURATION = VF.createIRI("http://schema.org/eligibleDuration");
        ELIGIBLEQUANTITY = VF.createIRI("http://schema.org/eligibleQuantity");
        ELIGIBLEREGION = VF.createIRI("http://schema.org/eligibleRegion");
        ELIGIBLETRANSACTIONVOLUME = VF.createIRI("http://schema.org/eligibleTransactionVolume");
        EMAIL = VF.createIRI("http://schema.org/email");
        EMBEDURL = VF.createIRI("http://schema.org/embedUrl");
        EMPLOYEES = VF.createIRI("http://schema.org/employees");
        EMPLOYMENTTYPE = VF.createIRI("http://schema.org/employmentType");
        ENCODESCREATIVEWORK = VF.createIRI("http://schema.org/encodesCreativeWork");
        ENCODINGTYPE = VF.createIRI("http://schema.org/encodingType");
        ENCODINGS = VF.createIRI("http://schema.org/encodings");
        ENDDATE = VF.createIRI("http://schema.org/endDate");
        ENDTIME = VF.createIRI("http://schema.org/endTime");
        ENDORSEE = VF.createIRI("http://schema.org/endorsee");
        HAS_ENTERTAINMENTBUSINESS = VF.createIRI("http://schema.org/entertainmentBusiness");
        EPISODENUMBER = VF.createIRI("http://schema.org/episodeNumber");
        EPISODES = VF.createIRI("http://schema.org/episodes");
        EQUAL = VF.createIRI("http://schema.org/equal");
        ERROR = VF.createIRI("http://schema.org/error");
        ESTIMATEDCOST = VF.createIRI("http://schema.org/estimatedCost");
        ESTIMATEDFLIGHTDURATION = VF.createIRI("http://schema.org/estimatedFlightDuration");
        EVENTSTATUS = VF.createIRI("http://schema.org/eventStatus");
        EVENTS = VF.createIRI("http://schema.org/events");
        EXIFDATA = VF.createIRI("http://schema.org/exifData");
        EXPECTEDARRIVALFROM = VF.createIRI("http://schema.org/expectedArrivalFrom");
        EXPECTEDARRIVALUNTIL = VF.createIRI("http://schema.org/expectedArrivalUntil");
        EXPECTSACCEPTANCEOF = VF.createIRI("http://schema.org/expectsAcceptanceOf");
        EXPERIENCEREQUIREMENTS = VF.createIRI("http://schema.org/experienceRequirements");
        EXPIRES = VF.createIRI("http://schema.org/expires");
        FAMILYNAME = VF.createIRI("http://schema.org/familyName");
        FATCONTENT = VF.createIRI("http://schema.org/fatContent");
        FAXNUMBER = VF.createIRI("http://schema.org/faxNumber");
        FEATURELIST = VF.createIRI("http://schema.org/featureList");
        FEESANDCOMMISSIONSSPECIFICATION = VF.createIRI("http://schema.org/feesAndCommissionsSpecification");
        FIBERCONTENT = VF.createIRI("http://schema.org/fiberContent");
        FILEFORMAT = VF.createIRI("http://schema.org/fileFormat");
        FILESIZE = VF.createIRI("http://schema.org/fileSize");
        FIRSTPERFORMANCE = VF.createIRI("http://schema.org/firstPerformance");
        FLIGHTDISTANCE = VF.createIRI("http://schema.org/flightDistance");
        FLIGHTNUMBER = VF.createIRI("http://schema.org/flightNumber");
        FLOORSIZE = VF.createIRI("http://schema.org/floorSize");
        FOLLOWEE = VF.createIRI("http://schema.org/followee");
        FOLLOWS = VF.createIRI("http://schema.org/follows");
        HAS_FOODESTABLISHMENT = VF.createIRI("http://schema.org/foodEstablishment");
        HAS_FOODEVENT = VF.createIRI("http://schema.org/foodEvent");
        FOUNDERS = VF.createIRI("http://schema.org/founders");
        FOUNDINGDATE = VF.createIRI("http://schema.org/foundingDate");
        FOUNDINGLOCATION = VF.createIRI("http://schema.org/foundingLocation");
        FREE = VF.createIRI("http://schema.org/free");
        FROMLOCATION = VF.createIRI("http://schema.org/fromLocation");
        FUELCONSUMPTION = VF.createIRI("http://schema.org/fuelConsumption");
        FUELEFFICIENCY = VF.createIRI("http://schema.org/fuelEfficiency");
        FUELTYPE = VF.createIRI("http://schema.org/fuelType");
        FUNDER = VF.createIRI("http://schema.org/funder");
        GAMEITEM = VF.createIRI("http://schema.org/gameItem");
        GAMELOCATION = VF.createIRI("http://schema.org/gameLocation");
        GAMEPLATFORM = VF.createIRI("http://schema.org/gamePlatform");
        GAMETIP = VF.createIRI("http://schema.org/gameTip");
        GENDER = VF.createIRI("http://schema.org/gender");
        GENRE = VF.createIRI("http://schema.org/genre");
        GEO = VF.createIRI("http://schema.org/geo");
        GEOMIDPOINT = VF.createIRI("http://schema.org/geoMidpoint");
        GEORADIUS = VF.createIRI("http://schema.org/geoRadius");
        GEOGRAPHICAREA = VF.createIRI("http://schema.org/geographicArea");
        GIVENNAME = VF.createIRI("http://schema.org/givenName");
        GLOBALLOCATIONNUMBER = VF.createIRI("http://schema.org/globalLocationNumber");
        GRANTEE = VF.createIRI("http://schema.org/grantee");
        GREATER = VF.createIRI("http://schema.org/greater");
        GREATEROREQUAL = VF.createIRI("http://schema.org/greaterOrEqual");
        GTIN12 = VF.createIRI("http://schema.org/gtin12");
        GTIN13 = VF.createIRI("http://schema.org/gtin13");
        GTIN14 = VF.createIRI("http://schema.org/gtin14");
        GTIN8 = VF.createIRI("http://schema.org/gtin8");
        HASCOURSEINSTANCE = VF.createIRI("http://schema.org/hasCourseInstance");
        HASDELIVERYMETHOD = VF.createIRI("http://schema.org/hasDeliveryMethod");
        HASDIGITALDOCUMENTPERMISSION = VF.createIRI("http://schema.org/hasDigitalDocumentPermission");
        HASMENUITEM = VF.createIRI("http://schema.org/hasMenuItem");
        HASMENUSECTION = VF.createIRI("http://schema.org/hasMenuSection");
        HASOFFERCATALOG = VF.createIRI("http://schema.org/hasOfferCatalog");
        HASPOS = VF.createIRI("http://schema.org/hasPOS");
        HEADLINE = VF.createIRI("http://schema.org/headline");
        HEIGHT = VF.createIRI("http://schema.org/height");
        HIGHPRICE = VF.createIRI("http://schema.org/highPrice");
        HIRINGORGANIZATION = VF.createIRI("http://schema.org/hiringOrganization");
        HOMELOCATION = VF.createIRI("http://schema.org/homeLocation");
        HOMETEAM = VF.createIRI("http://schema.org/homeTeam");
        HONORIFICPREFIX = VF.createIRI("http://schema.org/honorificPrefix");
        HONORIFICSUFFIX = VF.createIRI("http://schema.org/honorificSuffix");
        HOSTINGORGANIZATION = VF.createIRI("http://schema.org/hostingOrganization");
        HOURSAVAILABLE = VF.createIRI("http://schema.org/hoursAvailable");
        HTTPMETHOD = VF.createIRI("http://schema.org/httpMethod");
        IATACODE = VF.createIRI("http://schema.org/iataCode");
        ICAOCODE = VF.createIRI("http://schema.org/icaoCode");
        ILLUSTRATOR = VF.createIRI("http://schema.org/illustrator");
        INALBUM = VF.createIRI("http://schema.org/inAlbum");
        INBROADCASTLINEUP = VF.createIRI("http://schema.org/inBroadcastLineup");
        INPLAYLIST = VF.createIRI("http://schema.org/inPlaylist");
        INCENTIVES = VF.createIRI("http://schema.org/incentives");
        INCLUDEDCOMPOSITION = VF.createIRI("http://schema.org/includedComposition");
        INCLUDEDDATACATALOG = VF.createIRI("http://schema.org/includedDataCatalog");
        INCLUDESOBJECT = VF.createIRI("http://schema.org/includesObject");
        INDUSTRY = VF.createIRI("http://schema.org/industry");
        INELIGIBLEREGION = VF.createIRI("http://schema.org/ineligibleRegion");
        INGREDIENTS = VF.createIRI("http://schema.org/ingredients");
        INSTALLURL = VF.createIRI("http://schema.org/installUrl");
        INSTRUCTOR = VF.createIRI("http://schema.org/instructor");
        INTERACTIONCOUNT = VF.createIRI("http://schema.org/interactionCount");
        INTERACTIONSERVICE = VF.createIRI("http://schema.org/interactionService");
        INTERACTIONTYPE = VF.createIRI("http://schema.org/interactionType");
        INTERACTIVITYTYPE = VF.createIRI("http://schema.org/interactivityType");
        INTERESTRATE = VF.createIRI("http://schema.org/interestRate");
        INVENTORYLEVEL = VF.createIRI("http://schema.org/inventoryLevel");
        ISACCESSORYORSPAREPARTFOR = VF.createIRI("http://schema.org/isAccessoryOrSparePartFor");
        ISBASEDONURL = VF.createIRI("http://schema.org/isBasedOnUrl");
        ISCONSUMABLEFOR = VF.createIRI("http://schema.org/isConsumableFor");
        ISFAMILYFRIENDLY = VF.createIRI("http://schema.org/isFamilyFriendly");
        ISGIFT = VF.createIRI("http://schema.org/isGift");
        ISLIVEBROADCAST = VF.createIRI("http://schema.org/isLiveBroadcast");
        ISRELATEDTO = VF.createIRI("http://schema.org/isRelatedTo");
        ISSIMILARTO = VF.createIRI("http://schema.org/isSimilarTo");
        ISVARIANTOF = VF.createIRI("http://schema.org/isVariantOf");
        ISBN = VF.createIRI("http://schema.org/isbn");
        ISICV4 = VF.createIRI("http://schema.org/isicV4");
        ISRCCODE = VF.createIRI("http://schema.org/isrcCode");
        ISSN = VF.createIRI("http://schema.org/issn");
        ISSUENUMBER = VF.createIRI("http://schema.org/issueNumber");
        ISSUEDBY = VF.createIRI("http://schema.org/issuedBy");
        ISSUEDTHROUGH = VF.createIRI("http://schema.org/issuedThrough");
        ISWCCODE = VF.createIRI("http://schema.org/iswcCode");
        ITEM = VF.createIRI("http://schema.org/item");
        ITEMCONDITION = VF.createIRI("http://schema.org/itemCondition");
        ITEMLISTELEMENT = VF.createIRI("http://schema.org/itemListElement");
        ITEMLISTORDER = VF.createIRI("http://schema.org/itemListOrder");
        ITEMOFFERED = VF.createIRI("http://schema.org/itemOffered");
        ITEMREVIEWED = VF.createIRI("http://schema.org/itemReviewed");
        ITEMSHIPPED = VF.createIRI("http://schema.org/itemShipped");
        JOBLOCATION = VF.createIRI("http://schema.org/jobLocation");
        JOBTITLE = VF.createIRI("http://schema.org/jobTitle");
        KEYWORDS = VF.createIRI("http://schema.org/keywords");
        KNOWNVEHICLEDAMAGES = VF.createIRI("http://schema.org/knownVehicleDamages");
        KNOWS = VF.createIRI("http://schema.org/knows");
        LANDLORD = VF.createIRI("http://schema.org/landlord");
        HAS_LANGUAGE = VF.createIRI("http://schema.org/language");
        LASTREVIEWED = VF.createIRI("http://schema.org/lastReviewed");
        LATITUDE = VF.createIRI("http://schema.org/latitude");
        LEARNINGRESOURCETYPE = VF.createIRI("http://schema.org/learningResourceType");
        LEGALNAME = VF.createIRI("http://schema.org/legalName");
        LEICODE = VF.createIRI("http://schema.org/leiCode");
        LENDER = VF.createIRI("http://schema.org/lender");
        LESSER = VF.createIRI("http://schema.org/lesser");
        LESSEROREQUAL = VF.createIRI("http://schema.org/lesserOrEqual");
        LICENSE = VF.createIRI("http://schema.org/license");
        LINE = VF.createIRI("http://schema.org/line");
        LIVEBLOGUPDATE = VF.createIRI("http://schema.org/liveBlogUpdate");
        LOANTERM = VF.createIRI("http://schema.org/loanTerm");
        LOCATIONCREATED = VF.createIRI("http://schema.org/locationCreated");
        LODGINGUNITDESCRIPTION = VF.createIRI("http://schema.org/lodgingUnitDescription");
        LODGINGUNITTYPE = VF.createIRI("http://schema.org/lodgingUnitType");
        LOGO = VF.createIRI("http://schema.org/logo");
        LONGITUDE = VF.createIRI("http://schema.org/longitude");
        LOSER = VF.createIRI("http://schema.org/loser");
        LOWPRICE = VF.createIRI("http://schema.org/lowPrice");
        LYRICIST = VF.createIRI("http://schema.org/lyricist");
        LYRICS = VF.createIRI("http://schema.org/lyrics");
        MAINCONTENTOFPAGE = VF.createIRI("http://schema.org/mainContentOfPage");
        MANUFACTURER = VF.createIRI("http://schema.org/manufacturer");
        HAS_MAP = VF.createIRI("http://schema.org/map");
        MAPTYPE = VF.createIRI("http://schema.org/mapType");
        MAPS = VF.createIRI("http://schema.org/maps");
        MAXPRICE = VF.createIRI("http://schema.org/maxPrice");
        MAXVALUE = VF.createIRI("http://schema.org/maxValue");
        MAXIMUMATTENDEECAPACITY = VF.createIRI("http://schema.org/maximumAttendeeCapacity");
        MEALSERVICE = VF.createIRI("http://schema.org/mealService");
        MEMBERS = VF.createIRI("http://schema.org/members");
        MEMBERSHIPNUMBER = VF.createIRI("http://schema.org/membershipNumber");
        MEMORYREQUIREMENTS = VF.createIRI("http://schema.org/memoryRequirements");
        MENTIONS = VF.createIRI("http://schema.org/mentions");
        HAS_MENU = VF.createIRI("http://schema.org/menu");
        MERCHANT = VF.createIRI("http://schema.org/merchant");
        MESSAGEATTACHMENT = VF.createIRI("http://schema.org/messageAttachment");
        MILEAGEFROMODOMETER = VF.createIRI("http://schema.org/mileageFromOdometer");
        MINPRICE = VF.createIRI("http://schema.org/minPrice");
        MINVALUE = VF.createIRI("http://schema.org/minValue");
        MINIMUMPAYMENTDUE = VF.createIRI("http://schema.org/minimumPaymentDue");
        MODEL = VF.createIRI("http://schema.org/model");
        MODIFIEDTIME = VF.createIRI("http://schema.org/modifiedTime");
        MPN = VF.createIRI("http://schema.org/mpn");
        MULTIPLEVALUES = VF.createIRI("http://schema.org/multipleValues");
        MUSICARRANGEMENT = VF.createIRI("http://schema.org/musicArrangement");
        MUSICBY = VF.createIRI("http://schema.org/musicBy");
        MUSICCOMPOSITIONFORM = VF.createIRI("http://schema.org/musicCompositionForm");
        MUSICGROUPMEMBER = VF.createIRI("http://schema.org/musicGroupMember");
        MUSICRELEASEFORMAT = VF.createIRI("http://schema.org/musicReleaseFormat");
        MUSICALKEY = VF.createIRI("http://schema.org/musicalKey");
        NAICS = VF.createIRI("http://schema.org/naics");
        NAME = VF.createIRI("http://schema.org/name");
        NAMEDPOSITION = VF.createIRI("http://schema.org/namedPosition");
        NATIONALITY = VF.createIRI("http://schema.org/nationality");
        NETWORTH = VF.createIRI("http://schema.org/netWorth");
        NEXTITEM = VF.createIRI("http://schema.org/nextItem");
        NONEQUAL = VF.createIRI("http://schema.org/nonEqual");
        NUMADULTS = VF.createIRI("http://schema.org/numAdults");
        NUMCHILDREN = VF.createIRI("http://schema.org/numChildren");
        NUMTRACKS = VF.createIRI("http://schema.org/numTracks");
        NUMBEROFAIRBAGS = VF.createIRI("http://schema.org/numberOfAirbags");
        NUMBEROFAXLES = VF.createIRI("http://schema.org/numberOfAxles");
        NUMBEROFBEDS = VF.createIRI("http://schema.org/numberOfBeds");
        NUMBEROFDOORS = VF.createIRI("http://schema.org/numberOfDoors");
        NUMBEROFEMPLOYEES = VF.createIRI("http://schema.org/numberOfEmployees");
        NUMBEROFEPISODES = VF.createIRI("http://schema.org/numberOfEpisodes");
        NUMBEROFFORWARDGEARS = VF.createIRI("http://schema.org/numberOfForwardGears");
        NUMBEROFITEMS = VF.createIRI("http://schema.org/numberOfItems");
        NUMBEROFPAGES = VF.createIRI("http://schema.org/numberOfPages");
        NUMBEROFPLAYERS = VF.createIRI("http://schema.org/numberOfPlayers");
        NUMBEROFPREVIOUSOWNERS = VF.createIRI("http://schema.org/numberOfPreviousOwners");
        NUMBEROFROOMS = VF.createIRI("http://schema.org/numberOfRooms");
        NUMBEROFSEASONS = VF.createIRI("http://schema.org/numberOfSeasons");
        NUMBEREDPOSITION = VF.createIRI("http://schema.org/numberedPosition");
        NUTRITION = VF.createIRI("http://schema.org/nutrition");
        OCCUPANCY = VF.createIRI("http://schema.org/occupancy");
        OCCUPATIONALCATEGORY = VF.createIRI("http://schema.org/occupationalCategory");
        OFFERCOUNT = VF.createIRI("http://schema.org/offerCount");
        OFFERS = VF.createIRI("http://schema.org/offers");
        OPENINGHOURS = VF.createIRI("http://schema.org/openingHours");
        HAS_OPENINGHOURSSPECIFICATION = VF.createIRI("http://schema.org/openingHoursSpecification");
        OPENS = VF.createIRI("http://schema.org/opens");
        OPERATINGSYSTEM = VF.createIRI("http://schema.org/operatingSystem");
        OPPONENT = VF.createIRI("http://schema.org/opponent");
        OPTION = VF.createIRI("http://schema.org/option");
        ORDERDATE = VF.createIRI("http://schema.org/orderDate");
        ORDERDELIVERY = VF.createIRI("http://schema.org/orderDelivery");
        ORDERITEMNUMBER = VF.createIRI("http://schema.org/orderItemNumber");
        ORDERITEMSTATUS = VF.createIRI("http://schema.org/orderItemStatus");
        ORDERNUMBER = VF.createIRI("http://schema.org/orderNumber");
        ORDERQUANTITY = VF.createIRI("http://schema.org/orderQuantity");
        HAS_ORDERSTATUS = VF.createIRI("http://schema.org/orderStatus");
        ORDEREDITEM = VF.createIRI("http://schema.org/orderedItem");
        ORGANIZER = VF.createIRI("http://schema.org/organizer");
        ORIGINADDRESS = VF.createIRI("http://schema.org/originAddress");
        OWNEDFROM = VF.createIRI("http://schema.org/ownedFrom");
        OWNEDTHROUGH = VF.createIRI("http://schema.org/ownedThrough");
        OWNS = VF.createIRI("http://schema.org/owns");
        PAGEEND = VF.createIRI("http://schema.org/pageEnd");
        PAGESTART = VF.createIRI("http://schema.org/pageStart");
        PAGINATION = VF.createIRI("http://schema.org/pagination");
        PARENTITEM = VF.createIRI("http://schema.org/parentItem");
        PARENTSERVICE = VF.createIRI("http://schema.org/parentService");
        PARENTS = VF.createIRI("http://schema.org/parents");
        PARTOFEPISODE = VF.createIRI("http://schema.org/partOfEpisode");
        PARTOFINVOICE = VF.createIRI("http://schema.org/partOfInvoice");
        PARTOFORDER = VF.createIRI("http://schema.org/partOfOrder");
        PARTOFSEASON = VF.createIRI("http://schema.org/partOfSeason");
        PARTOFTVSERIES = VF.createIRI("http://schema.org/partOfTVSeries");
        PARTYSIZE = VF.createIRI("http://schema.org/partySize");
        PASSENGERPRIORITYSTATUS = VF.createIRI("http://schema.org/passengerPriorityStatus");
        PASSENGERSEQUENCENUMBER = VF.createIRI("http://schema.org/passengerSequenceNumber");
        PAYMENTACCEPTED = VF.createIRI("http://schema.org/paymentAccepted");
        PAYMENTDUE = VF.createIRI("http://schema.org/paymentDue");
        HAS_PAYMENTMETHOD = VF.createIRI("http://schema.org/paymentMethod");
        PAYMENTMETHODID = VF.createIRI("http://schema.org/paymentMethodId");
        PAYMENTSTATUS = VF.createIRI("http://schema.org/paymentStatus");
        PAYMENTURL = VF.createIRI("http://schema.org/paymentUrl");
        PERFORMERIN = VF.createIRI("http://schema.org/performerIn");
        PERFORMERS = VF.createIRI("http://schema.org/performers");
        PERMISSIONTYPE = VF.createIRI("http://schema.org/permissionType");
        PERMISSIONS = VF.createIRI("http://schema.org/permissions");
        PERMITAUDIENCE = VF.createIRI("http://schema.org/permitAudience");
        PERMITTEDUSAGE = VF.createIRI("http://schema.org/permittedUsage");
        PETSALLOWED = VF.createIRI("http://schema.org/petsAllowed");
        PHOTOS = VF.createIRI("http://schema.org/photos");
        PICKUPLOCATION = VF.createIRI("http://schema.org/pickupLocation");
        PICKUPTIME = VF.createIRI("http://schema.org/pickupTime");
        PLAYMODE = VF.createIRI("http://schema.org/playMode");
        PLAYERTYPE = VF.createIRI("http://schema.org/playerType");
        PLAYERSONLINE = VF.createIRI("http://schema.org/playersOnline");
        POLYGON = VF.createIRI("http://schema.org/polygon");
        POSTOFFICEBOXNUMBER = VF.createIRI("http://schema.org/postOfficeBoxNumber");
        POSTALCODE = VF.createIRI("http://schema.org/postalCode");
        POTENTIALACTION = VF.createIRI("http://schema.org/potentialAction");
        PREDECESSOROF = VF.createIRI("http://schema.org/predecessorOf");
        PREPTIME = VF.createIRI("http://schema.org/prepTime");
        PREVIOUSITEM = VF.createIRI("http://schema.org/previousItem");
        PREVIOUSSTARTDATE = VF.createIRI("http://schema.org/previousStartDate");
        PRICE = VF.createIRI("http://schema.org/price");
        PRICECOMPONENT = VF.createIRI("http://schema.org/priceComponent");
        PRICECURRENCY = VF.createIRI("http://schema.org/priceCurrency");
        PRICERANGE = VF.createIRI("http://schema.org/priceRange");
        HAS_PRICESPECIFICATION = VF.createIRI("http://schema.org/priceSpecification");
        PRICETYPE = VF.createIRI("http://schema.org/priceType");
        PRICEVALIDUNTIL = VF.createIRI("http://schema.org/priceValidUntil");
        PRIMARYIMAGEOFPAGE = VF.createIRI("http://schema.org/primaryImageOfPage");
        PRINTCOLUMN = VF.createIRI("http://schema.org/printColumn");
        PRINTEDITION = VF.createIRI("http://schema.org/printEdition");
        PRINTPAGE = VF.createIRI("http://schema.org/printPage");
        PRINTSECTION = VF.createIRI("http://schema.org/printSection");
        PROCESSINGTIME = VF.createIRI("http://schema.org/processingTime");
        PROCESSORREQUIREMENTS = VF.createIRI("http://schema.org/processorRequirements");
        PRODUCER = VF.createIRI("http://schema.org/producer");
        PRODUCES = VF.createIRI("http://schema.org/produces");
        PRODUCTID = VF.createIRI("http://schema.org/productID");
        PRODUCTSUPPORTED = VF.createIRI("http://schema.org/productSupported");
        PRODUCTIONCOMPANY = VF.createIRI("http://schema.org/productionCompany");
        PRODUCTIONDATE = VF.createIRI("http://schema.org/productionDate");
        PROFICIENCYLEVEL = VF.createIRI("http://schema.org/proficiencyLevel");
        PROGRAMMEMBERSHIPUSED = VF.createIRI("http://schema.org/programMembershipUsed");
        PROGRAMNAME = VF.createIRI("http://schema.org/programName");
        PROGRAMMINGLANGUAGE = VF.createIRI("http://schema.org/programmingLanguage");
        PROGRAMMINGMODEL = VF.createIRI("http://schema.org/programmingModel");
        PROPERTYID = VF.createIRI("http://schema.org/propertyID");
        PROTEINCONTENT = VF.createIRI("http://schema.org/proteinContent");
        PROVIDERMOBILITY = VF.createIRI("http://schema.org/providerMobility");
        PROVIDESBROADCASTSERVICE = VF.createIRI("http://schema.org/providesBroadcastService");
        PROVIDESSERVICE = VF.createIRI("http://schema.org/providesService");
        PUBLICACCESS = VF.createIRI("http://schema.org/publicAccess");
        PUBLICATION = VF.createIRI("http://schema.org/publication");
        PUBLISHEDON = VF.createIRI("http://schema.org/publishedOn");
        PUBLISHER = VF.createIRI("http://schema.org/publisher");
        PUBLISHINGPRINCIPLES = VF.createIRI("http://schema.org/publishingPrinciples");
        PURCHASEDATE = VF.createIRI("http://schema.org/purchaseDate");
        QUALIFICATIONS = VF.createIRI("http://schema.org/qualifications");
        QUERY = VF.createIRI("http://schema.org/query");
        QUEST = VF.createIRI("http://schema.org/quest");
        HAS_QUESTION = VF.createIRI("http://schema.org/question");
        RATINGCOUNT = VF.createIRI("http://schema.org/ratingCount");
        RATINGVALUE = VF.createIRI("http://schema.org/ratingValue");
        READONLYVALUE = VF.createIRI("http://schema.org/readonlyValue");
        HAS_REALESTATEAGENT = VF.createIRI("http://schema.org/realEstateAgent");
        HAS_RECIPE = VF.createIRI("http://schema.org/recipe");
        RECIPECATEGORY = VF.createIRI("http://schema.org/recipeCategory");
        RECIPECUISINE = VF.createIRI("http://schema.org/recipeCuisine");
        RECIPEINSTRUCTIONS = VF.createIRI("http://schema.org/recipeInstructions");
        RECIPEYIELD = VF.createIRI("http://schema.org/recipeYield");
        RECORDLABEL = VF.createIRI("http://schema.org/recordLabel");
        REFERENCEQUANTITY = VF.createIRI("http://schema.org/referenceQuantity");
        REFERENCESORDER = VF.createIRI("http://schema.org/referencesOrder");
        REGIONSALLOWED = VF.createIRI("http://schema.org/regionsAllowed");
        RELATEDLINK = VF.createIRI("http://schema.org/relatedLink");
        RELATEDTO = VF.createIRI("http://schema.org/relatedTo");
        RELEASEDATE = VF.createIRI("http://schema.org/releaseDate");
        RELEASENOTES = VF.createIRI("http://schema.org/releaseNotes");
        RELEASEDEVENT = VF.createIRI("http://schema.org/releasedEvent");
        REMAININGATTENDEECAPACITY = VF.createIRI("http://schema.org/remainingAttendeeCapacity");
        REPLACEE = VF.createIRI("http://schema.org/replacee");
        REPLACER = VF.createIRI("http://schema.org/replacer");
        REPLYTOURL = VF.createIRI("http://schema.org/replyToUrl");
        REPORTNUMBER = VF.createIRI("http://schema.org/reportNumber");
        REPRESENTATIVEOFPAGE = VF.createIRI("http://schema.org/representativeOfPage");
        REQUIREDCOLLATERAL = VF.createIRI("http://schema.org/requiredCollateral");
        REQUIREDGENDER = VF.createIRI("http://schema.org/requiredGender");
        REQUIREDMAXAGE = VF.createIRI("http://schema.org/requiredMaxAge");
        REQUIREDMINAGE = VF.createIRI("http://schema.org/requiredMinAge");
        REQUIREDQUANTITY = VF.createIRI("http://schema.org/requiredQuantity");
        REQUIREMENTS = VF.createIRI("http://schema.org/requirements");
        REQUIRESSUBSCRIPTION = VF.createIRI("http://schema.org/requiresSubscription");
        RESERVATIONFOR = VF.createIRI("http://schema.org/reservationFor");
        RESERVATIONID = VF.createIRI("http://schema.org/reservationId");
        RESERVATIONSTATUS = VF.createIRI("http://schema.org/reservationStatus");
        RESERVEDTICKET = VF.createIRI("http://schema.org/reservedTicket");
        RESPONSIBILITIES = VF.createIRI("http://schema.org/responsibilities");
        RESULTCOMMENT = VF.createIRI("http://schema.org/resultComment");
        RESULTREVIEW = VF.createIRI("http://schema.org/resultReview");
        REVIEWBODY = VF.createIRI("http://schema.org/reviewBody");
        REVIEWCOUNT = VF.createIRI("http://schema.org/reviewCount");
        REVIEWRATING = VF.createIRI("http://schema.org/reviewRating");
        REVIEWEDBY = VF.createIRI("http://schema.org/reviewedBy");
        REVIEWS = VF.createIRI("http://schema.org/reviews");
        RSVPRESPONSE = VF.createIRI("http://schema.org/rsvpResponse");
        RUNTIME = VF.createIRI("http://schema.org/runtime");
        SALARYCURRENCY = VF.createIRI("http://schema.org/salaryCurrency");
        SAMEAS = VF.createIRI("http://schema.org/sameAs");
        SAMPLETYPE = VF.createIRI("http://schema.org/sampleType");
        SATURATEDFATCONTENT = VF.createIRI("http://schema.org/saturatedFatContent");
        SCHEDULEDPAYMENTDATE = VF.createIRI("http://schema.org/scheduledPaymentDate");
        SCHEDULEDTIME = VF.createIRI("http://schema.org/scheduledTime");
        SCHEMAVERSION = VF.createIRI("http://schema.org/schemaVersion");
        SCREENCOUNT = VF.createIRI("http://schema.org/screenCount");
        SCREENSHOT = VF.createIRI("http://schema.org/screenshot");
        SEASONNUMBER = VF.createIRI("http://schema.org/seasonNumber");
        SEASONS = VF.createIRI("http://schema.org/seasons");
        SEATNUMBER = VF.createIRI("http://schema.org/seatNumber");
        SEATROW = VF.createIRI("http://schema.org/seatRow");
        SEATSECTION = VF.createIRI("http://schema.org/seatSection");
        SEATINGTYPE = VF.createIRI("http://schema.org/seatingType");
        SECURITYSCREENING = VF.createIRI("http://schema.org/securityScreening");
        SEEKS = VF.createIRI("http://schema.org/seeks");
        SENDER = VF.createIRI("http://schema.org/sender");
        SERVERSTATUS = VF.createIRI("http://schema.org/serverStatus");
        SERVESCUISINE = VF.createIRI("http://schema.org/servesCuisine");
        SERVICEAUDIENCE = VF.createIRI("http://schema.org/serviceAudience");
        SERVICELOCATION = VF.createIRI("http://schema.org/serviceLocation");
        SERVICEOPERATOR = VF.createIRI("http://schema.org/serviceOperator");
        SERVICEPHONE = VF.createIRI("http://schema.org/servicePhone");
        SERVICEPOSTALADDRESS = VF.createIRI("http://schema.org/servicePostalAddress");
        SERVICESMSNUMBER = VF.createIRI("http://schema.org/serviceSmsNumber");
        SERVICETYPE = VF.createIRI("http://schema.org/serviceType");
        SERVICEURL = VF.createIRI("http://schema.org/serviceUrl");
        SERVINGSIZE = VF.createIRI("http://schema.org/servingSize");
        SHAREDCONTENT = VF.createIRI("http://schema.org/sharedContent");
        SIBLINGS = VF.createIRI("http://schema.org/siblings");
        SIGNIFICANTLINKS = VF.createIRI("http://schema.org/significantLinks");
        SKILLS = VF.createIRI("http://schema.org/skills");
        SKU = VF.createIRI("http://schema.org/sku");
        SMOKINGALLOWED = VF.createIRI("http://schema.org/smokingAllowed");
        SODIUMCONTENT = VF.createIRI("http://schema.org/sodiumContent");
        SOFTWAREADDON = VF.createIRI("http://schema.org/softwareAddOn");
        SOFTWAREHELP = VF.createIRI("http://schema.org/softwareHelp");
        SOFTWAREVERSION = VF.createIRI("http://schema.org/softwareVersion");
        SOURCEORGANIZATION = VF.createIRI("http://schema.org/sourceOrganization");
        SPATIAL = VF.createIRI("http://schema.org/spatial");
        SPECIALCOMMITMENTS = VF.createIRI("http://schema.org/specialCommitments");
        SPECIALOPENINGHOURSSPECIFICATION = VF.createIRI("http://schema.org/specialOpeningHoursSpecification");
        HAS_SPECIALTY = VF.createIRI("http://schema.org/specialty");
        SPORT = VF.createIRI("http://schema.org/sport");
        HAS_SPORTSACTIVITYLOCATION = VF.createIRI("http://schema.org/sportsActivityLocation");
        HAS_SPORTSEVENT = VF.createIRI("http://schema.org/sportsEvent");
        HAS_SPORTSTEAM = VF.createIRI("http://schema.org/sportsTeam");
        SPOUSE = VF.createIRI("http://schema.org/spouse");
        STARRATING = VF.createIRI("http://schema.org/starRating");
        STARTDATE = VF.createIRI("http://schema.org/startDate");
        STARTTIME = VF.createIRI("http://schema.org/startTime");
        STEERINGPOSITION = VF.createIRI("http://schema.org/steeringPosition");
        STEPVALUE = VF.createIRI("http://schema.org/stepValue");
        STEPS = VF.createIRI("http://schema.org/steps");
        STORAGEREQUIREMENTS = VF.createIRI("http://schema.org/storageRequirements");
        STREETADDRESS = VF.createIRI("http://schema.org/streetAddress");
        SUBEVENTS = VF.createIRI("http://schema.org/subEvents");
        SUBRESERVATION = VF.createIRI("http://schema.org/subReservation");
        SUBTITLELANGUAGE = VF.createIRI("http://schema.org/subtitleLanguage");
        SUCCESSOROF = VF.createIRI("http://schema.org/successorOf");
        SUGARCONTENT = VF.createIRI("http://schema.org/sugarContent");
        SUGGESTEDGENDER = VF.createIRI("http://schema.org/suggestedGender");
        SUGGESTEDMAXAGE = VF.createIRI("http://schema.org/suggestedMaxAge");
        SUGGESTEDMINAGE = VF.createIRI("http://schema.org/suggestedMinAge");
        SUITABLEFORDIET = VF.createIRI("http://schema.org/suitableForDiet");
        SUPPORTINGDATA = VF.createIRI("http://schema.org/supportingData");
        SURFACE = VF.createIRI("http://schema.org/surface");
        TARGET = VF.createIRI("http://schema.org/target");
        TARGETDESCRIPTION = VF.createIRI("http://schema.org/targetDescription");
        TARGETNAME = VF.createIRI("http://schema.org/targetName");
        TARGETPLATFORM = VF.createIRI("http://schema.org/targetPlatform");
        TARGETPRODUCT = VF.createIRI("http://schema.org/targetProduct");
        TARGETURL = VF.createIRI("http://schema.org/targetUrl");
        TAXID = VF.createIRI("http://schema.org/taxID");
        TELEPHONE = VF.createIRI("http://schema.org/telephone");
        TEMPORAL = VF.createIRI("http://schema.org/temporal");
        HAS_TEXT = VF.createIRI("http://schema.org/text");
        THUMBNAIL = VF.createIRI("http://schema.org/thumbnail");
        THUMBNAILURL = VF.createIRI("http://schema.org/thumbnailUrl");
        TICKERSYMBOL = VF.createIRI("http://schema.org/tickerSymbol");
        TICKETNUMBER = VF.createIRI("http://schema.org/ticketNumber");
        TICKETTOKEN = VF.createIRI("http://schema.org/ticketToken");
        TICKETEDSEAT = VF.createIRI("http://schema.org/ticketedSeat");
        TIMEREQUIRED = VF.createIRI("http://schema.org/timeRequired");
        TITLE = VF.createIRI("http://schema.org/title");
        TOLOCATION = VF.createIRI("http://schema.org/toLocation");
        TORECIPIENT = VF.createIRI("http://schema.org/toRecipient");
        TOOL = VF.createIRI("http://schema.org/tool");
        TOTALPAYMENTDUE = VF.createIRI("http://schema.org/totalPaymentDue");
        TOTALPRICE = VF.createIRI("http://schema.org/totalPrice");
        TOTALTIME = VF.createIRI("http://schema.org/totalTime");
        TOURISTTYPE = VF.createIRI("http://schema.org/touristType");
        TRACKINGNUMBER = VF.createIRI("http://schema.org/trackingNumber");
        TRACKINGURL = VF.createIRI("http://schema.org/trackingUrl");
        TRACKS = VF.createIRI("http://schema.org/tracks");
        TRAILER = VF.createIRI("http://schema.org/trailer");
        TRAINNAME = VF.createIRI("http://schema.org/trainName");
        TRAINNUMBER = VF.createIRI("http://schema.org/trainNumber");
        TRANSFATCONTENT = VF.createIRI("http://schema.org/transFatContent");
        TRANSCRIPT = VF.createIRI("http://schema.org/transcript");
        TRANSLATOR = VF.createIRI("http://schema.org/translator");
        TYPEOFBED = VF.createIRI("http://schema.org/typeOfBed");
        TYPEOFGOOD = VF.createIRI("http://schema.org/typeOfGood");
        TYPICALAGERANGE = VF.createIRI("http://schema.org/typicalAgeRange");
        UNDERNAME = VF.createIRI("http://schema.org/underName");
        UNITCODE = VF.createIRI("http://schema.org/unitCode");
        UNITTEXT = VF.createIRI("http://schema.org/unitText");
        UNSATURATEDFATCONTENT = VF.createIRI("http://schema.org/unsaturatedFatContent");
        UPLOADDATE = VF.createIRI("http://schema.org/uploadDate");
        UPVOTECOUNT = VF.createIRI("http://schema.org/upvoteCount");
        HAS_URL = VF.createIRI("http://schema.org/url");
        URLTEMPLATE = VF.createIRI("http://schema.org/urlTemplate");
        USERINTERACTIONCOUNT = VF.createIRI("http://schema.org/userInteractionCount");
        VALIDFOR = VF.createIRI("http://schema.org/validFor");
        VALIDFROM = VF.createIRI("http://schema.org/validFrom");
        VALIDIN = VF.createIRI("http://schema.org/validIn");
        VALIDTHROUGH = VF.createIRI("http://schema.org/validThrough");
        VALIDUNTIL = VF.createIRI("http://schema.org/validUntil");
        VALUE = VF.createIRI("http://schema.org/value");
        VALUEADDEDTAXINCLUDED = VF.createIRI("http://schema.org/valueAddedTaxIncluded");
        VALUEMAXLENGTH = VF.createIRI("http://schema.org/valueMaxLength");
        VALUEMINLENGTH = VF.createIRI("http://schema.org/valueMinLength");
        VALUENAME = VF.createIRI("http://schema.org/valueName");
        VALUEPATTERN = VF.createIRI("http://schema.org/valuePattern");
        VALUEREFERENCE = VF.createIRI("http://schema.org/valueReference");
        VALUEREQUIRED = VF.createIRI("http://schema.org/valueRequired");
        VATID = VF.createIRI("http://schema.org/vatID");
        VEHICLECONFIGURATION = VF.createIRI("http://schema.org/vehicleConfiguration");
        VEHICLEENGINE = VF.createIRI("http://schema.org/vehicleEngine");
        VEHICLEIDENTIFICATIONNUMBER = VF.createIRI("http://schema.org/vehicleIdentificationNumber");
        VEHICLEINTERIORCOLOR = VF.createIRI("http://schema.org/vehicleInteriorColor");
        VEHICLEINTERIORTYPE = VF.createIRI("http://schema.org/vehicleInteriorType");
        VEHICLEMODELDATE = VF.createIRI("http://schema.org/vehicleModelDate");
        VEHICLESEATINGCAPACITY = VF.createIRI("http://schema.org/vehicleSeatingCapacity");
        VEHICLESPECIALUSAGE = VF.createIRI("http://schema.org/vehicleSpecialUsage");
        VEHICLETRANSMISSION = VF.createIRI("http://schema.org/vehicleTransmission");
        VENDOR = VF.createIRI("http://schema.org/vendor");
        VERSION = VF.createIRI("http://schema.org/version");
        VIDEO = VF.createIRI("http://schema.org/video");
        VIDEOFORMAT = VF.createIRI("http://schema.org/videoFormat");
        VIDEOFRAMESIZE = VF.createIRI("http://schema.org/videoFrameSize");
        VIDEOQUALITY = VF.createIRI("http://schema.org/videoQuality");
        VOLUMENUMBER = VF.createIRI("http://schema.org/volumeNumber");
        HAS_WARRANTYPROMISE = VF.createIRI("http://schema.org/warrantyPromise");
        HAS_WARRANTYSCOPE = VF.createIRI("http://schema.org/warrantyScope");
        WEBCHECKINTIME = VF.createIRI("http://schema.org/webCheckinTime");
        WEIGHT = VF.createIRI("http://schema.org/weight");
        WIDTH = VF.createIRI("http://schema.org/width");
        WINNER = VF.createIRI("http://schema.org/winner");
        WORDCOUNT = VF.createIRI("http://schema.org/wordCount");
        WORKHOURS = VF.createIRI("http://schema.org/workHours");
        WORKLOCATION = VF.createIRI("http://schema.org/workLocation");
        WORKPERFORMED = VF.createIRI("http://schema.org/workPerformed");
        WORKPRESENTED = VF.createIRI("http://schema.org/workPresented");
        WORKSFOR = VF.createIRI("http://schema.org/worksFor");
        WORSTRATING = VF.createIRI("http://schema.org/worstRating");
        YEARLYREVENUE = VF.createIRI("http://schema.org/yearlyRevenue");
        YEARSINOPERATION = VF.createIRI("http://schema.org/yearsInOperation");
        ABOUT = VF.createIRI("http://schema.org/about");
        ACTIONAPPLICATION = VF.createIRI("http://schema.org/actionApplication");
        ACTIONOPTION = VF.createIRI("http://schema.org/actionOption");
        ACTOR = VF.createIRI("http://schema.org/actor");
        ALBUM = VF.createIRI("http://schema.org/album");
        ALBUMRELEASE = VF.createIRI("http://schema.org/albumRelease");
        ALUMNI = VF.createIRI("http://schema.org/alumni");
        ALUMNIOF = VF.createIRI("http://schema.org/alumniOf");
        ARTWORKSURFACE = VF.createIRI("http://schema.org/artworkSurface");
        ATTENDEE = VF.createIRI("http://schema.org/attendee");
        HAS_AUDIENCE = VF.createIRI("http://schema.org/audience");
        AVAILABLEONDEVICE = VF.createIRI("http://schema.org/availableOnDevice");
        AWARD = VF.createIRI("http://schema.org/award");
        BLOGPOST = VF.createIRI("http://schema.org/blogPost");
        BROKER = VF.createIRI("http://schema.org/broker");
        CODESAMPLETYPE = VF.createIRI("http://schema.org/codeSampleType");
        COLLEAGUE = VF.createIRI("http://schema.org/colleague");
        HAS_CONTACTPOINT = VF.createIRI("http://schema.org/contactPoint");
        CONTAINSPLACE = VF.createIRI("http://schema.org/containsPlace");
        CONTAINSSEASON = VF.createIRI("http://schema.org/containsSeason");
        CONTENTLOCATION = VF.createIRI("http://schema.org/contentLocation");
        HAS_DATASET = VF.createIRI("http://schema.org/dataset");
        DESCRIPTION = VF.createIRI("http://schema.org/description");
        DIRECTOR = VF.createIRI("http://schema.org/director");
        HAS_DURATION = VF.createIRI("http://schema.org/duration");
        EMPLOYEE = VF.createIRI("http://schema.org/employee");
        ENCODING = VF.createIRI("http://schema.org/encoding");
        ENCODINGFORMAT = VF.createIRI("http://schema.org/encodingFormat");
        HAS_EPISODE = VF.createIRI("http://schema.org/episode");
        HAS_EVENT = VF.createIRI("http://schema.org/event");
        EXAMPLEOFWORK = VF.createIRI("http://schema.org/exampleOfWork");
        EXECUTABLELIBRARYNAME = VF.createIRI("http://schema.org/executableLibraryName");
        EXERCISECOURSE = VF.createIRI("http://schema.org/exerciseCourse");
        FOUNDER = VF.createIRI("http://schema.org/founder");
        HAS_GAME = VF.createIRI("http://schema.org/game");
        HAS_GAMESERVER = VF.createIRI("http://schema.org/gameServer");
        HASMENU = VF.createIRI("http://schema.org/hasMenu");
        INLANGUAGE = VF.createIRI("http://schema.org/inLanguage");
        INCENTIVECOMPENSATION = VF.createIRI("http://schema.org/incentiveCompensation");
        INTERACTIONSTATISTIC = VF.createIRI("http://schema.org/interactionStatistic");
        ISACCESSIBLEFORFREE = VF.createIRI("http://schema.org/isAccessibleForFree");
        ISBASEDON = VF.createIRI("http://schema.org/isBasedOn");
        JOBBENEFITS = VF.createIRI("http://schema.org/jobBenefits");
        MAINENTITY = VF.createIRI("http://schema.org/mainEntity");
        MAINENTITYOFPAGE = VF.createIRI("http://schema.org/mainEntityOfPage");
        MAKESOFFER = VF.createIRI("http://schema.org/makesOffer");
        OFFEREDBY = VF.createIRI("http://schema.org/offeredBy");
        PARENT = VF.createIRI("http://schema.org/parent");
        PARTOFSERIES = VF.createIRI("http://schema.org/partOfSeries");
        PAYMENTDUEDATE = VF.createIRI("http://schema.org/paymentDueDate");
        PERFORMTIME = VF.createIRI("http://schema.org/performTime");
        PERFORMER = VF.createIRI("http://schema.org/performer");
        PHOTO = VF.createIRI("http://schema.org/photo");
        PROVIDER = VF.createIRI("http://schema.org/provider");
        RECIPEINGREDIENT = VF.createIRI("http://schema.org/recipeIngredient");
        RECORDEDAS = VF.createIRI("http://schema.org/recordedAs");
        RECORDEDAT = VF.createIRI("http://schema.org/recordedAt");
        RECORDEDIN = VF.createIRI("http://schema.org/recordedIn");
        RECORDINGOF = VF.createIRI("http://schema.org/recordingOf");
        RELEASEOF = VF.createIRI("http://schema.org/releaseOf");
        HAS_REVIEW = VF.createIRI("http://schema.org/review");
        ROLENAME = VF.createIRI("http://schema.org/roleName");
        RUNTIMEPLATFORM = VF.createIRI("http://schema.org/runtimePlatform");
        HAS_SEASON = VF.createIRI("http://schema.org/season");
        SERIALNUMBER = VF.createIRI("http://schema.org/serialNumber");
        SERVICEAREA = VF.createIRI("http://schema.org/serviceArea");
        SERVICEOUTPUT = VF.createIRI("http://schema.org/serviceOutput");
        SIBLING = VF.createIRI("http://schema.org/sibling");
        SIGNIFICANTLINK = VF.createIRI("http://schema.org/significantLink");
        SOFTWAREREQUIREMENTS = VF.createIRI("http://schema.org/softwareRequirements");
        SPATIALCOVERAGE = VF.createIRI("http://schema.org/spatialCoverage");
        SPONSOR = VF.createIRI("http://schema.org/sponsor");
        SUBORGANIZATION = VF.createIRI("http://schema.org/subOrganization");
        SUGGESTEDANSWER = VF.createIRI("http://schema.org/suggestedAnswer");
        SUPEREVENT = VF.createIRI("http://schema.org/superEvent");
        TARGETCOLLECTION = VF.createIRI("http://schema.org/targetCollection");
        TRACK = VF.createIRI("http://schema.org/track");
        WARRANTY = VF.createIRI("http://schema.org/warranty");
        WORKEXAMPLE = VF.createIRI("http://schema.org/workExample");
        YIELD = VF.createIRI("http://schema.org/yield");
        COMPETITOR = VF.createIRI("http://schema.org/competitor");
        CONTAINEDINPLACE = VF.createIRI("http://schema.org/containedInPlace");
        HASMAP = VF.createIRI("http://schema.org/hasMap");
        IMAGE = VF.createIRI("http://schema.org/image");
        MATERIAL = VF.createIRI("http://schema.org/material");
        MEMBEROF = VF.createIRI("http://schema.org/memberOf");
        PARENTORGANIZATION = VF.createIRI("http://schema.org/parentOrganization");
        RESULT = VF.createIRI("http://schema.org/result");
        SELLER = VF.createIRI("http://schema.org/seller");
        STEP = VF.createIRI("http://schema.org/step");
        SUBEVENT = VF.createIRI("http://schema.org/subEvent");
        SUPPLY = VF.createIRI("http://schema.org/supply");
        TEMPORALCOVERAGE = VF.createIRI("http://schema.org/temporalCoverage");
        WORKFEATURED = VF.createIRI("http://schema.org/workFeatured");
        AREASERVED = VF.createIRI("http://schema.org/areaServed");
        INCLUDEDINDATACATALOG = VF.createIRI("http://schema.org/includedInDataCatalog");
        MEMBER = VF.createIRI("http://schema.org/member");
        RECIPIENT = VF.createIRI("http://schema.org/recipient");
        HASPART = VF.createIRI("http://schema.org/hasPart");
        ISPARTOF = VF.createIRI("http://schema.org/isPartOf");
        POSITION = VF.createIRI("http://schema.org/position");
        INSTRUMENT = VF.createIRI("http://schema.org/instrument");
        OBJECT = VF.createIRI("http://schema.org/object");
        LOCATION = VF.createIRI("http://schema.org/location");
        PARTICIPANT = VF.createIRI("http://schema.org/participant");
        IDENTIFIER = VF.createIRI("http://schema.org/identifier");

    }

    /** Utility class; private constructor to prevent instance being created. */
    private SCHEMAORG() {
    }
}