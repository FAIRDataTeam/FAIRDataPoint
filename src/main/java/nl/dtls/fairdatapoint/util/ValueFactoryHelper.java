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
package nl.dtls.fairdatapoint.util;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Shortcuts to simplify code that needs to create IRIs, Literals, Statements, etc.
 */
public class ValueFactoryHelper {

    private static final ValueFactory VF = SimpleValueFactory.getInstance();

    public static IRI i(String iri) {
        return VF.createIRI(iri);
    }

    public static IRI i(Optional<String> oIri) {
        if (oIri.isEmpty()) {
            return null;
        }
        return i(oIri.get());
    }

    public static Literal l(String literal) {
        return VF.createLiteral(literal);
    }

    public static Literal l(Optional<String> oLiteral) {
        if (oLiteral.isEmpty()) {
            return null;
        }
        return l(oLiteral.get());
    }

    public static Literal l(float literal) {
        return VF.createLiteral(literal);
    }

    public static Literal l(LocalDateTime literal) {
        return VF.createLiteral(literal.toString(), XMLSchema.DATETIME);
    }

    public static Statement s(Resource subject, IRI predicate, Value object, Resource context) {
        return VF.createStatement(subject, predicate, object, context);
    }
}
