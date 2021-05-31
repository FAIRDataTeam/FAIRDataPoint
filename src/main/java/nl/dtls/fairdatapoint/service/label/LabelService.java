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
package nl.dtls.fairdatapoint.service.label;

import static java.util.function.Predicate.isEqual;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import com.github.fairdevkit.rdf.resolver.api.ResourceResolver;
import com.github.fairdevkit.rdf.resolver.core.ContentNegotiationStrategy;
import com.github.fairdevkit.rdf.resolver.core.CoreResourceResolver;
import com.github.fairdevkit.rdf.resolver.core.PathExtensionStrategy;
import java.io.IOException;
import java.util.Optional;
import nl.dtls.fairdatapoint.api.dto.label.LabelDTO;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.springframework.stereotype.Service;

@Service
public class LabelService {
    private ResourceResolver resolver;

    public LabelService() {
        var resolver = new CoreResourceResolver();
        resolver.register(new ContentNegotiationStrategy());
        resolver.register(new PathExtensionStrategy());

        this.resolver = resolver;
    }

    public Optional<LabelDTO> getLabel(String iri) {
        try {
            var subject = i(iri);

            return resolver.resolveResource(iri)
                    .flatMap(model -> resolvePrefLabelByLanguage(model, subject, "en")
                            .or(() -> resolvePrefLabel(model, subject))
                            .or(() -> resolveLabelByLanguage(model, subject, "en"))
                            .or(() -> resolveLabel(model, subject))
                    )
                    .map(literal -> new LabelDTO(literal.getLabel(), literal.getLanguage().orElse("")));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Optional<Literal> resolvePrefLabelByLanguage(Model model, IRI iri, String lang) {
        return getPropertyLiteralByLanguage(model, iri, SKOS.PREF_LABEL, lang);
    }

    private Optional<Literal> resolvePrefLabel(Model model, IRI iri) {
        return Models.getPropertyLiteral(model, iri, SKOS.PREF_LABEL);
    }

    private Optional<Literal> resolveLabelByLanguage(Model model, IRI iri, String lang) {
        return getPropertyLiteralByLanguage(model, iri, RDFS.LABEL, lang);
    }

    private Optional<Literal> resolveLabel(Model model, IRI iri) {
        return Models.getPropertyLiteral(model, iri, RDFS.LABEL);
    }

    private static Optional<Literal> getPropertyLiteralByLanguage(Model model, IRI subject, IRI predicate, String lang) {
        return Models.getPropertyLiterals(model, subject, predicate)
                .stream()
                .filter(literal -> literal.getLanguage().filter(isEqual(lang)).isPresent())
                .findFirst();
    }
}
