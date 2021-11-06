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
package nl.dtls.fairdatapoint.database.mongo.migration.development.shape;

import nl.dtls.fairdatapoint.database.common.migration.Migration;
import nl.dtls.fairdatapoint.database.mongo.migration.development.shape.data.ShapeFixtures;
import nl.dtls.fairdatapoint.database.mongo.repository.ShapeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShapeMigration implements Migration {

    @Autowired
    private ShapeFixtures shapeFixtures;

    @Autowired
    private ShapeRepository shapeRepository;

    public void runMigration() {
        shapeRepository.deleteAll();
        shapeRepository.save(shapeFixtures.resourceShape());
        shapeRepository.save(shapeFixtures.fdpShape());
        shapeRepository.save(shapeFixtures.dataServiceShape());
        shapeRepository.save(shapeFixtures.metadataServiceShape());
        shapeRepository.save(shapeFixtures.catalogShape());
        shapeRepository.save(shapeFixtures.datasetShape());
        shapeRepository.save(shapeFixtures.distributionShape());
    }

}