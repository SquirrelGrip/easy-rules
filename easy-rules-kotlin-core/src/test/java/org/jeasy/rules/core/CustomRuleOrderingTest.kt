/*
 * The MIT License
 *
 *  Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */
package org.jeasy.rules.core

import org.jeasy.rules.api.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CustomRuleOrderingTest : AbstractTest() {
    @Mock
    private val rule1: MyRule? = null

    @Mock
    private val rule2: MyRule? = null
    @Test
    @Throws(Exception::class)
    fun whenCompareToIsOverridden_thenShouldExecuteRulesInTheCustomOrder() {
        // Given
        Mockito.`when`(rule1.name).thenReturn("a")
        Mockito.`when`(rule1.priority).thenReturn(1)
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.name).thenReturn("b")
        Mockito.`when`(rule2.priority).thenReturn(0)
        Mockito.`when`(rule2.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.compareTo(rule1)).thenCallRealMethod()
        rules.register(rule1)
        rules.register(rule2)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        /*
         * By default, if compareTo is not overridden, then rule2 should be executed first (priority 0 < 1).
         * But in this case, the compareTo method order rules by their name, so rule1 should be executed first ("a" < "b")
         */
        val inOrder = Mockito.inOrder(rule1, rule2)
        inOrder.verify<MyRule?>(rule1).execute(facts)
        inOrder.verify<MyRule?>(rule2).execute(facts)
    }

    internal class MyRule : BasicRule() {
        override fun compareTo(rule: Rule?): Int {
            return getName().compareTo(rule.getName())
        }
    }
}