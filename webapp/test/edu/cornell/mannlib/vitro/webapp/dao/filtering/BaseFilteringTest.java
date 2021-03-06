/*
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
*/

package edu.cornell.mannlib.vitro.webapp.dao.filtering;

import java.util.Arrays;
import java.util.List;

import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.fn.comparison.ComparisonFunctors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class BaseFilteringTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFilterMethods(){        
        List numbers = Arrays.asList( 1,2,3,4,5,6,7,8,9,10 );
        UnaryFunctor<Integer,Boolean> greaterThan3 =
            ComparisonFunctors.greater(3);
        
        BaseFiltering b = new BaseFiltering();
        List filteredNum = b.filter(numbers,greaterThan3);
        Assert.assertNotNull(filteredNum);
        Assert.assertTrue("expected 7 found "+filteredNum.size() , filteredNum.size() == 7);
    }
}
