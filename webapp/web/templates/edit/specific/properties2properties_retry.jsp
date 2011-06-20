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
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>

    <tr class="editformcell">
        <td valign="top">
            <c:choose>
                <c:when test="${opMode eq 'equivalentProperty'}">
                    <b>Property<sup>*</sup></b><br/>
                </c:when>
                <c:otherwise>
                    <b>Superproperty<sup>*</sup></b><br/>
                </c:otherwise>
            </c:choose>
            <select name="SuperpropertyURI">
                <form:option name="SuperpropertyURI"/>
            </select>
            <span class="warning"><form:error name="SuperpropertyURI"/></span>
        </td>
    </tr>
    <tr class="editformcell">
        <td valign="top">
            <c:choose>
                <c:when test="${opMode eq 'equivalentProperty'}">
                    <b>Equivalent Property<sup>*</sup></b><br/>
                </c:when>
                <c:otherwise>
                    <b>Subproperty<sup>*</sup></b><br/>
                </c:otherwise>
            </c:choose>
            <select name="SubpropertyURI" >
                <form:option name="SubpropertyURI"/>
            </select>
            <span class="warning"><form:error name="SubpropertyURI"/></span>
        </td>
    </tr>
    
    <input type="hidden" name="operation" value="${operation}" />
    <input type="hidden" name="opMode" value="${opMode}" />