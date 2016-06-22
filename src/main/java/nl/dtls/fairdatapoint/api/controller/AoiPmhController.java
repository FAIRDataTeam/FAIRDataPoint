package nl.dtls.fairdatapoint.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nl.dtls.fairdatapoint.aoipmh.writables.Request;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static nl.dtls.fairdatapoint.aoipmh.parameters.OAIRequest.Parameter.*;
import com.lyncode.xoai.exceptions.InvalidResumptionTokenException;
import com.lyncode.xoai.services.api.DateProvider;
import java.util.ArrayList;
import java.util.HashMap;
import nl.dtls.fairdatapoint.aoipmh.handlers.GetRecordHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.IdentifyHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListIdentifiersHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListMetadataFormatsHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListRecordsHandler;
import nl.dtls.fairdatapoint.aoipmh.handlers.ListSetsHandler;
import org.springframework.beans.factory.annotation.Autowired;
import nl.dtls.fairdatapoint.aoipmh.writables.OAIPMH;
import com.lyncode.xoai.dataprovider.exceptions.*;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import nl.dtls.fairdatapoint.aoipmh.handlers.ErrorHandler;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAICompiledRequest;
import nl.dtls.fairdatapoint.aoipmh.parameters.OAIRequest;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Shamanou van Leeuwen
 */
@RestController
@Api(description = "FDP oai-pmh access point")
@RequestMapping(value = "/oai")
public class AoiPmhController {
    @Autowired
    private DateProvider dateProvider;
    @Autowired
    private IdentifyHandler identifyHandler;
    @Autowired
    private ListSetsHandler listSetsHandler;
    @Autowired
    private ListMetadataFormatsHandler listMetadataFormatsHandler;
    @Autowired
    private GetRecordHandler getRecordHandler;
    @Autowired
    private ListIdentifiersHandler listIdentifiersHandler;
    @Autowired
    private ListRecordsHandler listRecordsHandler;
    @Autowired
    private ErrorHandler errorsHandler;
    @Autowired
    private String baseURI;
    @Autowired
    private String identifier;
    private final String[] verbs  = new String[]{"Identify","ListSets","ListMetadataFormats","GetRecord","ListIdentifiers","ListRecords"};
    private final static Logger LOGGER = LogManager.getLogger(AoiPmhController.class);
    
    @ApiOperation(value = "aoi-pmh protocol implementation")
    @RequestMapping(
        method = RequestMethod.GET,
        produces = { "text/turtle", "application/xml" } 
    )

    public String oai(@RequestParam(value="verb") String verb, @RequestParam(value="itemIdentifier", required = false) String itemIdentifier,@RequestParam(value="metadataPrefix", required = false) String metadataPrefix, 
            final HttpServletRequest httpRequest) {
        HashMap map  = new HashMap();
        ArrayList tmp = new ArrayList<>();
        int z = 0; 
        for (String x : verbs ){
            if (!verb.equals(x)){
                z++;
            }
        }
        tmp.add(verb);
        map.put("verb", tmp);
        if (metadataPrefix != null){
            tmp = new ArrayList();
            tmp.add(metadataPrefix);
            map.put("metadataPrefix", tmp);
        }

        if (itemIdentifier != null){
            tmp = new ArrayList();
            tmp.add(itemIdentifier);
            map.put("identifier", tmp);
        }
        
        OAIPMH response = new OAIPMH().withResponseDate(dateProvider.now());
        response.setContentType(httpRequest.getHeader(HttpHeaders.ACCEPT));
        if (z == verbs.length){
            try {
                return response.withError(errorsHandler.handle(new HandlerException("Wrong verb") {}));
            } catch (OAIException ex) {
                java.util.logging.Logger.getLogger(AoiPmhController.class.getName()).log(Level.SEVERE, null, ex);
                return ex.getMessage();
            }
        }
        OAIRequest requestParameters = new OAIRequest(map);
        Request request = new Request(this.baseURI)
                    .withIdentifier(this.identifier)
                    .withVerbType(requestParameters.get(Verb))
                    .withResumptionToken(requestParameters.get(ResumptionToken))
                    .withMetadataPrefix(requestParameters.get(MetadataPrefix))
                    .withSet(requestParameters.get(Set))
                    .withFrom(requestParameters.get(From))
                    .withUntil(requestParameters.get(Until));
        
        response.withRequest(request);
        try {
            OAICompiledRequest parameters = compileParameters(requestParameters);
            switch (request.getVerbType()) {
                case Identify:
                    response.withVerb(identifyHandler.handle(parameters));
                    break;
                case ListSets:
                    response.withVerb(listSetsHandler.handle(parameters));
                    break;
                case ListMetadataFormats:
                    response.withVerb(listMetadataFormatsHandler.handle(parameters));
                    break;
                case GetRecord:
                    response.withVerb(getRecordHandler.handle(parameters));
                    break;
                case ListIdentifiers:
                    response.withVerb(listIdentifiersHandler.handle(parameters));
                    break;
                case ListRecords:
                    response.withVerb(listRecordsHandler.handle(parameters));
                    break;
            }
        } catch (HandlerException e) {
            try {
                return response.withError(errorsHandler.handle(e));
            } catch (OAIException ex) {
                java.util.logging.Logger.getLogger(AoiPmhController.class.getName()).log(Level.SEVERE, null, ex);
                return ex.getMessage();
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(AoiPmhController.class.getName()).log(Level.SEVERE, null, ex);
            return ex.getMessage();
        }
        return response.toString();
    }
    
    private OAICompiledRequest compileParameters(OAIRequest requestParameters) throws 
            IllegalVerbException, UnknownParameterException, BadArgumentException, 
            DuplicateDefinitionException, BadResumptionToken {
        try {
            return requestParameters.compile();
        } catch (InvalidResumptionTokenException e) {
            throw new BadResumptionToken("The resumption token is invalid");
        }
    }
}