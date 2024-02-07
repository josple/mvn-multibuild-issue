package io.gib.appcommon;

import org.junit.Test;

public class TestCommon {

	@Test
	public void test() throws InterruptedException {
		System.out.println("Testing Common. Waiting 10 seconds.");
		synchronized (this) {
			this.wait(10_000);
		}
	}
}
