<%--
Copyright (c) 2011, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--%>

<%@ taglib prefix="form" uri="http://vitro.mannlib.cornell.edu/edit/tags" %>

	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>Name (for convenience only)</b><br/>
				<input type="text" name="Name" value="<form:value name="Name"/>" size="40%" maxlength="120" />
				<font size="2" color="red"><form:error name="Name"/></font>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>Namespace URI</b><br/>
			<input name="NamespaceURI" value="<form:value name="NamespaceURI"/>" size="80%" maxlength="255" />
				<font size="2" color="red"><form:error name="NamespaceURI"/></font>
		</td>
	</tr>
	<tr class="editformcell">
		<td valign="bottom" colspan="3">
			<b>Optional Namespace Prefix (for exports)</b><br/>
			<input name="Prefix" value="<form:value name="Prefix"/>" size="15%" maxlength="25" />
				<font size="2" color="red"><form:error name="Prefix"/></font>
		</td>
	</tr>


