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
public class ExampleFilesUtilsTest {
    
    public ExampleFilesUtilsTest() {
    }
    
    /**
     * Test of getTurtleAsString method, the test is excepted to pass.
     */
    @Test(expected = NullPointerException.class) 
    public void testGetTurtleAsStringNonExistingFile() {
        System.out.println("getTurtleAsString");
        String fileName = "blabla.ttl";
        ExampleFilesUtils.getFileContentAsString(fileName);
    }

    /**
     * Test of getTurtleAsString method, the test is excepted to pass.
     */
    @Test
    public void testGetTurtleAsStringExistingFile() {
        System.out.println("getTurtleAsString");
        String fileName = ExampleFilesUtils.FDP_METADATA_FILE;
        String result = ExampleFilesUtils.getFileContentAsString(fileName);
        assertTrue(result.length() > 0);
    }
    
    /**
     * Test of getExampleTurtleFileNames method, the test is excepted to pass.
     */
    @Test
    public void getExampleTurtleFileNames() {
        System.out.println("getExampleTurtleFileNames");
        List<String> result = ExampleFilesUtils.getTurtleFileNames();
        assertTrue(result.size() > 0);
    }
    
}
