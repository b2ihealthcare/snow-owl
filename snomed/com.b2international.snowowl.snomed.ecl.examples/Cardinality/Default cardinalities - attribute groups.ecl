/* As with attribute cardinality, the default attribute group cardinality, where not 
 * explicitly stated, is [1..*]. Therefore, the following four expression constraints
 * are equivalent.
 */
 
<373873005|Pharmaceutical / biologic product|:
	{127489000|Has active ingredient| = <105590001|Substance|}

<373873005|Pharmaceutical / biologic product|:
	{[1..*] 127489000|Has active ingredient| = <105590001|Substance|}

<373873005|Pharmaceutical / biologic product|:
	[1..*] {127489000|Has active ingredient| = <105590001|Substance|}

<373873005|Pharmaceutical / biologic product|:
	[1..*] {[1..*] 127489000|Has active ingredient| = <105590001|Substance|}