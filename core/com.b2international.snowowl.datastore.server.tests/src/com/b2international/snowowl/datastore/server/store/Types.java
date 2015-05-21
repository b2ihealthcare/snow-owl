package com.b2international.snowowl.datastore.server.store;

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
	
	static class ComplexData {
		
		@JsonProperty
		private String id;
		
		@JsonProperty
		private String name;
		
		@JsonProperty
		private State state;

		@JsonCreator
		public ComplexData(@JsonProperty("id") String id, @JsonProperty("name") String name, @JsonProperty("state") State state) {
			this.id = id;
			this.name = name;
			this.state = state;
		}
		
		public String getId() {
			return id;
		}
		
		@Override
		public int hashCode() {
			return Objects.hashCode(id);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof ComplexData)) return false;
			return Objects.equals(id, id);
		}
		
	}
	
	static enum State {
		SCHEDULED, RUNNING, FAILED
	}
	
}
