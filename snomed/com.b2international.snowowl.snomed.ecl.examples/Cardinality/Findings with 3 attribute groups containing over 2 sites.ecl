/* The following ECL should return all clinical findings that have exactly 
 * three attribute groups each containing 2 or more finding sites with anatomical
 * structures.
 */
 
<404684003|Clinical finding|:
    [3..3] {[2..*] 363698007|Finding site| = <91723000|Anatomical structure|}