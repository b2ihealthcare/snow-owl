package com.b2international.snowowl.core.api;

public interface BranchPath {

	BranchPath parent();
	
	String path();

	boolean isMain();

}