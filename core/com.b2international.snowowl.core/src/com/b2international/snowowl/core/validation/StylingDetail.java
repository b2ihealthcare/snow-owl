package com.b2international.snowowl.core.validation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StylingDetail {
	
	private final int index;
	private final int length;
	
	public StylingDetail(
			@JsonProperty("index") int index,
			@JsonProperty("length") int length) {
		this.index = index;
		this.length = length;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getLength() {
		return length;
	}
	
}
