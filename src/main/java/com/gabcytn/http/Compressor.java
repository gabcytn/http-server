package com.gabcytn.http;

@FunctionalInterface
public interface Compressor {
	byte[] compress(String payload);
}
