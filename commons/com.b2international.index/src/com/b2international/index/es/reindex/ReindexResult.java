/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.index.es.reindex;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 8.9
 */
@JsonDeserialize(builder = ReindexResult.Builder.class)
public class ReindexResult implements Serializable {

	private static final long serialVersionUID = 4201052194859583099L;

	private final String took;

	// use boxed Long for values where numbers higher than 0 matter the most (this is also to compact the result object)
	private final Long createdDocuments;
	private final Long updatedDocuments;
	private final Long deletedDocuments;
	private final Long noops;
	private final Long versionConflicts;

	// use primitve long to always show total number of affected documents
	private final long totalDocuments;

	private final String sourceIndex;
	private final String destinationIndex;

	private final String remoteAddress;

	private final boolean refresh;

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {

		private String took;

		private Long createdDocuments;
		private Long updatedDocuments;
		private Long deletedDocuments;
		private Long noops;
		private Long versionConflicts;

		private long totalDocuments;

		private String sourceIndex;
		private String destinationIndex;

		private String remoteAddress;

		private boolean refresh;

		private Builder() {}

		public Builder took(String took) {
			this.took = took;
			return this;
		}

		public Builder createdDocuments(long createdDocuments) {
			this.createdDocuments = createdDocuments > 0 ? Long.valueOf(createdDocuments) : null;
			return this;
		}

		public Builder updatedDocuments(long updatedDocuments) {
			this.updatedDocuments = updatedDocuments > 0 ? Long.valueOf(updatedDocuments) : null;
			return this;
		}

		public Builder deletedDocuments(long deletedDocuments) {
			this.deletedDocuments = deletedDocuments > 0 ? Long.valueOf(deletedDocuments) : null;
			return this;
		}

		public Builder noops(long noops) {
			this.noops = noops > 0 ? Long.valueOf(noops) : null;
			return this;
		}

		public Builder versionConflicts(long versionConflicts) {
			this.versionConflicts = versionConflicts > 0 ? Long.valueOf(versionConflicts) : null;
			return this;
		}

		public Builder totalDocuments(long totalDocuments) {
			this.totalDocuments = totalDocuments;
			return this;
		}

		public Builder sourceIndex(String sourceIndex) {
			this.sourceIndex = sourceIndex;
			return this;
		}

		public Builder destinationIndex(String destinationIndex) {
			this.destinationIndex = destinationIndex;
			return this;
		}

		public Builder remoteAddress(String remoteAddress) {
			this.remoteAddress = remoteAddress;
			return this;
		}

		public Builder refresh(boolean refresh) {
			this.refresh = refresh;
			return this;
		}

		public ReindexResult build() {
			return new ReindexResult(took, createdDocuments, updatedDocuments, deletedDocuments, noops, versionConflicts, totalDocuments, sourceIndex,
					destinationIndex, remoteAddress, refresh);
		}

	}

	private ReindexResult(
			String took,
			Long createdDocuments,
			Long updatedDocuments,
			Long deletedDocuments,
			Long noops,
			Long versionConflicts,
			long totalDocuments,
			String sourceIndex,
			String destinationIndex,
			String remoteAddress,
			boolean refresh) {
		this.took = took;
		this.createdDocuments = createdDocuments;
		this.updatedDocuments = updatedDocuments;
		this.deletedDocuments = deletedDocuments;
		this.noops = noops;
		this.versionConflicts = versionConflicts;
		this.totalDocuments = totalDocuments;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
		this.remoteAddress = remoteAddress;
		this.refresh = refresh;
	}

	public String getTook() {
		return took;
	}

	public Long getCreatedDocuments() {
		return createdDocuments;
	}

	public Long getUpdatedDocuments() {
		return updatedDocuments;
	}

	public Long getDeletedDocuments() {
		return deletedDocuments;
	}

	public Long getNoops() {
		return noops;
	}

	public Long getVersionConflicts() {
		return versionConflicts;
	}

	public long getTotalDocuments() {
		return totalDocuments;
	}

	public String getSourceIndex() {
		return sourceIndex;
	}

	public String getDestinationIndex() {
		return destinationIndex;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public boolean isRefresh() {
		return refresh;
	}

	@Override
	public String toString() {
		return "ReindexResult ["
				+ (took != null ? "took=" + took + ", " : "")
				+ (createdDocuments != null ? "createdDocuments=" + createdDocuments + ", " : "")
				+ (updatedDocuments != null ? "updatedDocuments=" + updatedDocuments + ", " : "")
				+ (deletedDocuments != null ? "deletedDocuments=" + deletedDocuments + ", " : "")
				+ (noops != null ? "noops=" + noops + ", " : "")
				+ (versionConflicts != null ? "versionConflicts=" + versionConflicts + ", " : "")
				+ "totalDocuments=" + totalDocuments + ", "
				+ (sourceIndex != null ? "sourceIndex=" + sourceIndex + ", " : "")
				+ (destinationIndex != null ? "destinationIndex=" + destinationIndex + ", " : "")
				+ (remoteAddress != null ? "remoteAddress=" + remoteAddress + ", " : "")
				+ "refresh=" + refresh + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDocuments, deletedDocuments, destinationIndex, noops, refresh, remoteAddress, sourceIndex, took, totalDocuments,
				updatedDocuments, versionConflicts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		final ReindexResult other = (ReindexResult) obj;
		return Objects.equals(createdDocuments, other.createdDocuments) && Objects.equals(deletedDocuments, other.deletedDocuments)
				&& Objects.equals(destinationIndex, other.destinationIndex) && Objects.equals(noops, other.noops) && refresh == other.refresh
				&& Objects.equals(remoteAddress, other.remoteAddress) && Objects.equals(sourceIndex, other.sourceIndex)
				&& Objects.equals(took, other.took) && totalDocuments == other.totalDocuments
				&& Objects.equals(updatedDocuments, other.updatedDocuments) && Objects.equals(versionConflicts, other.versionConflicts);
	}

}
