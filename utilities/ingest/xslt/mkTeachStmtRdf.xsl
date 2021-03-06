<?xml version="1.0"?>
<xsl:stylesheet version='2.0'
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs='http://www.w3.org/2001/XMLSchema'
	xmlns:core="http://vivoweb.org/ontology/core#"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
        xmlns:bibo="http://purl.org/ontology/bibo/"
        xmlns:foaf="http://xmlns.com/foaf/0.1/"
        xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:aiteachstmt="http://vivoweb.org/ontology/activity-insight"
	xmlns:acti="http://vivoweb.org/ontology/activity-insight#"
	xmlns:dm="http://www.digitalmeasures.com/schema/data"	
	xmlns:vfx='http://vivoweb.org/ext/functions'
	exclude-result-prefixes='xs vfx dm aiteachstmt'
	>

<xsl:param name='unoMapFile'  required='yes'/>
<xsl:param name='rawXmlPath' required='yes'/>
<xsl:param name='extPerIn' required='yes'/>
<xsl:param name='extPerOut' required='yes'/>

<xsl:output method='xml' indent='yes'/>
<xsl:strip-space elements="*"/>

<xsl:include href='commonvars.xsl'/>

<xsl:variable name='unomap'
	select="document($unoMapFile)/Mapping"/>

<xsl:variable name='extantPersons'
	select="document($extPerIn)/ExtantPersons"/>


<xsl:template match='/aiteachstmt:TEACHING_STATEMENT_PERSON_LIST'>
<rdf:RDF>

<xsl:variable name='prenewps'>
<xsl:element name='ExtantPersons' inherit-namespaces='no'>
<xsl:for-each select='aiteachstmt:PERSON'>
<xsl:if test='vfx:goodName(aiteachstmt:fname, aiteachstmt:mname, aiteachstmt:lname)'>
<xsl:variable name='ctr'  select='@counter'/>
<xsl:variable name='uno' select='$unomap/map[position()=$ctr]/@nuno'/>
<xsl:variable name='kUri' 
	select='vfx:knownUriByNetidOrName(aiteachstmt:fname, 
					  aiteachstmt:mname, 
					  aiteachstmt:lname,
					  aiteachstmt:netid,
					  $extantPersons)'/>
<xsl:variable name='furi' 
select="if($kUri != '') then $kUri 
                            else concat($g_instance,$uno)"/>


<xsl:if test='$kUri = ""'>
<xsl:element name='person' inherit-namespaces='no'>
<xsl:element name='uri' inherit-namespaces='no'>
<xsl:value-of select='concat("NEW-",$furi)'/></xsl:element>
<xsl:element name='fname' inherit-namespaces='no'>
<xsl:value-of select='aiteachstmt:fname'/></xsl:element>
<xsl:element name='mname' inherit-namespaces='no'>
<xsl:value-of select='aiteachstmt:mname'/></xsl:element>
<xsl:element name='lname' inherit-namespaces='no'>
<xsl:value-of select='aiteachstmt:lname'/></xsl:element>
<xsl:element name='netid' inherit-namespaces='no'>
<xsl:value-of select='aiteachstmt:netid'/></xsl:element>
</xsl:element>

</xsl:if>
</xsl:if>
</xsl:for-each>
</xsl:element>
</xsl:variable>

<xsl:variable name='newps'>
<xsl:call-template name='newPeople'>
<xsl:with-param name='knowns' select='$prenewps/ExtantPersons'/>
</xsl:call-template>
</xsl:variable>



<xsl:for-each select='aiteachstmt:PERSON'>

<xsl:variable name='ctr'  select='@counter'/>
<xsl:variable name='uno' select='$unomap/map[position()=$ctr]/@nuno'/>

<xsl:variable name='knownUri' 
select='vfx:knownUri(aiteachstmt:fname, aiteachstmt:mname, aiteachstmt:lname, $extantPersons)'/>

<xsl:variable name='peruri' 
select="if($knownUri != '') then $knownUri else concat($g_instance,$uno)"/>

<xsl:if test='$knownUri != "" and aiteachstmt:netid != ""'>

<rdf:Description rdf:about="{$peruri}">
<rdf:type rdf:resource=
	'http://vivoweb.org/ontology/activity-insight#ActivityInsightPerson'/>
</rdf:Description>
</xsl:if>

<xsl:if test='$knownUri = ""'>
<rdf:Description rdf:about="{$peruri}">
<rdf:type rdf:resource=
	'http://vitro.mannlib.cornell.edu/ns/vitro/0.7#Flag1Value1Thing'/>
<rdf:type rdf:resource='http://xmlns.com/foaf/0.1/Person'/>
<xsl:if test='aiteachstmt:netid != ""'>
<rdf:type rdf:resource=
	'http://vivoweb.org/ontology/activity-insight#ActivityInsightPerson'/>
</xsl:if>
<rdfs:label>
<xsl:value-of select='vfx:trim(aiteachstmt:fullname)'/>
</rdfs:label>

