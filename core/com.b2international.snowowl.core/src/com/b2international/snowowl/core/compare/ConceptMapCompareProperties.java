package com.b2international.snowowl.core.compare;

public enum ConceptMapCompareProperties {
	
	CODE_SYSTEM(
		"Code System", 
		true
	),
	
	TERM(
		"Term", 
		true
	),
	
	CODE(
		"Code", 
		true
	);
	
	private final String label;
	private final boolean countProperty;
	
	private ConceptMapCompareProperties(final String label, final boolean countProperty ) {
		this.label = label;
		this.countProperty = countProperty;
	}
	
	public String getLabel() {
		return label;
	}
	
	public boolean isCountProperty() {
		return countProperty;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
