<?xml version="1.0"?>
<xsl:stylesheet version='2.0'
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs='http://www.w3.org/2001/XMLSchema'
	xmlns:core="http://vivoweb.org/ontology/core#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:bibo="http://purl.org/ontology/bibo/"
        xmlns:foaf="http://xmlns.com/foaf/0.1/"
        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:aiic="http://vivoweb.org/ontology/activity-insight"
	xmlns:acti="http://vivoweb.org/ontology/activity-insight#"
        xmlns="http://vivoweb.org/ontology/activity-insight"
xmlns:dm="http://www.digitalmeasures.com/schema/data"	
	xmlns:vfx='http://vivoweb.org/ext/functions'
	exclude-result-prefixes='xs' >


<xsl:param name='cutoff' select='80.0'/>
<xsl:param name='wF' select='30.0'/>
<xsl:param name='wM' select='20.0'/>
<xsl:param name='wL' select='50.0'/>

<xsl:param name='wNullFirst' select='1.0'/>
<xsl:param name='wNullMiddle' select='1.0'/>

<xsl:param name='wBeginEndFirst' select='.4'/>
<xsl:param name='wBeginEndMiddle' select='.25'/>

<xsl:param name='wNoMatchFirst' select='-.25'/>
<xsl:param name='wNoMatchMiddle' select='-.1'/>

<xsl:param name='extPerIn' required='yes'/>


<xsl:output method='xml' encoding='us-ascii'/>


<xsl:include href='commonvars.xsl'/>


<xsl:variable name='extantPersons'
	select="document($extPerIn)/ExtantPersons"/>
<!-- ================================== -->
<xsl:template match='/aiic:INTELLCONT_AUTHOR_LIST'>


<xsl:for-each select='aiic:INTELLCONT_ITEMS_BY_AUTHOR'>
<xsl:value-of select='$NL'/>
<xsl:variable name='gn' select='.'/>


<xsl:value-of select='
concat("[",aiic:FirstName,"|",aiic:MiddleName,"|",aiic:LastName,"]")'/>

<xsl:value-of select='$NL'/>

<xsl:variable name='known' select='vfx:knownUri(aiic:FirstName, aiic:MiddleName, aiic:LastName, $extantPersons)'/>

<xsl:value-of select='count($known)'/>
<xsl:value-of select='$NL'/>

<xsl:for-each select='$known'>

<xsl:variable name='res' select="."/>



<xsl:value-of select='.'/>

<xsl:value-of select='$NL'/>

<xsl:variable name='results' select='$extantPersons/person[uri = $res]'/>


<xsl:value-of select='count($results)'/>-<xsl:value-of select='
concat("{",$results/fname,"|",$results/mname,"|",$results/lname,"}")'/>


<xsl:value-of select='$NL'/>


<!--
<xsl:value-of select='
concat("{",$results[1]/fname,"|",$results[1]/mname,"|",$results[1]/lname,"}")'/>
<xsl:value-of select='
concat("[",$gn/aigrant:FirstName,"|",$gn/aigrant:MiddleName,"|",$gn/aigrant:LastName,"]")'/>
-->
<xsl:value-of select='"Score "'/>
<xsl:variable name='ans' as='xs:float'  select='
vfx:isoScoreTest($gn/aiic:FirstName,$gn/aiic:MiddleName,$gn/aiic:LastName,
             $results[1]/fname,$results[1]/mname, $results[1]/lname)'/>
<xsl:value-of select='number($ans)'/>


<!-- /xsl:if -->


<xsl:value-of select='$NL'/>

</xsl:for-each>
</xsl:for-each>



<xsl:value-of select='$NL'/>
</xsl:template>


<!-- ================================== -->


<xsl:include href='vivofuncs.xsl'/>

</xsl:stylesheet>
