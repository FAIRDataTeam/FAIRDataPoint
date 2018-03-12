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
package nl.dtls.fairdatapoint.service.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import nl.dtls.fairdatapoint.service.FairSearchClient;
import org.apache.http.HttpStatus;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FairSearchClientImplTest class unit tests
 *
 * @author Rajaram Kaliyaperumal <rr.kaliyaperumal@gmail.com>
 * @since 2017-10-30
 * @version 0.1
 */
public class FairSearchClientImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
            FairSearchClientImplTest.class.getName());

    private static FairSearchClient FSE;
    private static final ValueFactory VF = SimpleValueFactory.getInstance();
    private static final IRI FDP_URI = VF.createIRI("http://example.com/fdp");

    @BeforeClass
    public static void setUp() {
        FSE = Mockito.mock(FairSearchClient.class);
        when(FSE.submitFdpUri(FDP_URI)).thenReturn(CompletableFuture.completedFuture(
                HttpStatus.SC_ACCEPTED));
    }

    /**
     * Test NULL fdp uri. This test is expected to thrown an error
     */
    @Test(expected = IllegalStateException.class)
    public void nullFDPUri() {
        FairSearchClient search = new FairSearchClientImpl();
        LOGGER.info("Test for NULL fdp url");
        search.submitFdpUri(null);
    }

    /**
     * Test VALID fdp uri. This test is expected to pass
     */
    @Test
    public void validFDPUri() throws InterruptedException, ExecutionException {
        LOGGER.info("Test for valid fdp url");
        CompletableFuture result = FSE.submitFdpUri(FDP_URI);
        assertEquals(HttpStatus.SC_ACCEPTED, (int)result.get());
    }

}
