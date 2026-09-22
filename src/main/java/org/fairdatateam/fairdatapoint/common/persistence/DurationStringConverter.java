/**
 * The MIT License
 * Copyright © 2017 FAIR Data Team
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
package org.fairdatateam.fairdatapoint.common.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;

/**
 * Stores a {@link Duration} as the ISO-8601 text it prints itself as, rather than as a number of
 * some unit. Every relational database this application runs on has a text type, none of them
 * agree on an interval type, and the REST API exchanges exactly the same ISO-8601 strings, so the
 * column holds what the API carries.
 *
 * <p>Not applied automatically: only the columns that are declared as text in the baseline schema
 * may use it, and they say so with {@code @Convert}.</p>
 */
@Converter(autoApply = false)
public class DurationStringConverter implements AttributeConverter<Duration, String> {

    @Override
    public String convertToDatabaseColumn(final Duration attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public Duration convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return null;
        }
        return Duration.parse(dbData);
    }
}
