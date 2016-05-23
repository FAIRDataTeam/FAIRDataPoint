/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import java.util.List;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author rajaram
 */
public class ExampleTurtleFilesTest {
    
    public ExampleTurtleFilesTest() {
    }
    
    /**
     * Test of getTurtleAsString method, the test is excepted to pass.
     */
    @Test(expected = NullPointerException.class) 
    public void testGetTurtleAsStringNonExistingFile() {
        System.out.println("getTurtleAsString");
        String fileName = "blabla.ttl";
        ExampleTurtleFiles.getTurtleAsString(fileName);
    }

    /**
     * Test of getTurtleAsString method, the test is excepted to pass.
     */
    @Test
    public void testGetTurtleAsStringExistingFile() {
        System.out.println("getTurtleAsString");
        String fileName = ExampleTurtleFiles.EXAMPLE_FDP_METADATA_FILE;
        String result = ExampleTurtleFiles.getTurtleAsString(fileName);
        assertTrue(result.length() > 0);
    }
    
    /**
     * Test of getExampleTurtleFileNames method, the test is excepted to pass.
     */
    @Test
    public void getExampleTurtleFileNames() {
        System.out.println("getExampleTurtleFileNames");
        List<String> result = ExampleTurtleFiles.getExampleTurtleFileNames();
        assertTrue(result.size() > 0);
    }
    
}
