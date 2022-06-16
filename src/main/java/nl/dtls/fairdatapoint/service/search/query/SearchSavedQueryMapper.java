package nl.dtls.fairdatapoint.service.search.query;

import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryChangeDTO;
import nl.dtls.fairdatapoint.api.dto.search.SearchSavedQueryDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.entity.search.SearchSavedQuery;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class SearchSavedQueryMapper {
    public SearchSavedQueryDTO toDTO(SearchSavedQuery query, UserDTO userDTO) {
        return SearchSavedQueryDTO.builder()
                .uuid(query.getUuid())
                .name(query.getName())
                .description(query.getDescription())
                .variables(query.getVariables())
                .user(userDTO)
                .type(query.getType())
                .createdAt(query.getCreatedAt())
                .updatedAt(query.getUpdatedAt())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(SearchSavedQueryChangeDTO reqDto, UserDTO userDto) {
        return SearchSavedQuery.builder()
                .uuid(UUID.randomUUID().toString())
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .type(reqDto.getType())
                .variables(reqDto.getVariables())
                .userUuid(userDto == null ? null : userDto.getUuid())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public SearchSavedQuery fromChangeDTO(SearchSavedQuery query, SearchSavedQueryChangeDTO reqDto) {
        return query.toBuilder()
                .name(reqDto.getName())
                .description(reqDto.getDescription())
                .variables(reqDto.getVariables())
                .userUuid(query.getUserUuid())
                .type(reqDto.getType())
                .updatedAt(Instant.now())
                .build();
    }
}
