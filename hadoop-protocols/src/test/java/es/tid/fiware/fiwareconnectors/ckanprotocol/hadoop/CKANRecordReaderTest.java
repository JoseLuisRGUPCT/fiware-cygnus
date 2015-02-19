/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
 *
 * This file is part of fiware-connectors (FI-WARE project).
 *
 * fiware-connectors is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * fiware-connectors is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with fiware-connectors. If not, see
 * http://www.gnu.org/licenses/.
 *
 * For those usages not covered by the GNU Affero General Public License please contact with
 * francisco.romerobueno at telefonica dot com
 */
package es.tid.fiware.fiwareconnectors.ckanprotocol.hadoop;

import es.tid.fiware.fiwareconnectors.ckanprotocol.backends.ckan.CKANBackend;
import es.tid.fiware.fiwareconnectors.ckanprotocol.hadoop.ckan.CKANInputSplit;
import es.tid.fiware.fiwareconnectors.ckanprotocol.hadoop.ckan.CKANRecordReader;
import java.io.IOException;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.json.simple.JSONArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 * @author frb
 */
@RunWith(MockitoJUnitRunner.class)
public class CKANRecordReaderTest {
    
    // instance to be tested
    private CKANRecordReader recordReader;
    
    // mocks
    @Mock
    private CKANBackend backend;
    @Mock
    private CKANInputSplit inputSplit;
    @Mock
    private TaskAttemptContext taskAttemptContext;
    
    // constants
    private final String resId = "1234567890";
    private final long firstRecordIndex = 0;
    private final long length = 1000;
    
    /**
     * Sets up tests by creating a unique instance of the tested class, and by defining the behaviour of the mocked
     * classes.
     *  
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        // set up the instance of the tested class
        recordReader = new CKANRecordReader(backend, inputSplit, taskAttemptContext);
        
        // set up other instances
        JSONArray records = new JSONArray();
        
        // set up the behaviour of the mocked classes
        when(backend.getRecords(resId, firstRecordIndex, length)).thenReturn(records);
        when(inputSplit.getFirstRecordIndex()).thenReturn(firstRecordIndex);
        when(inputSplit.getLength()).thenReturn(length);
        when(inputSplit.getResId()).thenReturn(resId);
    } // setUp
    
    /**
     * Test of initialize method, of class CKANRecordReader.
     */
    @Test
    public void testInitialize() {
        System.out.println("Testing CKANRecordReader.initialize");
        
        try {
            recordReader.initialize(inputSplit, taskAttemptContext);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            assertTrue(true);
        } // try catch finally
        
    } // testInitialize
    
    /**
     * Test of nextKeyValue method, of class CKANRecordReader.
     */
    @Test
    public void testNextKeyValue() {
        System.out.println("Testing CKANRecordReader.nextKeyValue");
        
        try {
            recordReader.initialize(inputSplit, taskAttemptContext);
            assertTrue(recordReader.nextKeyValue());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } finally {
            assertTrue(true);
        } // try catch finally
    } // testNextKeyValue
    
    /**
     * Test of getCurrentKey method, of class CKANRecordReader.
     */
    @Test
    public void testGetCurrentKey() {
        System.out.println("Testing CKANRecordReader.getCurrentKey");
        
        try {
            recordReader.initialize(inputSplit, taskAttemptContext);
            recordReader.nextKeyValue();
            assertEquals(0, recordReader.getCurrentKey());
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InterruptedException e) {
            fail(e.getMessage());
        } finally {
            assertTrue(true);
        } // try catch finally
    } // testGetCurrentKey
    
    /**
     * Test of getCurrentValue method, of class CKANRecordReader.
     */
    @Test
    public void testGetCurrentValue() {
        System.out.println("Testing CKANRecordReader.getCurrentValue");
    } // testGetCurrentValue
    
    /**
     * Test of getProgress method, of class CKANRecordReader.
     */
    @Test
    public void testGetProgress() {
        System.out.println("Testing CKANRecordReader.getProgress");
    } // testGetProgress
    
    /**
     * Test of close method, of class CKANRecordReader.
     */
    @Test
    public void testClose() {
        System.out.println("Testing CKANRecordReader.close");
        assertTrue(true);
    } // testClose
    
} // CKANRecordReaderTest
