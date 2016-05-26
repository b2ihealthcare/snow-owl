package com.b2international.index.lucene;

import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermFilter;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

public enum EmptyIndexField implements IndexField<Object> {
	
	INSTANCE;
	
	@Override
	public TermFilter toTermFilter(Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Term toTerm(Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public TermQuery toQuery(Object value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public PrefixQuery toExistsQuery() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeAll(Document doc) {
	}
	
	@Override
	public Set<String> getValuesAsStringSet(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<String> getValuesAsStringList(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<Object> getValues(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getValueAsString(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Object getValue(Document doc) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String fieldName() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Filter createTermsFilter(Iterable<Object> values) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Sort createSort() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Filter createBytesRefFilter(Iterable<BytesRef> bytesRefs) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void copyTo(Document source, Document target) {
	}
	
	@Override
	public void addTo(Document doc, Object value) {
	}	
}
