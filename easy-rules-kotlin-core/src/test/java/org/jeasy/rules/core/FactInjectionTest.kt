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
import org.jeasy.rules.annotation.Action
import org.jeasy.rules.annotation.Condition
import org.jeasy.rules.annotation.Fact
import org.jeasy.rules.annotation.Rule
import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rules
import org.jeasy.rules.api.RulesEngine
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FactInjectionTest {
    @Test
    fun declaredFactsShouldBeCorrectlyInjectedByNameOrType() {
        // Given
        val fact1 = Any()
        val fact2 = Any()
        val facts = Facts()
        facts.put("fact1", fact1)
        facts.put("fact2", fact2)
        val rule = DummyRule()
        val rules = Rules(rule)

        // When
        val rulesEngine: RulesEngine = DefaultRulesEngine()
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(rule.fact1).isSameAs(fact1)
        Assertions.assertThat(rule.fact2).isSameAs(fact2)
        Assertions.assertThat(rule.facts).isSameAs(facts)
    }

    @Test
    fun rulesShouldBeExecutedWhenFactsAreCorrectlyInjected() {
        // Given
        val facts = Facts()
        facts.put("rain", true)
        facts.put("age", 18)
        val weatherRule = WeatherRule()
        val ageRule = AgeRule()
        val rules = Rules(weatherRule, ageRule)

        // When
        val rulesEngine: RulesEngine = DefaultRulesEngine()
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(ageRule.isExecuted()).isTrue
        Assertions.assertThat(weatherRule.isExecuted()).isTrue
    }

    @Test
    fun whenFactTypeDoesNotMatchParameterType_thenTheRuleShouldNotBeExecuted() {
        // Given
        val facts = Facts()
        facts.put("age", "foo")
        val ageRule = AgeRule()
        val rules = Rules(ageRule)
        val rulesEngine: RulesEngine = DefaultRulesEngine()

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(ageRule.isExecuted()).isFalse
    }

    @Test
    fun whenADeclaredFactIsMissingInEvaluateMethod_thenTheRuleShouldNotBeExecuted() {
        // Given
        val facts = Facts()
        val ageRule = AgeRule()
        val rules = Rules(ageRule)
        val rulesEngine: RulesEngine = DefaultRulesEngine()

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(ageRule.isExecuted()).isFalse
    }

    @Test
    fun whenADeclaredFactIsMissingInExecuteMethod_thenTheRuleShouldNotBeExecuted() {
        // Given
        val facts = Facts()
        val rule = AnotherDummyRule()
        val rules = Rules(rule)
        val rulesEngine: RulesEngine = DefaultRulesEngine()

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(rule.isExecuted()).isFalse
    }

    @Rule
    class DummyRule {
        lateinit var fact1: Any
        lateinit var fact2: Any
        lateinit var facts: Facts

        @Condition
        fun `when`(
                @Fact("fact1") fact1: Any,
                @Fact("fact2") fact2: Any
        ): Boolean {
            this.fact1 = fact1
            this.fact2 = fact2
            return true
        }

        @Action
        fun then(facts: Facts) {
            this.facts = facts
        }

    }

    @Rule
    internal class AnotherDummyRule {
        private var isExecuted = false
        @Condition
        fun `when`(): Boolean {
            return true
        }

        @Action
        fun then(@Fact("foo") fact: Any?) {
            isExecuted = true
        }

        fun isExecuted(): Boolean {
            return isExecuted
        }
    }

    @Rule
    internal class AgeRule {
        private var isExecuted = false
        @Condition
        fun isAdult(@Fact("age") age: Int): Boolean {
            return age >= 18
        }

        @Action
        fun printYourAreAdult() {
            println("You are an adult")
            isExecuted = true
        }

        fun isExecuted(): Boolean {
            return isExecuted
        }
    }

    @Rule
    internal class WeatherRule {
        private var isExecuted = false
        @Condition
        fun itRains(@Fact("rain") rain: Boolean): Boolean {
            return rain
        }

        @Action
        fun takeAnUmbrella(facts: Facts) {
            println("It rains, take an umbrella!")
            isExecuted = true
        }

        fun isExecuted(): Boolean {
            return isExecuted
        }
    }
}