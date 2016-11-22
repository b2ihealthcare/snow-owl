/* The default cardinality of each attribute, where not explicitly stated, is [1..*].
 * Therefore, the following two expression constraints are equivalent.
 */
  
<373873005|Pharmaceutical / biologic product|:
	127489000|Has active ingredient| = <105590001|Substance|

<373873005|Pharmaceutical / biologic product|:
	[1..*] 127489000|Has active ingredient| = <105590001|Substance|