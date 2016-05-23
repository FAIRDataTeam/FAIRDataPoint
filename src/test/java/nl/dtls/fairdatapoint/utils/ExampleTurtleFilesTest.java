/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.utils;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

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
        String fileName = ExampleTurtleFiles.FDP_METADATA;
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
