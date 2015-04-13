package com.b2international.snowowl.datastore.branch;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicLongTimestampAuthority implements TimestampAuthority {

	private AtomicLong timestampAuthority = new AtomicLong(0L);
	
	@Override
	public long getTimestamp() {
		return timestampAuthority.getAndIncrement();
	}
	
	void advance(long delta) {
		timestampAuthority.addAndGet(delta);
	}

}
