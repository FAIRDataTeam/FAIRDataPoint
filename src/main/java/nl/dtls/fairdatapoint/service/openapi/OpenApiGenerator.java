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
package nl.dtls.fairdatapoint.service.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberCreateDTO;
import nl.dtls.fairdatapoint.api.dto.member.MemberDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaDTO;
import nl.dtls.fairdatapoint.api.dto.metadata.MetaStateChangeDTO;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

public class OpenApiGenerator {

    public static final String FDP_TAG_PRIORITY = "FDP-TP";

    public static final String TAG_PREFIX = "Metadata: ";

    private static final Schema<String> SCHEMA_STRING = new Schema<String>().type("string");

    private static final Schema<String> SCHEMA_ERROR = new Schema<ErrorDTO>().$ref("#/components/schemas/ErrorDTO");

    private static final Schema<String> SCHEMA_META = new Schema<MetaDTO>().$ref("#/components/schemas/MetaDTO");

    private static final Schema<String> SCHEMA_META_STATE_CHANGE = new Schema<MetaStateChangeDTO>().$ref("#/components/schemas/MetaStateChangeDTO");

    private static final Schema<String> SCHEMA_MEMBER = new Schema<MemberDTO>().$ref("#/components/schemas/MemberDTO");

    private static final ArraySchema SCHEMA_MEMBERS = new ArraySchema().items(SCHEMA_MEMBER);

    private static final Schema<String> SCHEMA_MEMBER_CREATE = new Schema<MemberCreateDTO>().$ref("#/components/schemas/MemberDTO");

    private static final Content CONTENT_RDF = new Content()
            .addMediaType("text/turtle", new MediaType().schema(SCHEMA_STRING))
            .addMediaType("application/ld+json", new MediaType().schema(SCHEMA_STRING))
            .addMediaType("application/rdf+xml", new MediaType().schema(SCHEMA_STRING))
            .addMediaType("text/n3", new MediaType().schema(SCHEMA_STRING));

    private static final Content CONTENT_ERROR = new Content()
            .addMediaType("text/plain", new MediaType().schema(SCHEMA_STRING))
            .addMediaType("application/json", new MediaType().schema(SCHEMA_ERROR));

    private static final Content CONTENT_META_STATE_CHANGE = new Content()
            .addMediaType("application/json", new MediaType().schema(SCHEMA_META_STATE_CHANGE));

    private static final ApiResponse RESPONSE_BAD_REQUEST = new ApiResponse()
            .description("Bad Request")
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_FORBIDDEN = new ApiResponse()
            .description("Forbidden")
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_UNAUTHORIZED = new ApiResponse()
            .description("Unauthorized")
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_NOT_FOUND = new ApiResponse()
            .description("Not Found")
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_NO_CONTENT = new ApiResponse()
            .description("No Content");

    private static final ApiResponse RESPONSE_INTERNAL_SERVER_ERROR = new ApiResponse()
            .description("Internal Server Error")
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_OK_RDF = new ApiResponse()
            .description("OK")
            .content(CONTENT_RDF);

