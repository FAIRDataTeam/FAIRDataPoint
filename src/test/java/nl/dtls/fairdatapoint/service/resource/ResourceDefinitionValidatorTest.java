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
package nl.dtls.fairdatapoint.service.resource;

import nl.dtls.fairdatapoint.database.mongo.migration.development.resource.data.ResourceDefinitionFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ResourceDefinitionRepository;
import nl.dtls.fairdatapoint.entity.exception.ValidationException;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinitionChild;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceDefinitionValidatorTest {

    @Mock
    private ResourceDefinitionRepository resourceDefinitionRepository;

    @Mock
    private ResourceDefinitionCache resourceDefinitionCache;

    @InjectMocks
    private ResourceDefinitionValidator resourceDefinitionValidator;

    @InjectMocks
    private ResourceDefinitionFixtures resourceDefinitionFixtures;

    @Test
    public void nameUniqueness() throws BindException {
        // GIVEN: Prepare reqDto
        ResourceDefinition reqDto = resourceDefinitionFixtures.fdpDefinition();
        reqDto.setChildren(List.of());

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.of(resourceDefinitionFixtures.fdpDefinition()));

        // WHEN:
        resourceDefinitionValidator.validate(reqDto);

        // THEN:
        // Nothing to check
    }

    @Test
    public void nameUniquenessBreach() {
        // GIVEN: Prepare reqDto
        ResourceDefinition reqDto = resourceDefinitionFixtures.ontologyDefinition();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.of(resourceDefinitionFixtures.fdpDefinition()));

        // WHEN:
        BindException exception = assertThrows(
                BindException.class,
                () -> resourceDefinitionValidator.validate(reqDto)
        );

        // THEN:
        assertThat(exception.getBindingResult().getFieldError("name"), is(notNullValue()));
    }

    @Test
    public void urlPrefixUniqueness() throws BindException {
        // GIVEN: Prepare reqDto
        ResourceDefinition reqDto = resourceDefinitionFixtures.fdpDefinition();
        reqDto.setChildren(List.of());

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.of(resourceDefinitionFixtures.fdpDefinition()));

        // WHEN:
        resourceDefinitionValidator.validate(reqDto);

        // THEN:
        // Nothing to check
    }

    @Test
    public void urlPrefixUniquenessBreach() {
        // GIVEN: Prepare reqDto
        ResourceDefinition reqDto = resourceDefinitionFixtures.ontologyDefinition();

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.of(resourceDefinitionFixtures.fdpDefinition()));

        // WHEN:
        BindException exception = assertThrows(
                BindException.class,
                () -> resourceDefinitionValidator.validate(reqDto)
        );

        // THEN:
        assertThat(exception.getBindingResult().getFieldError("urlPrefix"), is(notNullValue()));
    }

    @Test
    public void nonExistingChild() {
        // GIVEN: Prepare reqDto
        ResourceDefinition reqDto = resourceDefinitionFixtures.ontologyDefinition();
        ResourceDefinitionChild child = new ResourceDefinitionChild("nonExistingChild", "", null);
        reqDto.setChildren(List.of(child));

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionCache.getByUuid(child.getResourceDefinitionUuid()))
                .thenReturn(null);

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> resourceDefinitionValidator.validate(reqDto)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Child doesn't exist")));
    }

    @Test
    public void existingDependencyCycle() {
        // GIVEN: Prepare reqDto and resource definitions
        ResourceDefinition rdRepository = resourceDefinitionFixtures.fdpDefinition();
        ResourceDefinition reqDto = resourceDefinitionFixtures.catalogDefinition();
        ResourceDefinition rdDataset = resourceDefinitionFixtures.datasetDefinition();

        ResourceDefinitionChild rdDatasetChild = new ResourceDefinitionChild(rdRepository.getUuid(), "", null);
        rdDataset.setChildren(List.of(rdDatasetChild));

        // AND: Prepare database
        when(resourceDefinitionRepository.findByName(reqDto.getName()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionRepository.findByUrlPrefix(reqDto.getUrlPrefix()))
                .thenReturn(Optional.empty());
        when(resourceDefinitionCache.getByUuid(rdRepository.getUuid()))
                .thenReturn(rdRepository);
        when(resourceDefinitionCache.getByUuid(rdDataset.getUuid()))
                .thenReturn(rdDataset);

        // WHEN:
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> resourceDefinitionValidator.validate(reqDto)
        );

        // THEN:
        assertThat(exception.getMessage(), is(equalTo("Detect dependency cycle through child")));
    }

}
