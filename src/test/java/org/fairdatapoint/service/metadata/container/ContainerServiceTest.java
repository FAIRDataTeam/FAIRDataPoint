package org.fairdatapoint.service.metadata.container;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContainerServiceTest {

    final private Comparator<String> newComparator = Comparator.comparing(
            String::valueOf, String.CASE_INSENSITIVE_ORDER
    );

    private int oldCompare(String title1, String title2) {
        if (title1 == null) {
            return -1;
        }
        if (title2 == null) {
            return 1;
        }
        return title1.compareToIgnoreCase(title2);
    }

    private int newCompare(String title1, String title2) {
        return newComparator.compare(title1, title2);
    }

    /**
     * The original GenericController.getMetaDataChildren used a custom comparator for sorting,
     * reproduced as in the oldCompare method above.
     * Also see api/controller/metadata/GenericController.java L346 (de41d47).
     * This is now replaced by Comparator.comparing(), like newComparator above,
     * so we need to verify that the resulting outcome is identical.
     */
    @ParameterizedTest
    @CsvSource({",", "foo,", ",bar", "foo,bar", "foo,foo", "foo,FOO", "BAR,bar"})
    public void testContainerServiceComparatorEquality(String string1, String string2) {
        assertEquals(oldCompare(string1, string2), newCompare(string1, string2));
    }
}
