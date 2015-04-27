package com.b2international.snowowl.datastore.store;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 4.1
 */
class Types {

	static class EmptyData {
	}
	
	static class Data {
		@JsonProperty
		public String name;
		
		@JsonCreator
		public Data(@JsonProperty("name") String name) {
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(name);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (!(obj instanceof Data)) return false;
			return name.equals(((Data) obj).name);
		}
		
	}
	
}
