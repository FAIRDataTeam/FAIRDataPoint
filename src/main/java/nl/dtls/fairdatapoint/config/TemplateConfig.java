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
package nl.dtls.fairdatapoint.config;

import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import nl.dtls.fairdatapoint.Profiles;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;


/**
 * This class defines and registers helpers for Handlebars templates.
 */
@Configuration
@Profile(value = Profiles.NON_TESTING)
public class TemplateConfig {

    @Autowired
    private HandlebarsViewResolver handlebarsViewResolver;

    @PostConstruct
    public void registerHelper() {
        handlebarsViewResolver.registerHelper("length", TemplateConfig::length);
        handlebarsViewResolver.registerHelper("literal", TemplateConfig::literal);
        handlebarsViewResolver.registerHelper("simplifyUrl", TemplateConfig::simplifyUrl);
        handlebarsViewResolver.registerHelper("stringToDate", TemplateConfig::stringToDate);
        handlebarsViewResolver.registerHelper("trim", TemplateConfig::trim);
    }

    /**
     * Helper to return a length of the given collection.
     *
     * Usage in template:
     *
     * {{length myCollection}}
     *
     * @param collection Given collection
     * @param options    Options are not used for this helper
     * @return Length of the collection
     */
    private static Integer length(Collection collection, Options options) {
        if (collection != null) {
            return collection.size();
        }
        return 0;
    }

    /**
     * Helper to get a label of the given literal.
     *
     * Usage in template:
     *
     * {{literal myLiteral}}
     *
     * @param literal Given literal
     * @param options Options are not used for this helper
     * @return Literal label or empty string if the literal is null
     */
    private static String literal(Literal literal, Options options) {
        if (literal != null) {
            return literal.getLabel();
        }
        return "";
    }

    /**
     * Helper that returns only the last part of the given URL to make it more readable. E.g.:
     *
     * http://rdflicense.appspot.com/rdflicense/cc-by-nc-nd3.0 -> cc-by-nc-nd3.0
     *
     * Usage in template:
     *
     * {{simplifyUrl myUrl}}
     *
     * @param iri     Given URL
     * @param options Options are not used for this helper
     * @return Last part of the URL or empty string
     */
    private static String simplifyUrl(IRI iri, Options options) {
        if (iri != null) {
            String[] parts = iri.toString().split("/");
            if (parts.length > 0) {
                return parts[parts.length - 1];
            }
        }
        return "";
    }

    /**
     * Helper to convert a datetime string used in FDP to actual Date object.
     *
     * Usage in template:
     *
     * {{stringToDate myString}}
     *
     * This can be easily chained with Handlebars dateFormat helper:
     *
     * {{dateFormat (stringToDate myString) "dd-MM-YYYY"}}
     *
     * @param string  String with datetime format yyyy-MM-dd'T'HH:mm:ss.SSSZ
     * @param options Options are not used for this helper
     * @return Date object created from the given string
     */
    private static Date stringToDate(String string, Options options) {
        try {
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return format.parse(string);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * Helper to trim given string to not exceed given limit. If the given string is longer than the
     * limit, it is trimmed and "..." is added at the end. If the string is shorter or no limit is
     * given, the original string is returned.
     *
     * Usage in template:
     *
     * {{trim myString limit=180}}
     *
     * @param string  Given string
     * @param options Options can contain
     * @return Trimmed string
     */
    private static String trim(String string, Options options) {
        Integer limit = options.hash("limit");

        if (limit == null || string.length() <= limit) {
            return string;
        }

        return string.substring(0, limit) + "...";
    }
}
