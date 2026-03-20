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

import java.util.List;
import java.util.stream.Collectors;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

public class RdfUtil {

    // ****************************************************
    // *** Subject
    // ****************************************************
    public static List<Resource> getSubjectsBy(Model model, IRI predicate, Resource object) {
        return model
                .filter(null, predicate, object)
                .stream()
                .map(Statement::getSubject)
                .collect(Collectors.toList());
    }

    public static Resource getSubjectBy(Model model, IRI predicate, Resource object) {
        final List<Resource> subjects = getSubjectsBy(model, predicate, object);
        return subjects.size() > 0 ? subjects.get(0) : null;
    }

    // ****************************************************
    // *** Objects
    // ****************************************************
    public static List<Value> getObjectsBy(Model model, Resource subject, IRI predicate) {
        return model
                .filter(subject, predicate, null)
                .stream()
                .map(Statement::getObject)
                .collect(Collectors.toList());
    }

    public static List<Value> getObjectsBy(Model model, String subject, String predicate) {
        return getObjectsBy(model, i(subject, model), i(predicate, model));
    }

    public static Value getObjectBy(Model model, Resource subject, IRI predicate) {
        final List<Value> objects = getObjectsBy(model, subject, predicate);
        return objects.size() > 0 ? objects.get(0) : null;
    }

    public static String getStringObjectBy(Model model, Resource subject, IRI predicate) {
        final Value object = getObjectBy(model, subject, predicate);
        return object != null ? object.stringValue() : null;
    }

    public static boolean containsObject(Model model, String subject, String predicate) {
        return getObjectsBy(model, subject, predicate).size() > 0;
    }

    // ****************************************************
    // *** Update
    // ****************************************************
    public static void update(Model model, Resource subj, IRI pred, Value obj) {
        model.remove(subj, pred, null);
        if (subj != null && pred != null && obj != null) {
            model.add(subj, pred, obj);
        }
    }

    public static <T extends Value> void update(
            Model model, Resource subj, IRI pred, List<T> list
    ) {
        model.remove(subj, pred, null);
        if (list != null) {
            list.forEach(obj -> {
                model.add(subj, pred, obj);
            });
        }
    }

    // ****************************************************
    // *** Other
    // ****************************************************
    public static void checkNotLiteral(Value val) {
        if (val instanceof Literal) {
            throw new IllegalArgumentException(
                    "Objects of accessRights statements expected to be IRI"
            );
        }
    }

    public static IRI removeLastPartOfIRI(IRI uri) {
        final String uriWithoutLastPart = uri.getNamespace();
        return i(uriWithoutLastPart.substring(0, uriWithoutLastPart.length() - 1));
    }

}
