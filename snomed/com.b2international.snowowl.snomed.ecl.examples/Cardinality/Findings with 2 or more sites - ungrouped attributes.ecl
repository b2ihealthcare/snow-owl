/* When the attributes to which cardinality are applied can be grouped, but braces
 * are not used in the expression constraint, the cardinality constrains the number
 * of times the attribute may be included in any attribute group. For example, the
 * following expression constraint is satisfied by any clinical finding whose 
 * definition has two or more non-redundant finding sites, irrespective of which 
 * attribute group they are contained in.
 */
 
<404684003|Clinical finding|: 
	[2..*] 363698007|Finding site| = <91723000|Anatomical structure|