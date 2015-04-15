package com.b2international.snowowl.datastore.internal.branch;

import java.util.concurrent.atomic.AtomicLong;

import com.b2international.snowowl.datastore.branch.TimestampProvider;

public class AtomicLongTimestampAuthority implements TimestampProvider {

	private AtomicLong timestampAuthority = new AtomicLong(0L);
	
	@Override
	public long getTimestamp() {
		return timestampAuthority.getAndIncrement();
	}
	
	void advance(long delta) {
		timestampAuthority.addAndGet(delta);
	}

}
