--
-- The MIT License
-- Copyright © 2016-2024 FAIR Data Team
--
-- Permission is hereby granted, free of charge, to any person obtaining a copy
-- of this software and associated documentation files (the "Software"), to deal
-- in the Software without restriction, including without limitation the rights
-- to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
-- copies of the Software, and to permit persons to whom the Software is
-- furnished to do so, subject to the following conditions:
--
-- The above copyright notice and this permission notice shall be included in
-- all copies or substantial portions of the Software.
--
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
-- IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
-- FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
-- AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
-- LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
-- OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
-- THE SOFTWARE.
--

INSERT INTO membership (uuid, name, allowed_entities, created_at, updated_at)
VALUES ('49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8', 'Owner', ARRAY ['a0949e72-4466-4d53-8900-9436d1049a4b', '2f08228e-1789-40f8-84cd-28e3288c3604', '02c649de-c579-43bb-b470-306abdc808c7'], NOW(), NOW());

INSERT INTO membership (uuid, name, allowed_entities, created_at, updated_at)
VALUES ('87a2d984-7db2-43f6-805c-6b0040afead5', 'Data Provider', ARRAY ['a0949e72-4466-4d53-8900-9436d1049a4b'], NOW(), NOW());

INSERT INTO membership_permission (uuid, membership_id, mask, code, created_at, updated_at)
VALUES ('e0d9f853-637b-4c50-9ad9-07b6349bf76f', '49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8', 2, 'W', NOW(), NOW());

INSERT INTO membership_permission (uuid, membership_id, mask, code, created_at, updated_at)
VALUES ('de4e4f85-f11d-475b-b6f0-33bdfe5f923a', '49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8', 4, 'C', NOW(), NOW());

INSERT INTO membership_permission (uuid, membership_id, mask, code, created_at, updated_at)
VALUES ('60bebbf0-210d-4b05-af85-ca1b58546261', '49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8', 8, 'D', NOW(), NOW());

INSERT INTO membership_permission (uuid, membership_id, mask, code, created_at, updated_at)
VALUES ('36c3b6e9-f2e3-48b7-bae1-4dc3196a3657', '49f2bcfd-ef0a-4a3a-a1a3-0fc72a6892a8', 16, 'A', NOW(), NOW());

INSERT INTO membership_permission (uuid, membership_id, mask, code, created_at, updated_at)
VALUES ('589d09d3-1c29-4c6f-97fc-6ea4e007fb85', '87a2d984-7db2-43f6-805c-6b0040afead5', 4, 'C', NOW(), NOW());
