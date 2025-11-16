package com.gabcytn.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class GzipCompressor {
  private static final Logger LOG = LoggerFactory.getLogger(GzipCompressor.class);
  public GzipCompressor() {}

  public byte[] compress(String payload) {
    byte[] uncompressedPayload = payload.getBytes(StandardCharsets.UTF_8);
    byte[] compressedPayload = new byte[0];
    try (ByteArrayOutputStream byteArrayOutputStream =
            new ByteArrayOutputStream(uncompressedPayload.length);
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream); ) {
      gzipOutputStream.write(uncompressedPayload);
      gzipOutputStream.finish();

      compressedPayload = byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      LOG.error("Error compressing with Gzip: {}", e.getMessage());
    }

    return compressedPayload;
  }
}
