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
import tools.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores a map of strings as a JSON object in a text column. The Index harvests whatever
 * properties a remote FAIR Data Point happens to describe itself with, so the set of keys is not
 * known in advance and cannot become a set of columns; a JSON object is the shape the map already
 * has in the REST API.
 *
 * <p>An absent map reads back as an empty one rather than as {@code null}: the columns using this
 * converter are declared NOT NULL, and callers iterate the map without checking.</p>
 *
 * <p>Not applied automatically: only the columns declared as text in the baseline schema may use
 * it, and they say so with {@code @Convert}.</p>
 */
@Converter(autoApply = false)
public class StringMapJsonConverter implements AttributeConverter<Map<String, String>, String> {

    private static final TypeReference<Map<String, String>> TYPE = new TypeReference<>() {
    };

    @Override
    public String convertToDatabaseColumn(final Map<String, String> attribute) {
        if (attribute == null) {
            return JsonColumnMapper.INSTANCE.writeValueAsString(new HashMap<String, String>());
        }
        return JsonColumnMapper.INSTANCE.writeValueAsString(attribute);
    }

    @Override
    public Map<String, String> convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return new HashMap<>();
        }
        return JsonColumnMapper.INSTANCE.readValue(dbData, TYPE);
    }
}
