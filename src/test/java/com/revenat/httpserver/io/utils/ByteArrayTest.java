package com.revenat.httpserver.io.utils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


public class ByteArrayTest {

	private ByteArray array;

	@Before
	public void setup() {
		array = new ByteArray();
	}

	@Test
	public void isEmptyWhenCreated() throws Exception {
		assertThat(array.size(), equalTo(0));
	}

	@Test
	public void incrementsSizeWhenAddValues() throws Exception {
		array.add((byte) 10);

		assertThat(array.size(), equalTo(1));
	}
	
	@Test
	public void hasUnlimitedCapacity() throws Exception {
		byte[] bigArray = createBigArray(1_000_000);
		
		array.add(bigArray, 0, bigArray.length);
		
		assertThat(array.size(), equalTo(1_000_000));
	}
	
	private static byte[] createBigArray(int numberOfBytes) {
		byte[] array = new byte[numberOfBytes];
		Arrays.fill(array, (byte)100);
		return array;
	}
	
	@Test
	public void returnsFalseForLineFeedIfSizeLessThan2() throws Exception {
		byte[] data = "H".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isLineFeed(), is(false));
		
	}

	@Test
	public void returnsFalseIfDoesNotContainLineFeedAthTheEnd() throws Exception {
		byte[] data = "Hello Jack".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isLineFeed(), is(false));
		
	}

	@Test
	public void returnsTrueIfContainsLineFeedAtTheEnd() throws Exception {
		byte[] data = "Hello Jack\r\n".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isLineFeed(), is(true));
	}
	
	@Test
	public void returnsFalseForEmptyLineIfSizeLessThan4() throws Exception {
		byte[] data = "Bil".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isEmptyLine(), is(false));
		
	}
	
	@Test
	public void returnsFalseIfDoesNotContainEmptyLineAtTheEnd() throws Exception {
		byte[] data = "Hello Jack\r\n".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isEmptyLine(), is(false));
		
	}

	@Test
	public void returnsTrueIfContainsEmptyLineAtTheEnd() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		for (byte value : data) {
			array.add(value);
		}

		assertThat(array.isEmptyLine(), is(true));
	}

	@Test
	public void canReceiveDataFromArray() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();

		array.add(data, 0, data.length);

		assertThat(array.size(), equalTo(data.length));

	}

	@Test
	public void returnsArrayWithValues() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		array.add(data, 0, data.length);

		assertThat(array.toArray(), equalTo(data));

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionIfAddValuesWithFromIndexLessThatZero() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		array.add(data, -1, data.length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionIfAddValuesWithFromIndexEqualOrGreaterThatValuesLength() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		array.add(data, data.length, data.length);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionIfAddValuesWithCountLessThatZero() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		array.add(data, 0, -1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void throwsExceptionIfAddValuesWithCountGreaterThanValuesLeft() throws Exception {
		byte[] data = "Hello Jack\r\n\r\n".getBytes();
		array.add(data, 5, 15);
	}
}
