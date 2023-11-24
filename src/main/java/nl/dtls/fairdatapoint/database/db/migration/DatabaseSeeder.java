package nl.dtls.fairdatapoint.database.db.migration;

import lombok.RequiredArgsConstructor;
import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;

@RequiredArgsConstructor
public class DatabaseSeeder implements Callback {

    private final String activeProfile;

    @Override
    public boolean supports(Event event, Context context) {
        return event.name().equals(Event.AFTER_MIGRATE.name());
    }

    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return true;
    }

    @Override
    public void handle(Event event, Context context) {
        try {
            System.out.println("seed database here...");
            System.out.println(activeProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getCallbackName() {
        return DatabaseSeeder.class.getName();
    }
}
