package nl.dtls.fairdatapoint.service.openapi;

import io.swagger.v3.oas.models.tags.Tag;
import nl.dtls.fairdatapoint.entity.resource.ResourceDefinition;

import java.util.*;
import java.util.stream.Collectors;

public class OpenApiTagsUtils {

    private static final Comparator<String> STRING_COMPARATOR = Comparator.comparing(String::toString);

    private static final Comparator<Tag> TAG_COMPARATOR = (o1, o2) -> {
        int priority1 = (int)o1.getExtensions().getOrDefault(OpenApiGenerator.FDP_TAG_PRIORITY, 90);
        int priority2 = (int)o2.getExtensions().getOrDefault(OpenApiGenerator.FDP_TAG_PRIORITY, 90);
        if (priority1 < priority2) {
            return -1;
        } else if (priority1 > priority2) {
            return 1;
        }
        return STRING_COMPARATOR.compare(o1.getName(), o2.getName());
    };

    public static final Tag METADATA_TAG = new Tag()
            .name("Metadata")
            .description("Common operations with all metadata")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 0));

    public static final Tag METADATA_MMODEL_TAG = new Tag()
            .name("Metadata Model")
            .description("Manipulation with model of metadata")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 20));

    public static final Tag METADATA_CLIENT_TAG = new Tag()
            .name("Client")
            .description("Endpoints for FAIR Data Point Client")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 30));

    public static final Tag METADATA_INDEX_TAG = new Tag()
            .name("Index")
            .description("FAIR Data Point Index endpoints")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 40));

    public static final Tag METADATA_AA_TAG = new Tag()
            .name("Authentication and Authorization")
            .description("Management of access to FDP (not specific type of metadata)")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 50));

    public static final Tag METADATA_USERMGMT_TAG = new Tag()
            .name("User Management")
            .description("Management of user accounts")
            .extensions(Map.of(OpenApiGenerator.FDP_TAG_PRIORITY, 60));

    public static final List<Tag> STATIC_TAGS = Arrays.asList(METADATA_TAG, METADATA_MMODEL_TAG, METADATA_CLIENT_TAG, METADATA_INDEX_TAG, METADATA_AA_TAG, METADATA_USERMGMT_TAG);

    public static List<Tag> listTags(List<ResourceDefinition> resourceDefinitions) {
        List<Tag> tags = resourceDefinitions.stream().map(OpenApiGenerator::generateTag).collect(Collectors.toList());
        tags.addAll(STATIC_TAGS);
        tags.sort(TAG_COMPARATOR);
        return tags;
    }
}
