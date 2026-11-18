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
package org.fairdatateam.fairdatapoint.rdf.metadata;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.impl.DynamicModelFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.fairdatateam.fairdatapoint.actuator.AppInfoContributor;
import org.fairdatateam.fairdatapoint.common.config.InstanceProperties;
import org.fairdatateam.fairdatapoint.profile.ProfileService;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinition;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionCache;
import org.fairdatateam.fairdatapoint.resource.ResourceDefinitionService;
import org.junit.jupiter.api.Test;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.fairdatateam.fairdatapoint.common.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests ability to disable default language and accessRightsDescription (#1065)
 */
@SpringJUnitConfig(classes = { MetadataConfig.class, MetadataEnhancer.class })
@TestPropertySource(properties = {
        "metadataProperties.accessRightsDescription=",
        "metadataProperties.language=",
        "metadataProperties.license=http://license.example.org"
})
@EnableConfigurationProperties(value = {InstanceProperties.class, MetadataProperties.class})
public class MetadataEnhancerTest {

    @Autowired
    private MetadataEnhancer metadataEnhancer;

    @MockitoBean
    AppInfoContributor appInfoContributor;

    @MockitoBean
    MetricsMetadataService metricsMetadataService;

    @MockitoBean
    ProfileService profileService;

    @MockitoBean
    ResourceDefinitionCache resourceDefinitionCache;

    @MockitoBean
    ResourceDefinitionService resourceDefinitionService;

    @MockitoBean
    SpringDocConfigProperties springDocConfigProperties;

    @MockitoBean
    SwaggerUiConfigProperties swaggerUiConfigProperties;

    @Test
    public void noLanguageStatementAddedIfConfigLanguageIsEmpty() {
        // given an empty model and mock inputs
        final Model metadata = new DynamicModelFactory().createEmptyModel();
        final String uri = "https://fdp.example.org";
        final ResourceDefinition resourceDefinition = new ResourceDefinition();
        resourceDefinition.setUrlPrefix("");

        // when we enhance the model
        metadataEnhancer.enhance(metadata, i(uri), resourceDefinition);

        // then the expected metadata statements should be present
        assertFalse(metadata.contains(i(uri + "#accessRights"), DCTERMS.DESCRIPTION, null));
        assertFalse(metadata.contains(null, DCTERMS.LANGUAGE, null));
        assertTrue(metadata.contains(null, DCTERMS.LICENSE, null));
    }
}
