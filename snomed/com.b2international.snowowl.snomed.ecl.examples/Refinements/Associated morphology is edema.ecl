/* Adding an attribute refinement to an expression constraint restricts the set
 * of valid clinical meanings to only those whose defining attributes satisfy the
 * given refinement condition. Similarly to SNOMED CT Compositional Grammar, 
 * attribute refinements are placed after a 'colon' (i.e. ":") in the expression 
 * constraint.
 *
 * The example below is satisfied only by the set of lung disorders, which have 
 * an associated morphology that is exactly equal to |Edema|.
 */
 
<19829001|Disorder of lung|:
	116676008|Associated morphology| = 79654002|Edema|