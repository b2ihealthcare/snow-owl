/* Attribute cardinalities and attribute group cardinalities can be used together
 * to achieve a combined effect. For example, to represent the set of clinical 
 * findings which have no attribute groups that contain two or more finding site
 * attributes (in the same attribute group), the following expression constraint 
 * can be used:
 */
 
<404684003|Clinical finding|: 
	[0..0] {[2..*] 363698007|Finding site| = <91723000|Anatomical structure|}