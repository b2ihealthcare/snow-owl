/* An alternative way of representing 'reversed attributes' is called the 
 * 'dot notation'. Using this alternative notation, "<123456|X|.234567|Y|" 
 * represents the set of attribute values (i.e. destination concepts) of the
 * attribute "Y" for descendants or self of concept "X". This is therefore 
 * equivalent to " *: R 234567|Y| = <123456|X|" using the reverse flag.
 *
 * The following expression constraint (which finds the set of body sites for any
 * subtype of bone fracture)
 *
 *    <91723000|Anatomical structure|: 
 *        R 363698007|Finding site| = <125605004 |Fracture of bone|
 *
 * has an equivalent representation using the 'dot notation' of:
 */
 
<91723000|Anatomical structure| AND <125605004|Fracture of bone|.363698007|Finding site|