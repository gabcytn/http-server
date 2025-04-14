package com.gabcytn;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    private PrintWriter writer;
    private BufferedReader reader;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
        try
        {
            Socket socket = new Socket("localhost", 8080);
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e)
        {
            System.err.println("Error creating socket connection: " + e.getMessage());
        }
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigorous Test :-)
     */
    public void testRootEndpoint()
    {
        try
        {
            writer.println("GET / HTTP/1.1\r\n\r\n");

            // Read status line
            String response = reader.readLine();
            assertEquals("HTTP/1.1 200 OK", response);

            // Read headers
            Map<String, String> headers = new HashMap<>();
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                String[] header = line.split(":", 2);
                headers.put(header[0].trim().toLowerCase(), header[1].trim().toLowerCase());
            }

            assertNotNull(headers.get("connection"));
            assertNotNull(headers.get("content-length"));

            assertEquals("keep-alive", headers.get("connection"));
            assertEquals("0", headers.get("content-length"));
        }
        catch (IOException e)
        {
            System.err.println("Error opening socket, " + e.getMessage());
        }
    }

    public void testUnexistingEndpoint () {
        writer.println("GET /nonexistent HTTP/1.1\r\n\r\n");

        // Read status line
        String response = null;
        try
        {
            response = reader.readLine();
        } catch (IOException e)
        {
            System.err.println("Error reading status line: " + e.getMessage());
        }
        assertNotNull(response);
        assertEquals("HTTP/1.1 404 Not Found", response);
    }
}
