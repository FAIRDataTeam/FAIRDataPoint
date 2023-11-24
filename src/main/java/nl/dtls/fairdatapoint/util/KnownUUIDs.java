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
package nl.dtls.fairdatapoint.util;

import java.util.UUID;

public class KnownUUIDs {

    public static final UUID NULL_UUID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static final UUID USER_ALBERT_UUID =
            UUID.fromString("7e64818d-6276-46fb-8bb1-732e6e09f7e9");

    public static final UUID USER_NIKOLA_UUID =
            UUID.fromString("b5b92c69-5ed9-4054-954d-0121c29b6800");

    public static final UUID USER_ISAAC_UUID =
            UUID.fromString("8d1a4c06-bb0e-4d03-a01f-14fa49bbc152");

    public static final UUID USER_ADMIN_UUID =
            UUID.fromString("95589e50-d261-492b-8852-9324e9a66a42");

    public static final UUID API_KEY_ALBERT_UUID =
            UUID.fromString("a1c00673-24c5-4e0a-bdbe-22e961ee7548");

    public static final UUID API_KEY_NIKOLA_UUID =
            UUID.fromString("62657760-21fe-488c-a0ea-f612a70493da");

    public static final UUID MEMBERSHIP_OWNER_UUID =
            UUID.fromString("49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8");

    public static final UUID MEMBERSHIP_DATAPROVIDER_UUID =
            UUID.fromString("87a2d984-7db2-43f6-805c-6b0040afead5");

    public static final UUID SCHEMA_RESOURCE_UUID =
            UUID.fromString("6a668323-3936-4b53-8380-a4fd2ed082ee");

    public static final UUID SCHEMA_REPOSITORY_UUID =
            UUID.fromString("a92958ab-a414-47e6-8e17-68ba96ba3a2b");

    public static final UUID SCHEMA_FDP_UUID =
            SCHEMA_REPOSITORY_UUID;

    public static final UUID SCHEMA_DATASERVICE_UUID =
            UUID.fromString("89d94c1b-f6ff-4545-ba9b-120b2d1921d0");

    public static final UUID SCHEMA_METADATASERVICE_UUID =
            UUID.fromString("6f7a5a76-6185-4bd0-9fe9-62ecc90c9bad");

    public static final UUID SCHEMA_CATALOG_UUID =
            UUID.fromString("2aa7ba63-d27a-4c0e-bfa6-3a4e250f4660");

    public static final UUID SCHEMA_DATASET_UUID =
            UUID.fromString("866d7fb8-5982-4215-9c7c-18d0ed1bd5f3");

    public static final UUID SCHEMA_DISTRIBUTION_UUID =
            UUID.fromString("ebacbf83-cd4f-4113-8738-d73c0735b0ab");

    public static final UUID SCHEMA_V1_RESOURCE_UUID =
            UUID.fromString("71d77460-f919-4f72-b265-ed26567fe361");

    public static final UUID SCHEMA_V2_RESOURCE_UUID =
            UUID.fromString("4c65bdf7-bb56-4bca-ae22-74977b148b16");

    public static final UUID SCHEMA_V1_FDP_UUID =
            UUID.fromString("4e64208d-f102-45a0-96e3-17b002e6213e");

    public static final UUID SCHEMA_V1_DATASERVICE_UUID =
            UUID.fromString("9111d436-fe58-4bd5-97ae-e6f86bc2997a");

    public static final UUID SCHEMA_V1_METADATASERVICE_UUID =
            UUID.fromString("36b22b70-6203-4dd2-9fb6-b39a776bf467");

    public static final UUID SCHEMA_V1_CATALOG_UUID =
            UUID.fromString("c9640671-945d-4114-88fb-e81314cb7ab2");

    public static final UUID SCHEMA_V1_DATASET_UUID =
            UUID.fromString("9cc3c89a-76cf-4639-a71f-652627af51db");

    public static final UUID SCHEMA_V1_DISTRIBUTION_UUID =
            UUID.fromString("3cda8cd3-b08b-4797-822d-d3f3e83c466a");

    public static final UUID RD_REPOSITORY_UUID =
            UUID.fromString("77aaad6a-0136-4c6e-88b9-07ffccd0ee4c");

    public static final UUID RD_FDP_UUID =
            RD_REPOSITORY_UUID;

    public static final UUID RD_CATALOG_UUID =
            UUID.fromString("a0949e72-4466-4d53-8900-9436d1049a4b");

    public static final UUID RD_DATASET_UUID =
            UUID.fromString("2f08228e-1789-40f8-84cd-28e3288c3604");

    public static final UUID RD_DISTRIBUTION_UUID =
            UUID.fromString("02c649de-c579-43bb-b470-306abdc808c7");

    public static final UUID SETTINGS_UUID = NULL_UUID;

    public static final UUID SAVED_QUERY_PUBLIC_UUID =
            UUID.fromString("d31e3da1-2cfa-4b55-a8cb-71d1acf01aef");

    public static final UUID SAVED_QUERY_PRIVATE_UUID =
            UUID.fromString("97da9119-834e-4687-8321-3df157547178");

    public static final UUID SAVED_QUERY_INTERNAL_UUID =
            UUID.fromString("c7d0b6a0-5b0a-4b0e-9b0a-9b0a9b0a9b0a");

    public static final UUID RD_CHILD_FDP_CATALOG_UUID =
            UUID.fromString("b8648597-8fbd-4b89-9e30-5eab82675e42");

    public static final UUID RD_DATASERVICE_UUID =
            UUID.fromString("4bc19f45-845d-48d6-ade7-ac2664563f60");

    public static final UUID RD_CHILD_CATALOG_DATASET_UUID =
            UUID.fromString("e9f0f5d3-2a93-4aa3-9dd0-acb1d76f54fc");

    public static final UUID RD_CHILD_DATASET_DISTRIBUTION_UUID =
            UUID.fromString("9f138a13-9d45-4371-b763-0a3b9e0ec912");

    public static final UUID RD_CHILD_DATASET_DISTRIBUTION_MEDIA_TYPE_UUID =
            UUID.fromString("723e95d3-1696-45e2-9429-f6e98e3fb893");

    public static final UUID RD_LINK_DISTRIBUTION_ACCESS_UUID =
            UUID.fromString("660a1821-a5d2-48d0-a26b-0c6d5bac3de4");

    public static final UUID RD_LINK_DISTRIBUTION_DOWNLOAD_UUID =
            UUID.fromString("c2eaebb8-4d8d-469d-8736-269adeded996");

    public static final UUID SCHEMA_V1_DATASERVICE_EXTENSION_UUID =
            UUID.fromString("5f0dbdf7-ff73-4574-a918-cdcda12ecfd4");

    public static final UUID SCHEMA_V1_METADATASERVICE_EXTENSION_UUID =
            UUID.fromString("a843138f-b6f8-43a9-8020-550d2d4b644e");

    public static final UUID SCHEMA_V1_FDP_EXTENSION_UUID =
            UUID.fromString("42a76a7a-ba04-43ae-a3d5-1bba62971261");

    public static final UUID RD_FDP_USAGE_UUID =
            UUID.fromString("d8a67021-0f4c-4b0a-ab3d-7bc8621fbee7");
}
