package com.b2international.snowowl.snomed.api.rest.domain;

public class SnomedConceptMini {

	private String id;
	private String fsn;
	
	public SnomedConceptMini(String id) {
		this.id = id;
	}
	
	public SnomedConceptMini(String id, String fsn) {
		this.id = id;
		this.fsn = fsn;
	}

	public void setFsn(String fsn) {
		this.fsn = fsn;
	}
	
	public String getId() {
		return id;
	}
	
	public String getFsn() {
		return fsn;
	}
	
}
