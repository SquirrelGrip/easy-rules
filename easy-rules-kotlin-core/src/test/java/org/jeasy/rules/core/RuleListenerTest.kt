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

import org.jeasy.rules.api.RuleListener
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class RuleListenerTest : AbstractTest() {
    @Mock
    lateinit var ruleListener1: RuleListener

    @Mock
    lateinit var ruleListener2: RuleListener

    @Before
    @Throws(Exception::class)
    override fun setup() {
        super.setup()
        Mockito.`when`(ruleListener1.beforeEvaluate(rule1, facts)).thenReturn(true)
        Mockito.`when`(ruleListener2.beforeEvaluate(rule1, facts)).thenReturn(true)
        rulesEngine.registerRuleListener(ruleListener1)
        rulesEngine.registerRuleListener(ruleListener2)
    }

    @Test
    fun whenTheRuleExecutesSuccessfully_thenOnSuccessShouldBeExecuted() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        val inOrder = Mockito.inOrder(rule1, fact1, fact2, ruleListener1, ruleListener2)
        inOrder.verify(ruleListener1).beforeExecute(rule1, facts)
        inOrder.verify(ruleListener2).beforeExecute(rule1, facts)
        inOrder.verify(ruleListener1).onSuccess(rule1, facts)
        inOrder.verify(ruleListener2).onSuccess(rule1, facts)
    }

    @Test
    @Throws(Exception::class)
    fun whenTheRuleFails_thenOnFailureShouldBeExecuted() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        val exception = Exception("fatal error!")
        Mockito.doThrow(exception).`when`(rule1).execute(facts)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        val inOrder = Mockito.inOrder(rule1, fact1, fact2, ruleListener1, ruleListener2)
        inOrder.verify(ruleListener1).beforeExecute(rule1, facts)
        inOrder.verify(ruleListener2).beforeExecute(rule1, facts)
        inOrder.verify(ruleListener1).onFailure(rule1, facts, exception)
        inOrder.verify(ruleListener2).onFailure(rule1, facts, exception)
    }

    @Test
    fun whenListenerBeforeEvaluateReturnsFalse_thenTheRuleShouldBeSkippedBeforeBeingEvaluated() {
        // Given
        Mockito.`when`(ruleListener1.beforeEvaluate(rule1, facts)).thenReturn(false)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(rule1, Mockito.never()).evaluate(facts)
    }

    @Test
    fun whenListenerBeforeEvaluateReturnsTrue_thenTheRuleShouldBeEvaluated() {
        // Given
        Mockito.`when`(ruleListener1.beforeEvaluate(rule1, facts)).thenReturn(true)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(rule1).evaluate(facts)
    }

    @Test
    fun whenTheRuleEvaluatesToTrue_thenTheListenerShouldBeInvoked() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(ruleListener1).afterEvaluate(rule1, facts, true)
    }

    @Test
    fun whenTheRuleEvaluatesToFalse_thenTheListenerShouldBeInvoked() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(false)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(ruleListener1).afterEvaluate(rule1, facts, false)
    }
}