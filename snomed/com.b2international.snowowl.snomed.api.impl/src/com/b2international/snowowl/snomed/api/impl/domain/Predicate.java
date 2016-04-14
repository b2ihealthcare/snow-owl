package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;

public class Predicate {

	private PredicateType type;
	private String relationshipTypeExpression;
	private String relationshipValueExpression;

	public void setType(PredicateType type) {
		this.type = type;
	}
	
	public PredicateType getType() {
		return type;
	}
	
	public void setRelationshipTypeExpression(String relationshipTypeExpression) {
		this.relationshipTypeExpression = relationshipTypeExpression;
	}
	
	public String getRelationshipTypeExpression() {
		return relationshipTypeExpression;
	}
	
	public void setRelationshipValueExpression(
			String relationshipValueExpression) {
		this.relationshipValueExpression = relationshipValueExpression;
	}
	
	public String getRelationshipValueExpression() {
		return relationshipValueExpression;
	}

}
