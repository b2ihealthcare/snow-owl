/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.datastore.index;

import java.util.Arrays;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PhraseQuery;

/**
 * Copied from Lucene 3.6.0; the original class has package visibility.
 * 
 * @see PhraseQuery.PostingsAndFreq
 */
public class PostingsAndFreq implements Comparable<PostingsAndFreq> {
	final DocsAndPositionsEnum postings;
    final int docFreq;
    final int position;
    final Term[] terms;
    final int nTerms; // for faster comparisons

    public PostingsAndFreq(DocsAndPositionsEnum postings, int docFreq, int position, Term... terms) {
      this.postings = postings;
      this.docFreq = docFreq;
      this.position = position;
      
      if (null == terms) {
    	  nTerms = 0;
    	  this.terms = null;
      } else {
    	  nTerms = terms.length;
    	  if (terms.length==1) {
    		  this.terms = terms;
    	  } else {
    		  Term[] terms2 = new Term[terms.length];
    		  System.arraycopy(terms, 0, terms2, 0, terms.length);
    		  Arrays.sort(terms2);
    		  this.terms = terms2;
    	  }
      }
    }

    public int compareTo(PostingsAndFreq other) {
      if (docFreq != other.docFreq) {
        return docFreq - other.docFreq;
      }
      if (position != other.position) {
        return position - other.position;
      }
      if (nTerms != other.nTerms) {
        return nTerms - other.nTerms;
      }
      if (nTerms == 0) {
        return 0;
      }
      for (int i=0; i<terms.length; i++) {
        int res = terms[i].compareTo(other.terms[i]);
        if (res!=0) return res;
      }
      return 0;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + docFreq;
      result = prime * result + position;
      for (int i=0; i<nTerms; i++) {
        result = prime * result + terms[i].hashCode(); 
      }
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      PostingsAndFreq other = (PostingsAndFreq) obj;
      if (docFreq != other.docFreq) return false;
      if (position != other.position) return false;
      if (terms == null) return other.terms == null;
      return Arrays.equals(terms, other.terms);
    }
}
