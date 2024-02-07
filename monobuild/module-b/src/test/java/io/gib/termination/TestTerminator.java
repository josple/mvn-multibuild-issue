package io.gib.termination;

import org.junit.Test;

public class TestTerminator {

	@Test
	public void test() throws InterruptedException {
		System.out.println("Testing terminator. Waiting 30 seconds");
		synchronized (this) {
			this.wait(30_000);
		}
	}
}
