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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.pid;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;
import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultPIDSystemImpl class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @author Kees Burger <kees.burger@dtls.nl>
 * @version 0.1
 * @since 2018-06-05
 */
public class DefaultPIDSystemImplTest {

    private final ValueFactory valueFactory = SimpleValueFactory.getInstance();
    private final DefaultPIDSystemImpl pidSystem = new DefaultPIDSystemImpl();

    /**
     * Test of null metadata, this test is excepted to throw error
     */
    @Test
    public void testGetURIForNullMetadata() {
        assertThrows(NullPointerException.class, () -> {
            pidSystem.getURI(null);
        });
    }

    /**
     * Test of valid metadata uri, this test is excepted to pass
     */
    @Test
    public void testGetURIForValidMetadata() {
        IRI iri = pidSystem.getURI(i("http://example.org/my-metadata"));
        assertTrue(iri.toString().contains("#"));
    }

    /**
     * Test of null pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForNullPIDIri() {
        assertThrows(NullPointerException.class, () -> {
            pidSystem.getId(null);
        });
    }

    /**
     * Test of invalid default pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForInvalidPIDIri() {
        assertThrows(IllegalStateException.class, () -> {
            pidSystem.getId(valueFactory.createIRI("http://example.com/fdp/someid"));
        });
    }

    /**
     * Test of valid default pid iri, this test is excepted to throw error
     */
    @Test
    public void testGetIdForValidPIDIri() {
        String id = "someId";
        String resultId = pidSystem.getId(valueFactory.createIRI("http://example.com/fdp#" + id));
        assertEquals(resultId, id);
    }

}
