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

import org.assertj.core.api.Assertions
import org.jeasy.rules.api.RuleListener
import org.jeasy.rules.api.RulesEngineListener
import org.jeasy.rules.api.RulesEngineParameters
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class DefaultRulesEngineTest : AbstractTest() {
    @Mock
    lateinit var ruleListener: RuleListener
    @Mock
    lateinit var rulesEngineListener: RulesEngineListener

//    lateinit var annotatedRule: AnnotatedRule

    @Before
    @Throws(Exception::class)
    override fun setup() {
        super.setup()
        Mockito.`when`(rule1.name).thenReturn("r")
        Mockito.`when`(rule1.priority).thenReturn(1)
//        annotatedRule = AnnotatedRule()
    }

    @Test
    @Throws(Exception::class)
    fun whenConditionIsTrue_thenActionShouldBeExecuted() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(rule1).execute(facts)
    }

    @Test
    @Throws(Exception::class)
    fun whenConditionIsFalse_thenActionShouldNotBeExecuted() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(false)
        rules.register(rule1)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Mockito.verify(rule1, Mockito.never()).execute(facts)
    }

    @Test
    @Throws(Exception::class)
    fun rulesMustBeTriggeredInTheirNaturalOrder() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.compareTo(rule1)).thenReturn(1)
        rules.register(rule1)
        rules.register(rule2)

        // When
        rulesEngine.fire(rules, facts)

        // Then
        val inOrder = Mockito.inOrder(rule1, rule2)
        inOrder.verify(rule1).execute(facts)
        inOrder.verify(rule2).execute(facts)
    }

    @Test
    fun rulesMustBeCheckedInTheirNaturalOrder() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.evaluate(facts)).thenReturn(true)
        Mockito.`when`(rule2.compareTo(rule1)).thenReturn(1)
        rules.register(rule1)
        rules.register(rule2)

        // When
        rulesEngine.check(rules, facts)

        // Then
        val inOrder = Mockito.inOrder(rule1, rule2)
        inOrder.verify(rule1).evaluate(facts)
        inOrder.verify(rule2).evaluate(facts)
    }

//    @Test
//    fun actionsMustBeExecutedInTheDefinedOrder() {
//        // Given
//        rules.register(annotatedRule)
//
//        // When
//        rulesEngine.fire(rules, facts)
//
//        // Then
//        Assert.assertEquals("012", annotatedRule.getActionSequence())
//    }

//    @Test
//    @Throws(Exception::class)
//    fun annotatedRulesAndNonAnnotatedRulesShouldBeUsableTogether() {
//        // Given
//        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
//        rules.register(rule1)
//        rules.register(annotatedRule)
//
//        // When
//        rulesEngine.fire(rules, facts)
//
//        // Then
//        Mockito.verify(rule1).execute(facts)
//        Assertions.assertThat(annotatedRule.isExecuted()).isTrue
//    }

//    @Test
//    fun whenRuleNameIsNotSpecified_thenItShouldBeEqualToClassNameByDefault() {
//        val rule: Rule = RuleProxy.Companion.asRule(DummyRule())
//        Assertions.assertThat(rule.name).isEqualTo("DummyRule")
//    }
//
//    @Test
//    fun whenRuleDescriptionIsNotSpecified_thenItShouldBeEqualToConditionNameFollowedByActionsNames() {
//        val rule: Rule = RuleProxy.Companion.asRule(DummyRule())
//        Assertions.assertThat(rule.description).isEqualTo("when condition then action1,action2")
//    }

//    @Test
//    fun testCheckRules() {
//        // Given
//        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
//        rules.register(rule1)
//        rules.register(annotatedRule)
//
//        // When
//        val result = rulesEngine.check(rules, facts)
//
//        // Then
//        Assertions.assertThat(result).hasSize(2)
//        for (r in rules) {
//            Assertions.assertThat(result[r]).isTrue
//        }
//    }

    @Test
    fun listenerShouldBeInvokedBeforeCheckingRules() {
        // Given
        Mockito.`when`(rule1.evaluate(facts)).thenReturn(true)
        Mockito.`when`(ruleListener.beforeEvaluate(rule1, facts)).thenReturn(true)
        val rulesEngine = DefaultRulesEngine()
        rulesEngine.registerRuleListener(ruleListener)
        rules.register(rule1)

        // When
        rulesEngine.check(rules, facts)

        // Then
        Mockito.verify(ruleListener).beforeEvaluate(rule1, facts)
    }

    @Test
    fun getParametersShouldReturnACopyOfTheParameters() {
        // Given
        val parameters = RulesEngineParameters()
                .skipOnFirstAppliedRule(true)
                .skipOnFirstFailedRule(true)
                .skipOnFirstNonTriggeredRule(true)
                .priorityThreshold(42)
        val rulesEngine = DefaultRulesEngine(parameters)

        // When
        val engineParameters = rulesEngine.parameters

        // Then
        Assertions.assertThat(engineParameters).isEqualToComparingFieldByField(parameters)
    }

    @Test
    fun testGetRuleListeners() {
        // Given
        val rulesEngine = DefaultRulesEngine()
        rulesEngine.registerRuleListener(ruleListener)

        // When
        val ruleListeners = rulesEngine.ruleListeners

        // Then
        Assertions.assertThat(ruleListeners).contains(ruleListener)
    }

    @Test
    fun testGetRulesEngineListeners() {
        // Given
        val rulesEngine = DefaultRulesEngine()
        rulesEngine.registerRulesEngineListener(rulesEngineListener)

        // When
        val rulesEngineListeners = rulesEngine.rulesEngineListeners

        // Then
        Assertions.assertThat(rulesEngineListeners).contains(rulesEngineListener)
    }

    @After
    fun clearRules() {
        rules.clear()
    }

}