/* Exclusion can also be applied to the membership of a reference set. For example,
 * the following expression constraint returns the set of lung disorders which are
 * not members of the SNOMED to ICD-10 map. That is, the set of descendants or
 * self of 'disorder of lung' minus the set of members of the 'ICD-10 complex map
 * reference set'.
 */
 
<<19829001|Disorder of lung| MINUS ^447562003|ICD-10 complex map reference set|