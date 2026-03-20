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
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.Map;

import static java.lang.String.format;

public class OpenApiGenerator {

    public static final String FDP_TAG_PRIORITY = "FDP-TP";

    public static final int FDP_TAG_PRIORITY_VALUE = 10;

    public static final String TAG_PREFIX = "Metadata: ";

    private static final String TYPE_JSON = "application/json";

    private static final String TYPE_JSONLD = "application/ld+json";

    private static final String TYPE_RDFXML = "application/rdf+xml";

    private static final String TYPE_TURTLE = "text/turtle";

    private static final String TYPE_N3 = "text/n3";

    private static final String TYPE_PLAIN = "text/plain";

    private static final String PATH_PARAM = "path";

    private static final String PATH_ROOT = "/";

    private static final String FDP_RD_KEY = "fdpResourceDefinition";

    private static final Schema<String> SCHEMA_STRING =
            new Schema<String>().type("string");

    private static final Schema<String> SCHEMA_ERROR =
            new Schema<ErrorDTO>().$ref("#/components/schemas/ErrorDTO");

    private static final Schema<String> SCHEMA_META =
            new Schema<MetaDTO>().$ref("#/components/schemas/MetaDTO");

    private static final Schema<String> SCHEMA_META_STATE_CHANGE =
            new Schema<MetaStateChangeDTO>().$ref("#/components/schemas/MetaStateChangeDTO");

    private static final Schema<String> SCHEMA_MEMBER =
            new Schema<MemberDTO>().$ref("#/components/schemas/MemberDTO");

    private static final ArraySchema SCHEMA_MEMBERS = new ArraySchema().items(SCHEMA_MEMBER);

    private static final Schema<String> SCHEMA_MEMBER_CREATE =
            new Schema<MemberCreateDTO>().$ref("#/components/schemas/MemberCreateDTO");

    private static final Content CONTENT_RDF = new Content()
            .addMediaType(TYPE_TURTLE, new MediaType().schema(SCHEMA_STRING))
            .addMediaType(TYPE_JSONLD, new MediaType().schema(SCHEMA_STRING))
            .addMediaType(TYPE_RDFXML, new MediaType().schema(SCHEMA_STRING))
            .addMediaType(TYPE_N3, new MediaType().schema(SCHEMA_STRING));

