/* When a conjunction and disjunction are both used together, it is mandatory 
 * to use parentheses to disambiguate the meaning of the expression constraint.
 * For example, the following expression constraint is not valid:
 * 
 *     <19829001|Disorder of lung| AND <301867009|Edema of trunk| OR
 *	       ^447562003|ICD-10 complex map reference set|
 *
 * and must be expressed (depending on the intended meaning) as either:
 *
 *     <19829001|Disorder of lung| AND (<301867009|Edema of trunk| AND
 *	       ^447562003|ICD-10 complex map reference set|)
 *
 * or as:
 */
     
(<19829001|Disorder of lung| AND <301867009|Edema of trunk|) AND
	^447562003|ICD-10 complex map reference set|
