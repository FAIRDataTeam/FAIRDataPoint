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
import static nl.dtls.fairdatapoint.config.CacheConfig.LABEL_CACHE;
import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import org.fairdatateam.rdf.resolver.api.ResourceResolver;
import org.fairdatateam.rdf.resolver.core.ContentNegotiationStrategy;
import org.fairdatateam.rdf.resolver.core.CoreResourceResolver;
import org.fairdatateam.rdf.resolver.core.PathExtensionStrategy;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import nl.dtls.fairdatapoint.api.dto.label.LabelDTO;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.Models;
import org.eclipse.rdf4j.model.vocabulary.RDFS;
import org.eclipse.rdf4j.model.vocabulary.SKOS;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@CacheConfig(cacheNames = LABEL_CACHE)
public class LabelService {
    private final ResourceResolver resolver;

    public LabelService() {
        final CoreResourceResolver defaultResolver = new CoreResourceResolver();
        defaultResolver.register(new ContentNegotiationStrategy());
        defaultResolver.register(new PathExtensionStrategy());

        this.resolver = defaultResolver;
    }

    @Cacheable
    public Optional<LabelDTO> getLabel(String iri, String lang) {
        try {
            final IRI subject = i(iri);
            return resolver.resolveResource(iri)
                    .flatMap(model -> {
                        return getPropertyLiteralByLanguage(model, subject, SKOS.PREF_LABEL, lang)
                                .or(() -> getPropertyLiteralWithoutLanguage(model, subject, SKOS.PREF_LABEL))
                                .or(() -> getPropertyLiteralByLanguage(model, subject, RDFS.LABEL, lang))
                                .or(() -> getPropertyLiteralWithoutLanguage(model, subject, RDFS.LABEL));
                    })
                    .map(literal -> new LabelDTO(literal.getLabel(), literal.getLanguage().orElse("")));
        }
        catch (Exception exception) {
            log.warn("Unable to resolve label for {} (lang {}): {}",
                    iri, lang, exception.getMessage());
            return Optional.empty();
        }
    }

    private static Optional<Literal> getPropertyLiteralByLanguage(
            Model model, IRI subject, IRI predicate, String lang
    ) {
        return Models.getPropertyLiterals(model, subject, predicate)
                .stream()
                .filter(literal -> literal.getLanguage().filter(isEqual(lang)).isPresent())
                .findFirst();
    }

    private static Optional<Literal> getPropertyLiteralWithoutLanguage(
            Model model, IRI subject, IRI predicate
    ) {
        return Models.getPropertyLiterals(model, subject, predicate)
                .stream()
                .filter(literal -> literal.getLanguage().isEmpty())
                .findFirst();
    }
}