    private static final ApiResponses RESPONSES_RDF = new ApiResponses()
            .addApiResponse("200", RESPONSE_OK_RDF)
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_DELETE = new ApiResponses()
            .addApiResponse("204", RESPONSE_NO_CONTENT)
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_META = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                    .description("OK")
                    .content(new Content()
                            .addMediaType("application/json", new MediaType().schema(SCHEMA_META)))
            )
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_META_STATE = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                    .description("OK")
                    .content(CONTENT_META_STATE_CHANGE)
            )
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_MEMBERS = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                    .description("OK")
                    .content(new Content()
                            .addMediaType("application/json", new MediaType().schema(SCHEMA_MEMBERS)))
            )
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_MEMBER = new ApiResponses()
            .addApiResponse("200", new ApiResponse()
                    .description("OK")
                    .content(new Content()
                            .addMediaType("application/json", new MediaType().schema(SCHEMA_MEMBER)))
            )
            .addApiResponse("400", RESPONSE_BAD_REQUEST)
            .addApiResponse("401", RESPONSE_UNAUTHORIZED)
            .addApiResponse("403", RESPONSE_FORBIDDEN)
            .addApiResponse("404", RESPONSE_NOT_FOUND)
            .addApiResponse("500", RESPONSE_INTERNAL_SERVER_ERROR);

    private static final RequestBody BODY_META_STATE = new RequestBody()
            .description("New state")
            .content(CONTENT_META_STATE_CHANGE)
            .required(true);

    private static final RequestBody BODY_MEMBERSHIP = new RequestBody()
            .description("New membership")
            .content(new Content().addMediaType("application/json", new MediaType().schema(SCHEMA_MEMBER_CREATE)))
            .required(true);

    private static final Parameter PARAM_USER_UUID = new Parameter()
            .name("userUuid")
            .in("path")
            .schema(SCHEMA_STRING);

    private static final Parameter PARAM_CHILD_PREFIX = new Parameter()
            .name("childPrefix")
            .in("path")
            .schema(SCHEMA_STRING);

    public static void generatePathsForRootResourceDefinition(Paths paths, ResourceDefinition resourceDefinition) {
        String tag = TAG_PREFIX + resourceDefinition.getName();
        String operationSuffix = resourceDefinition.getName();
        Map<String, Object> extensions = Map.of("fdpResourceDefinition", resourceDefinition.getUuid());
        // CRUD: GET, PUT, DELETE
        paths.addPathItem(
                "/",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix)
                                .description("Get " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
                        .put(new Operation()
                                .operationId("put" + operationSuffix)
                                .description("Edit existing " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(resourceDefinition.getName() + " in RDF")
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF)
                        )
                        .delete(new Operation()
                                .operationId("delete" + operationSuffix)
                                .description("Delete existing " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
        // Spec (SHACL Shape)
        paths.addPathItem(
                "/spec",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Spec")
                                .description("Get SHACL shape specification for " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Expanded
        paths.addPathItem(
                "/expanded",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + StringUtils.capitalize(operationSuffix) + "Expanded")
                                .description("Get " + resourceDefinition.getName() + " with its children")
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Page
        paths.addPathItem(
                "/page/{childPrefix}",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "ChildrenPage")
                                .description("Get a page of " + resourceDefinition.getName() + " children")
                                .addParametersItem(PARAM_CHILD_PREFIX)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Meta
        paths.addPathItem(
                "/meta",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Meta")
                                .description("Get metadata (memberships and state) for " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .responses(RESPONSES_META)
                        )
        );
        paths.addPathItem(
                "/meta/state",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId("put" + operationSuffix + "MetaState")
                                .description("Change state of " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .requestBody(BODY_META_STATE)
                                .responses(RESPONSES_META_STATE)
                        )
        );
        // Membership
        paths.addPathItem(
                "/members",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Members")
                                .description("Get members of a specific " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .responses(RESPONSES_MEMBERS)
                        )
        );
        paths.addPathItem(
                "/members/{userUuid}",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId("put" + operationSuffix + "Member")
                                .description("Set membership for specific user in some " + resourceDefinition.getName())
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .requestBody(BODY_MEMBERSHIP)
                                .responses(RESPONSES_MEMBER)
                        )
                        .delete(new Operation()
                                .operationId("delete" + operationSuffix + "Member")
                                .description("Set membership for specific user in some " + resourceDefinition.getName())
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
    }

    public static void generatePathsForResourceDefinition(Paths paths, ResourceDefinition resourceDefinition) {
        String prefix = resourceDefinition.getUrlPrefix();
        String operationSuffix = StringUtils.capitalize(prefix);
        if (prefix.isEmpty()) {
            generatePathsForRootResourceDefinition(paths, resourceDefinition);
            return;
        }
        String parameterName = "uuid";
        String nestedPrefix = "/" + prefix;
        Parameter parameter = new Parameter()
                .name(parameterName)
                .in("path")
                .schema(new Schema<String>().type("string"));
        String tag = TAG_PREFIX + resourceDefinition.getName();
        Map<String, Object> extensions = Map.of("fdpResourceDefinition", resourceDefinition.getUuid());
        // CRUD: POST
        paths.addPathItem(
                "/" + prefix,
                new PathItem()
                        .extensions(extensions)
                        .post(new Operation()
                                .operationId("post" + operationSuffix)
                                .description("Create a new " + resourceDefinition.getName())
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(resourceDefinition.getName() + " in RDF")
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF)
                        )
        );
        // CRUD: GET, PUT, DELETE
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix)
                                .description("Get " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
                        .put(new Operation()
                                .operationId("put" + operationSuffix)
                                .description("Edit existing " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(resourceDefinition.getName() + " in RDF")
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF)
                        )
                        .delete(new Operation()
                                .operationId("delete" + operationSuffix)
                                .description("Delete existing " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
        // Spec (SHACL Shape)
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/spec",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Spec")
                                .description("Get SHACL shape specification for " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Expanded
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/expanded",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Expanded")
                                .description("Get " + resourceDefinition.getName() + " with its children")
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Page
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/page/{childPrefix}",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "ChildrenPage")
                                .description("Get a page of " + resourceDefinition.getName() + " children")
                                .addParametersItem(parameter)
                                .addParametersItem(PARAM_CHILD_PREFIX)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Meta
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/meta",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Meta")
                                .description("Get metadata (memberships and state) for " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_META)
                        )
        );
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/meta/state",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId("put" + operationSuffix + "MetaState")
                                .description("Change state of " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .requestBody(BODY_META_STATE)
                                .responses(RESPONSES_META_STATE)
                        )
        );
        // Membership
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/members",
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId("get" + operationSuffix + "Members")
                                .description("Get members of a specific " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_MEMBERS)
                        )
        );
        paths.addPathItem(
                nestedPrefix + "/{" + parameterName + "}/members/{userUuid}",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId("put" + operationSuffix + "Member")
                                .description("Set membership for specific user in some " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .requestBody(BODY_MEMBERSHIP)
                                .responses(RESPONSES_MEMBER)
                        )
                        .delete(new Operation()
                                .operationId("delete" + operationSuffix + "Member")
                                .description("Set membership for specific user in some " + resourceDefinition.getName())
                                .addParametersItem(parameter)
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
    }

    public static Tag generateTag(ResourceDefinition resourceDefinition) {
        return new Tag()
                .name(TAG_PREFIX + resourceDefinition.getName())
                .description("Metadata according to the " + resourceDefinition.getName() + " resource definition")
                .extensions(Map.of(FDP_TAG_PRIORITY, 10));
    }
}
