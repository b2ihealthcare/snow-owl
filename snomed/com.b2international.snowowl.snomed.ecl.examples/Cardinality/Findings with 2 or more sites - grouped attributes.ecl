/* When braces are placed around an attribute with a given cardinality, there 
 * must exist at least one attribute group for which the given cardinality is 
 * satisfied by attributes in that group. For example, the following expression 
 * constraint is satisfied by any clinical finding whose definition contains an 
 * attribute group with two or more non-redundant finding sites.
 */

<404684003|Clinical finding|: 
	{[2..*] 363698007|Finding site| = <91723000|Anatomical structure|}