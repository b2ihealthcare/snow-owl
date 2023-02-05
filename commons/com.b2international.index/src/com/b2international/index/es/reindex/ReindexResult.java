/*******************************************************************************
 * Copyright (c) 2023 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.index.es.reindex;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 8.8
 */
@JsonDeserialize(builder = ReindexResult.Builder.class)
public class ReindexResult implements Serializable {

	private static final long serialVersionUID = 4201052194859583099L;

	// use primitve long to always show elapsed time
	private final long elapsedNanos;

	// use boxed Long for values where numbers higher than 0 matter the most (this is also to compact the result object)
	private final Long createdDocuments;
	private final Long updatedDocuments;
	private final Long deletedDocuments;
	private final Long noops;

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

		private long elapsedNanos;

		private Long createdDocuments;
		private Long updatedDocuments;
		private Long deletedDocuments;
		private Long noops;

		private long totalDocuments;

		private String sourceIndex;
		private String destinationIndex;

		private String remoteAddress;

		private boolean refresh;

		private Builder() {}

		public Builder elapsedNanos(long elapsedNanos) {
			this.elapsedNanos = elapsedNanos;
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
			return new ReindexResult(elapsedNanos, createdDocuments, updatedDocuments, deletedDocuments, noops, totalDocuments, sourceIndex,
					destinationIndex, remoteAddress, refresh);
		}

	}

	private ReindexResult(
			long elapsedNanos,
			Long createdDocuments,
			Long updatedDocuments,
			Long deletedDocuments,
			Long noops,
			long totalDocuments,
			String sourceIndex,
			String destinationIndex,
			String remoteAddress,
			boolean refresh) {
		this.elapsedNanos = elapsedNanos;
		this.createdDocuments = createdDocuments;
		this.updatedDocuments = updatedDocuments;
		this.deletedDocuments = deletedDocuments;
		this.noops = noops;
		this.totalDocuments = totalDocuments;
		this.sourceIndex = sourceIndex;
		this.destinationIndex = destinationIndex;
		this.remoteAddress = remoteAddress;
		this.refresh = refresh;
	}

	public long getElapsedNanos() {
		return elapsedNanos;
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
		return "ReindexResult [elapsedNanos=" + elapsedNanos + ", createdDocuments=" + createdDocuments + ", updatedDocuments=" + updatedDocuments
				+ ", deletedDocuments=" + deletedDocuments + ", noops=" + noops + ", totalDocuments=" + totalDocuments + ", "
				+ (sourceIndex != null ? "sourceIndex=" + sourceIndex + ", " : "")
				+ (destinationIndex != null ? "destinationIndex=" + destinationIndex + ", " : "")
				+ (remoteAddress != null ? "remoteAddress=" + remoteAddress + ", " : "") + "refresh=" + refresh + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdDocuments, deletedDocuments, destinationIndex, elapsedNanos, noops, refresh, remoteAddress, sourceIndex,
				totalDocuments, updatedDocuments);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		final ReindexResult other = (ReindexResult) obj;
		return createdDocuments == other.createdDocuments && deletedDocuments == other.deletedDocuments
				&& Objects.equals(destinationIndex, other.destinationIndex) && elapsedNanos == other.elapsedNanos && noops == other.noops
				&& refresh == other.refresh && Objects.equals(remoteAddress, other.remoteAddress) && Objects.equals(sourceIndex, other.sourceIndex)
				&& totalDocuments == other.totalDocuments && updatedDocuments == other.updatedDocuments;
	}

}