    private static final Content CONTENT_ERROR = new Content()
            .addMediaType(TYPE_PLAIN, new MediaType().schema(SCHEMA_STRING))
            .addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_ERROR));

    private static final Content CONTENT_META_STATE_CHANGE = new Content()
            .addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_META_STATE_CHANGE));

    private static final ApiResponse RESPONSE_BAD_REQUEST = new ApiResponse()
            .description(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_FORBIDDEN = new ApiResponse()
            .description(HttpStatus.FORBIDDEN.getReasonPhrase())
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_UNAUTHORIZED = new ApiResponse()
            .description(HttpStatus.UNAUTHORIZED.getReasonPhrase())
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_NOT_FOUND = new ApiResponse()
            .description(HttpStatus.NOT_FOUND.getReasonPhrase())
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_NO_CONTENT = new ApiResponse()
            .description(HttpStatus.NO_CONTENT.getReasonPhrase());

    private static final ApiResponse RESPONSE_INTERNAL_SERVER_ERROR = new ApiResponse()
            .description(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .content(CONTENT_ERROR);

    private static final ApiResponse RESPONSE_OK_RDF = new ApiResponse()
            .description(HttpStatus.OK.getReasonPhrase())
            .content(CONTENT_RDF);

    private static final ApiResponses RESPONSES_RDF = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), RESPONSE_OK_RDF)
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_RDF_POST = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), RESPONSE_OK_RDF)
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_DELETE = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.NO_CONTENT.value()), RESPONSE_NO_CONTENT)
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_META = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse()
                    .description(HttpStatus.OK.getReasonPhrase())
                    .content(new Content()
                            .addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_META)))
            )
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_META_STATE = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse()
                    .description(HttpStatus.OK.getReasonPhrase())
                    .content(CONTENT_META_STATE_CHANGE)
            )
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_MEMBERS = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse()
                    .description(HttpStatus.OK.getReasonPhrase())
                    .content(new Content()
                            .addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_MEMBERS)))
            )
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final ApiResponses RESPONSES_MEMBER = new ApiResponses()
            .addApiResponse(String.valueOf(HttpStatus.OK.value()), new ApiResponse()
                    .description(HttpStatus.OK.getReasonPhrase())
                    .content(new Content()
                            .addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_MEMBER)))
            )
            .addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()), RESPONSE_BAD_REQUEST)
            .addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()), RESPONSE_UNAUTHORIZED)
            .addApiResponse(String.valueOf(HttpStatus.FORBIDDEN.value()), RESPONSE_FORBIDDEN)
            .addApiResponse(String.valueOf(HttpStatus.NOT_FOUND.value()), RESPONSE_NOT_FOUND)
            .addApiResponse(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), RESPONSE_INTERNAL_SERVER_ERROR);

    private static final RequestBody BODY_META_STATE = new RequestBody()
            .description("New state")
            .content(CONTENT_META_STATE_CHANGE)
            .required(true);

    private static final RequestBody BODY_MEMBERSHIP = new RequestBody()
            .description("New membership")
            .content(new Content().addMediaType(TYPE_JSON, new MediaType().schema(SCHEMA_MEMBER_CREATE)))
            .required(true);

    private static final Parameter PARAM_USER_UUID = new Parameter()
            .name("userUuid")
            .in(PATH_PARAM)
            .schema(SCHEMA_STRING);

    private static final Parameter PARAM_CHILD_PREFIX = new Parameter()
            .name("childPrefix")
            .in(PATH_PARAM)
            .schema(SCHEMA_STRING);

    private static final String IN_RDF = "%s in RDF";
    private static final String GET_OP_ID = "get%s";
    private static final String GET_OP_DESC = "Get %s";
    private static final String PUT_OP_ID = "put%s";
    private static final String PUT_OP_DESC = "Edit existing %s";
    private static final String DELETE_OP_ID = "delete%s";
    private static final String DELETE_OP_DESC = "Delete existing %s";
    private static final String GET_SPEC_OP_ID = "get%sSpec";
    private static final String GET_SPEC_OP_DESC = "Get SHACL shape specification for %s";
    private static final String GET_EXPANDED_OP_ID = "get%sExpanded";
    private static final String GET_EXPANDED_OP_DESC = "Get %s with its children";
    private static final String GET_CHILDPAGE_OP_ID = "get%sChildrenPage";
    private static final String GET_CHILDPAGE_OP_DESC = "Get a page of %s children";
    private static final String GET_META_OP_ID = "get%sMeta";
    private static final String GET_META_OP_DESC = "Get metadata (memberships and state) for %s";
    private static final String PUT_METASTATE_OP_ID = "put%sMetaState";
    private static final String PUT_METASTATE_OP_DESC = "Change state of %s";
    private static final String GET_MEMBERS_OP_ID = "get%sMembers";
    private static final String GET_MEMBERS_OP_DESC = "Get members of a specific %s";
    private static final String PUT_MEMBER_OP_ID = "put%sMember";
    private static final String PUT_MEMBER_OP_DESC = "Set membership for specific user in some %s";
    private static final String DELETE_MEMBER_OP_ID = "delete%sMember";
    private static final String DELETE_MEMBER_OP_DESC = "Delete membership of specific user in some %s";
    private static final String POST_OP_ID = "post%s";
    private static final String POST_OP_DESC = "Create a new %s";

    public static void generatePathsForRootResourceDefinition(Paths paths, ResourceDefinition resourceDefinition) {
        final String tag = TAG_PREFIX + resourceDefinition.getName();
        final String operationSuffix = resourceDefinition.getName();
        final Map<String, Object> extensions = Map.of(FDP_RD_KEY, resourceDefinition.getUuid());
        // CRUD: GET, PUT, DELETE
        paths.addPathItem(
                PATH_ROOT,
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_OP_ID, operationSuffix))
                                .description(format(GET_OP_DESC, resourceDefinition.getName()))
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
                        .put(new Operation()
                                .operationId(format(PUT_OP_ID, operationSuffix))
                                .description(format(PUT_OP_DESC, resourceDefinition.getName()))
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(format(IN_RDF, resourceDefinition.getName()))
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF)
                        )
                        .delete(new Operation()
                                .operationId(format(DELETE_OP_ID, operationSuffix))
                                .description(format(DELETE_OP_DESC, resourceDefinition.getName()))
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
                                .operationId(format(GET_SPEC_OP_ID, operationSuffix))
                                .description(format(GET_SPEC_OP_DESC, resourceDefinition.getName()))
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
                                .operationId(format(GET_EXPANDED_OP_ID, StringUtils.capitalize(operationSuffix)))
                                .description(format(GET_EXPANDED_OP_DESC, resourceDefinition.getName()))
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
                                .operationId(format(GET_CHILDPAGE_OP_ID, operationSuffix))
                                .description(format(GET_CHILDPAGE_OP_DESC, resourceDefinition.getName()))
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
                                .operationId(format(GET_META_OP_ID, operationSuffix))
                                .description(format(GET_META_OP_DESC, resourceDefinition.getName()))
                                .addTagsItem(tag)
                                .responses(RESPONSES_META)
                        )
        );
        paths.addPathItem(
                "/meta/state",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId(format(PUT_METASTATE_OP_ID, operationSuffix))
                                .description(format(PUT_METASTATE_OP_DESC, resourceDefinition.getName()))
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
                                .operationId(format(GET_MEMBERS_OP_ID, operationSuffix))
                                .description(format(GET_MEMBERS_OP_ID, resourceDefinition.getName()))
                                .addTagsItem(tag)
                                .responses(RESPONSES_MEMBERS)
                        )
        );
        paths.addPathItem(
                "/members/{userUuid}",
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId(format(PUT_MEMBER_OP_ID, operationSuffix))
                                .description(format(PUT_MEMBER_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .requestBody(BODY_MEMBERSHIP)
                                .responses(RESPONSES_MEMBER)
                        )
                        .delete(new Operation()
                                .operationId(format(DELETE_MEMBER_OP_ID, operationSuffix))
                                .description(format(DELETE_MEMBER_OP_DESC,
                                        resourceDefinition.getName()))
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
    }

    public static void generatePathsForResourceDefinition(Paths paths, ResourceDefinition resourceDefinition) {
        final String prefix = resourceDefinition.getUrlPrefix();
        final String operationSuffix = StringUtils.capitalize(prefix);
        if (prefix.isEmpty()) {
            generatePathsForRootResourceDefinition(paths, resourceDefinition);
            return;
        }
        final String parameterName = "uuid";
        final String nestedPrefix = PATH_ROOT + prefix;
        final Parameter parameter = new Parameter()
                .name(parameterName)
                .in(PATH_PARAM)
                .schema(SCHEMA_STRING);
        final String tag = TAG_PREFIX + resourceDefinition.getName();
        final Map<String, Object> extensions = Map.of(FDP_RD_KEY, resourceDefinition.getUuid());
        // CRUD: POST
        paths.addPathItem(
                PATH_ROOT + prefix,
                new PathItem()
                        .extensions(extensions)
                        .post(new Operation()
                                .operationId(format(POST_OP_ID, operationSuffix))
                                .description(format(POST_OP_DESC, resourceDefinition.getName()))
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(format(IN_RDF, resourceDefinition.getName()))
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF_POST)
                        )
        );
        // CRUD: GET, PUT, DELETE
        paths.addPathItem(
                format("%s/{%s}", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_OP_ID, operationSuffix))
                                .description(format(GET_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
                        .put(new Operation()
                                .operationId(format(PUT_OP_ID, operationSuffix))
                                .description(format(PUT_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .requestBody(new RequestBody()
                                        .description(format(IN_RDF, resourceDefinition.getName()))
                                        .content(CONTENT_RDF)
                                        .required(true)
                                )
                                .responses(RESPONSES_RDF)
                        )
                        .delete(new Operation()
                                .operationId(format(DELETE_OP_ID, operationSuffix))
                                .description(format(DELETE_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_DELETE)
                        )
        );
        // Spec (SHACL Shape)
        paths.addPathItem(
                format("%s/{%s}/spec", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_SPEC_OP_ID, operationSuffix))
                                .description(format(GET_SPEC_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Expanded
        paths.addPathItem(
                format("%s/{%s}/expanded", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_EXPANDED_OP_ID, operationSuffix))
                                .description(format(GET_EXPANDED_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Page
        paths.addPathItem(
                format("%s/{%s}/page/{childPrefix}", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_CHILDPAGE_OP_ID, operationSuffix))
                                .description(format(GET_CHILDPAGE_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addParametersItem(PARAM_CHILD_PREFIX)
                                .addTagsItem(tag)
                                .responses(RESPONSES_RDF)
                        )
        );
        // Meta
        paths.addPathItem(
                format("%s/{%s}/meta", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_META_OP_ID, operationSuffix))
                                .description(format(GET_META_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_META)
                        )
        );
        paths.addPathItem(
                format("%s/{%s}/meta/state", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId(format(PUT_METASTATE_OP_ID, operationSuffix))
                                .description(format(PUT_METASTATE_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .requestBody(BODY_META_STATE)
                                .responses(RESPONSES_META_STATE)
                        )
        );
        // Membership
        paths.addPathItem(
                format("%s/{%s}/members", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .get(new Operation()
                                .operationId(format(GET_MEMBERS_OP_ID, operationSuffix))
                                .description(format(GET_MEMBERS_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addTagsItem(tag)
                                .responses(RESPONSES_MEMBERS)
                        )
        );
        paths.addPathItem(
                format("%s/{%s}/members/{userUuid}", nestedPrefix, parameterName),
                new PathItem()
                        .extensions(extensions)
                        .put(new Operation()
                                .operationId(format(PUT_MEMBER_OP_ID, operationSuffix))
                                .description(format(PUT_MEMBER_OP_DESC, resourceDefinition.getName()))
                                .addParametersItem(parameter)
                                .addParametersItem(PARAM_USER_UUID)
                                .addTagsItem(tag)
                                .requestBody(BODY_MEMBERSHIP)
                                .responses(RESPONSES_MEMBER)
                        )
                        .delete(new Operation()
                                .operationId(format(DELETE_MEMBER_OP_ID, operationSuffix))
                                .description(format(DELETE_MEMBER_OP_DESC, resourceDefinition.getName()))
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
                .description(format("Metadata according to the %s resource definition", resourceDefinition.getName()))
                .extensions(Map.of(FDP_TAG_PRIORITY, FDP_TAG_PRIORITY_VALUE));
    }
}