<core:middleName><xsl:value-of select='aiteachstmt:mname'/></core:middleName>
<core:firstName><xsl:value-of select='aiteachstmt:fname'/></core:firstName>
<foaf:firstName><xsl:value-of select='aiteachstmt:fname'/></foaf:firstName>
<core:lastName><xsl:value-of select='aiteachstmt:lname'/></core:lastName>
<foaf:lastName><xsl:value-of select='aiteachstmt:lname'/></foaf:lastName>

<xsl:if test='aiteachstmt:netid != ""'>

<xsl:variable name='nidxml' 
	select="concat($rawXmlPath,'/',
			aiteachstmt:netid , '.xml')"/>

<!-- do not bother with these if file is not available -->
<xsl:if test='doc-available($nidxml)'>
<xsl:variable name='pci' select="document($nidxml)//dm:PCI"/>
<core:workEmail><xsl:value-of select='$pci/dm:EMAIL'/></core:workEmail>
<bibo:prefixName><xsl:value-of select='$pci/dm:PREFIX'/> </bibo:prefixName>
<core:workFax>
<xsl:value-of select='$pci/dm:FAX1'/>-<xsl:value-of select='$pci/dm:FAX2'/>-<xsl:value-of select='$pci/dm:FAX3'/>
</core:workFax>
<core:workPhone>
<xsl:value-of select='$pci/dm:OPHONE1'/>-<xsl:value-of select='$pci/dm:OPHONE2'/>-<xsl:value-of select='$pci/dm:OPHONE3'/>
</core:workPhone>
</xsl:if>

</xsl:if>
</rdf:Description>
</xsl:if>


<xsl:call-template name='process-object'>
<xsl:with-param name='list' select='aiteachstmt:TEACHING_STATEMENT_LIST'/>
<xsl:with-param name='objref' select="$peruri"/>
<xsl:with-param name='nid' select="aiteachstmt:netid"/>
</xsl:call-template>
</xsl:for-each>

<xsl:result-document href='{$extPerOut}'>
<xsl:element name='ExtantPersons' namespace=''>
<xsl:for-each select='aiteachstmt:TEACHING_STATEMENT'>

<xsl:variable name='ctr'  select='@counter'/>
<xsl:variable name='uno' select='$unomap/map[position()=$ctr]/@nuno'/>
<xsl:variable name='knownUri' 
select='vfx:knownUri(aiteachstmt:fname, aiteachstmt:mname, aiteachstmt:lname, $extantPersons)'/>

<xsl:variable name='peruri' 
select="if($knownUri != '') then $knownUri else concat($g_instance,$uno)"/>

<!-- must prevent duplicates -->
<xsl:if test="$knownUri = ''">
<xsl:element name='person' namespace=''>
<xsl:element name='uri'  namespace=''>
<xsl:value-of select='$peruri'/>
</xsl:element>
<xsl:element name='fname' namespace=''>
<xsl:value-of select='aiteachstmt:fname'/>
</xsl:element>
<xsl:element name='mname' namespace=''>
<xsl:value-of select='aiteachstmt:mname'/>
</xsl:element>
<xsl:element name='lname' namespace=''>
<xsl:value-of select='aiteachstmt:lname'/>
</xsl:element>
<xsl:element name='netid' namespace=''>
<xsl:value-of select='aiteachstmt:netid'/>
</xsl:element>

</xsl:element>
</xsl:if>

</xsl:for-each>
</xsl:element>
</xsl:result-document>
</rdf:RDF>
<xsl:value-of select='$NL'/>
</xsl:template>

<!-- =================================================== -->
<!-- =================================================== -->
<xsl:template name='process-object'>
<xsl:param name='list'/>
<xsl:param name='objref'/>
<xsl:param name='nid'/>
<xsl:for-each select='$list/aiteachstmt:TEACHING_STATEMENT'>
<!-- =================================================== -->

<xsl:variable name='objid' select='@id'/>
<xsl:variable name='nidxml' select="concat($rawXmlPath,'/',$nid , '.xml')"/>

<rdf:Description rdf:about="{$objref}">
<xsl:for-each 
select='document($nidxml)//dm:TEACHING_STATEMENT[
			@id = $objid]/dm:TEACHING_STATEMENT_KEYWORD'>
<xsl:if test='vfx:simple-trim(./dm:KEYWORD) != ""'>
<acti:teachingKeyword>
<xsl:value-of select='vfx:simple-trim(./dm:KEYWORD)'/>
</acti:teachingKeyword>
</xsl:if>
</xsl:for-each>
<xsl:if test='document($nidxml)//dm:TEACHING_STATEMENT[
				@id = $objid]/dm:INTERESTS != ""'>
<core:teachingOverview>
<xsl:value-of select='document($nidxml)//dm:TEACHING_STATEMENT[
				@id = $objid]/dm:INTERESTS'/> 
</core:teachingOverview>
</xsl:if>
</rdf:Description>

</xsl:for-each>
</xsl:template>

<!-- ================================== -->


<xsl:include href='vivofuncs.xsl'/>

</xsl:stylesheet>
