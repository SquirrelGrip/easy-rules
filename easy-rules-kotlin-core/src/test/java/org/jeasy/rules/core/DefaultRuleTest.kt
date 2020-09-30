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

import org.jeasy.rules.api.Action
import org.jeasy.rules.api.Condition
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class DefaultRuleTest : AbstractTest() {
    @Mock
    lateinit var condition: Condition

    @Mock
    lateinit var action1: Action

    @Mock
    lateinit var action2: Action

    @Test
    @Throws(Exception::class)
    fun WhenConditionIsTrue_ThenActionsShouldBeExecutedInOrder() {
        // given
        Mockito.`when`(condition.evaluate(facts)).thenReturn(true)
        val rule = RuleBuilder()
                .`when`(condition)
                .then(action1)
                .then(action2)
                .build()
        rules.register(rule)

        // when
        rulesEngine.fire(rules, facts)

        // then
        val inOrder = Mockito.inOrder(action1, action2)
        inOrder.verify(action1).execute(facts)
        inOrder.verify(action2).execute(facts)
    }

    @Test
    @Throws(Exception::class)
    fun WhenConditionIsFalse_ThenActionsShouldNotBeExecuted() {
        // given
        Mockito.`when`(condition.evaluate(facts)).thenReturn(false)
        val rule = RuleBuilder()
                .`when`(condition)
                .then(action1)
                .then(action2)
                .build()
        rules.register(rule)

        // when
        rulesEngine.fire(rules, facts)

        // then
        Mockito.verify(action1, Mockito.never()).execute(facts)
        Mockito.verify(action2, Mockito.never()).execute(facts)
    }
}