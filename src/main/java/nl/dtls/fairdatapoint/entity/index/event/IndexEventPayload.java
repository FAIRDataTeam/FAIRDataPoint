package nl.dtls.fairdatapoint.entity.index.event;

import lombok.Builder;
import lombok.Data;
import nl.dtls.fairdatapoint.entity.index.event.payload.*;

@Data
@Builder
public class IndexEventPayload {

    private IncomingPing incomingPing;

    private MetadataRetrieval metadataRetrieval;

    private AdminTrigger adminTrigger;

    private WebhookPing webhookPing;

    private WebhookTrigger webhookTrigger;

    public IndexEventType extractType() {
        if (incomingPing != null) {
            return IndexEventType.INCOMING_PING;
        } else if (metadataRetrieval != null) {
            return IndexEventType.METADATA_RETRIEVAL;
        } else if (adminTrigger != null) {
            return IndexEventType.ADMIN_TRIGGER;
        } else if (webhookPing != null) {
            return IndexEventType.WEBHOOK_PING;
        } else if (webhookTrigger != null) {
            return IndexEventType.WEBHOOK_TRIGGER;
        }
        return null;
    }

    public boolean hasType(IndexEventType type) {
        return extractType() == type;
    }
}
