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

import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.springmvc.HandlebarsViewResolver;
import nl.dtls.fairdatapoint.Profiles;
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

@Configuration
@Profile(value = Profiles.NON_TESTING)
public class TemplateConfig {

    @Autowired
    private HandlebarsViewResolver handlebarsViewResolver;

    private static Date stringToDate(String string) {
        try {
            final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return format.parse(string);
        } catch (ParseException e) {
            return new Date();
        }
    }

    private static String trim(String string, Integer limit) {
        if (limit == null || string.length() <= limit) {
            return string;
        }

        return string.substring(0, limit) + "...";
    }

    @PostConstruct
    public void registerHelper() {
        handlebarsViewResolver.registerHelper("literal",
                (Helper<Literal>) (literal, options) -> literal.getLabel());
        handlebarsViewResolver.registerHelper("stringToDate",
                (Helper<String>) (string, options) -> stringToDate(string));
        handlebarsViewResolver.registerHelper("length",
                (Helper<Collection>) (collection, options) -> collection.size());
        handlebarsViewResolver.registerHelper("trim",
                (Helper<String>) (string, options) -> trim(string, options.hash("limit")));
    }
}
