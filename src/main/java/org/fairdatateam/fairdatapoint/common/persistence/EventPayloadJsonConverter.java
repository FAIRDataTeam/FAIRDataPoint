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
import org.fairdatateam.fairdatapoint.index.event.EventPayload;

/**
 * Stores what an Index event recorded as a JSON object in a text column. Only one of the five
 * branches of a payload is ever filled, and each branch has a shape of its own - nested HTTP
 * exchanges with their headers, harvested repository metadata - so the payload is kept as the
 * document it always was rather than spread over columns that would be null for every other kind
 * of event.
 *
 * <p>An absent payload reads back as an empty one rather than as {@code null}: the column is
 * declared NOT NULL and {@code Event} reads its branches without checking.</p>
 *
 * <p>Not applied automatically: only the columns declared as text in the baseline schema may use
 * it, and they say so with {@code @Convert}.</p>
 */
@Converter(autoApply = false)
public class EventPayloadJsonConverter implements AttributeConverter<EventPayload, String> {

    @Override
    public String convertToDatabaseColumn(final EventPayload attribute) {
        if (attribute == null) {
            return JsonColumnMapper.INSTANCE.writeValueAsString(new EventPayload());
        }
        return JsonColumnMapper.INSTANCE.writeValueAsString(attribute);
    }

    @Override
    public EventPayload convertToEntityAttribute(final String dbData) {
        if (dbData == null) {
            return new EventPayload();
        }
        return JsonColumnMapper.INSTANCE.readValue(dbData, EventPayload.class);
    }
}
