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
package nl.dtls.fairdatapoint.api.controller.profile;

import io.swagger.v3.oas.annotations.tags.Tag;
import nl.dtls.fairdatapoint.config.ConverterConfig;
import nl.dtls.fairdatapoint.entity.exception.ResourceNotFoundException;
import nl.dtls.fairdatapoint.service.profile.ProfileService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.util.HttpUtil.getRequestURL;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

@Tag(name = "Client")
@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    @Qualifier("persistentUrl")
    private String persistentUrl;

    @Autowired
    private ProfileService profileService;

    @GetMapping(path = "/{uuid}", produces = {
            "text/turtle",
            "application/x-turtle",
            "text/n3",
            "text/rdf+n3",
            "application/ld+json",
            "application/rdf+xml",
            "application/xml",
            "text/xml",
    })
    public ResponseEntity<Model> getShapeContent(HttpServletRequest request, @PathVariable final String uuid)
            throws ResourceNotFoundException {
        IRI uri = i(getRequestURL(request, persistentUrl));
        Optional<Model> oDto = profileService.getProfileByUuid(uuid, uri);
        if (oDto.isPresent()) {
            return new ResponseEntity<>(oDto.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException(format("Profile '%s' doesn't exist", uuid));
        }
    }

}
