package com.revenat.httpserver.io.utils;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;

public class ByteArray {
	private byte[] array = new byte[1024];
	private int size;
	
	public void add(byte value) {
		if (size == array.length) {
			byte[] temp = array;
			array = new byte[array.length * 2];
			System.arraycopy(temp, 0, array, 0, size);
		}
		array[size++] = value;
	}
	
	public void add(byte[] values, int fromIndex, int count) {
		checkParams(values, fromIndex, count);
		
		for (int i = fromIndex; i < fromIndex + count; i++) {
			add(values[i]);
		}
	}

	private static void checkParams(byte[] values, int fromIndex, int count) {
		requireNonNull(values, "Values can not be null");
		
		if (fromIndex < 0 || fromIndex >= values.length) {
			throw new IllegalArgumentException("Illegal fromIndex value. fromIndex should be greater thet 0 and"
					+ " less than values length. Given fromIndex: " + fromIndex + ", values length: " + values.length);
		}
		if (count < 0 || fromIndex + count > values.length) {
			throw new IllegalArgumentException("Illegal count value. fromIndex + count snould not be greater than"
					+ " values length.Given fromIndex: " + fromIndex + ", count: " + count +
					", values length: " + values.length);
		}
	}
	
	public byte[] toArray() {
		return Arrays.copyOf(array, size);
	}
	
	public boolean isLineFeed() {
		if (size >= 2) {
			return array[size-1] == '\n' && array[size-2] == '\r';
		}
		return false;
	}
	
	public boolean isEmptyLine() {
		if (size >= 4) {
			return array[size-1] == '\n' && array[size-2] == '\r'
					&& array[size-3] == '\n' && array[size-4] == '\r';
		}
		return false;
	}
	
	public int size() {
		return size;
	}

}
