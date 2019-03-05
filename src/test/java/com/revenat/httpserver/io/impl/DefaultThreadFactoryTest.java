package com.revenat.httpserver.io.impl;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.concurrent.ThreadFactory;

import org.junit.Before;
import org.junit.Test;

public class DefaultThreadFactoryTest {
	
	private static final Runnable TASK = () -> System.out.println("Test task");
	private static final String THREAD_PREFIX = "test-thread-";
	private ThreadFactory workerThreadFactory;
	
	@Before
	public void setup() {
		workerThreadFactory = new DefaultThreadFactory(THREAD_PREFIX);
	}

	@Test
	public void createsNewThreadEachCall() throws Exception {
		Thread instanceA = workerThreadFactory.newThread(TASK);
		Thread instanceB = workerThreadFactory.newThread(TASK);
		
		assertThat(instanceA, not(sameInstance(instanceB)));
	}
	
	@Test
	public void createsNotDaemonThreads() throws Exception {
		Thread newThread = workerThreadFactory.newThread(TASK);
		
		assertThat(newThread.isDaemon(), equalTo(false));
	}
	
	@Test
	public void createsThreadWithNameContainsSpecifiedNamePrefix() throws Exception {
		Thread instanceA = workerThreadFactory.newThread(TASK);
		Thread instanceB = workerThreadFactory.newThread(TASK);
		
		assertThat(instanceA.getName(), containsString(THREAD_PREFIX));
		assertThat(instanceB.getName(), containsString(THREAD_PREFIX));
	}
	
	@Test
	public void createsThreadsWithUniqueNames() throws Exception {
		Thread instanceA = workerThreadFactory.newThread(TASK);
		Thread instanceB = workerThreadFactory.newThread(TASK);
		
		assertThat(instanceA.getName(), not(equalTo(instanceB.getName())));
		
	}

}
