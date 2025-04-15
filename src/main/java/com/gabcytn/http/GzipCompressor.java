package com.gabcytn.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor
{
    public GzipCompressor () {}

    public byte[] compress (String payload)
    {
        byte[] uncompressedPayload = payload.getBytes(StandardCharsets.UTF_8);
        byte[] compressedPayload = new byte[0];
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(uncompressedPayload.length);
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        ) {
            gzipOutputStream.write(uncompressedPayload);
            gzipOutputStream.finish();

            compressedPayload = byteArrayOutputStream.toByteArray();
        }
        catch (IOException e)
        {
            System.err.println("Error compressing with Gzip: " + e.getMessage());
        }
        
        return compressedPayload;
    }
}
