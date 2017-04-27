/* When more than one conjunction or more than one disjunction is used, parentheses
 * can be optionally applied. For example, the following expression constraints
 * are all valid and equivalent to each other:
 * 
 * <19829001|Disorder of lung| AND <301867009|Edema of trunk| AND
 *     ^447562003|ICD-10 complex map reference set|
 *
 * (<19829001|Disorder of lung| AND <301867009|Edema of trunk|) AND
 *     ^447562003|ICD-10 complex map reference set|
 */
      
<19829001|Disorder of lung| AND (<301867009|Edema of trunk| AND
	^447562003|ICD-10 complex map reference set|)
