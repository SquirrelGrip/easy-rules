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
import org.jeasy.rules.annotation.*
import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rules
import org.jeasy.rules.api.RulesEngine
import org.jeasy.rules.api.RulesEngineListener
import org.junit.Test

class InferenceRulesEngineTest {
    @Test
    fun testCandidateSelection() {
        // Given
        val facts = Facts()
        facts.put("foo", true)
        val dummyRule = DummyRule()
        val anotherDummyRule = AnotherDummyRule()
        val rules = Rules(dummyRule, anotherDummyRule)
        val rulesEngine: RulesEngine = InferenceRulesEngine()

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(dummyRule.isExecuted()).isTrue
        Assertions.assertThat(anotherDummyRule.isExecuted()).isFalse
    }

    @Test
    fun testCandidateOrdering() {
        // Given
        val facts = Facts()
        facts.put("foo", true)
        facts.put("bar", true)
        val dummyRule = DummyRule()
        val anotherDummyRule = AnotherDummyRule()
        val rules = Rules(dummyRule, anotherDummyRule)
        val rulesEngine: RulesEngine = InferenceRulesEngine()

        // When
        rulesEngine.fire(rules, facts)

        // Then
        Assertions.assertThat(dummyRule.isExecuted()).isTrue
        Assertions.assertThat(anotherDummyRule.isExecuted()).isTrue
        Assertions.assertThat(dummyRule.getTimestamp()).isLessThanOrEqualTo(anotherDummyRule.getTimestamp())
    }

    @Test
    fun testRulesEngineListener() {
        // Given
        internal class StubRulesEngineListener : RulesEngineListener {
            private var executedBeforeEvaluate = false
            private var executedAfterExecute = false
            override fun beforeEvaluate(rules: Rules?, facts: Facts?) {
                executedBeforeEvaluate = true
            }

            override fun afterExecute(rules: Rules?, facts: Facts?) {
                executedAfterExecute = true
            }

            private fun isExecutedBeforeEvaluate(): Boolean {
                return executedBeforeEvaluate
            }

            private fun isExecutedAfterExecute(): Boolean {
                return executedAfterExecute
            }
        }

        val facts = Facts()
        facts.put("foo", true)
        val rule = DummyRule()
        val rules = Rules(rule)
        val rulesEngineListener = StubRulesEngineListener()

        // When
        val rulesEngine = InferenceRulesEngine()
        rulesEngine.registerRulesEngineListener(rulesEngineListener)
        rulesEngine.fire(rules, facts)

        // Then
        // Rules engine listener should be invoked
        Assertions.assertThat(rulesEngineListener.isExecutedBeforeEvaluate()).isTrue
        Assertions.assertThat(rulesEngineListener.isExecutedAfterExecute()).isTrue
        Assertions.assertThat(rule.isExecuted()).isTrue
    }

    @Rule
    internal class DummyRule {
        private var isExecuted = false
        private var timestamp: Long = 0
        @Condition
        fun `when`(@Fact("foo") foo: Boolean): Boolean {
            return foo
        }

        @Action
        fun then(facts: Facts?) {
            isExecuted = true
            timestamp = System.currentTimeMillis()
            facts.remove("foo")
        }

        @Priority
        fun priority(): Int {
            return 1
        }

        fun isExecuted(): Boolean {
            return isExecuted
        }

        fun getTimestamp(): Long {
            return timestamp
        }
    }

    @Rule
    internal class AnotherDummyRule {
        private var isExecuted = false
        private var timestamp: Long = 0
        @Condition
        fun `when`(@Fact("bar") bar: Boolean): Boolean {
            return bar
        }

        @Action
        fun then(facts: Facts?) {
            isExecuted = true
            timestamp = System.currentTimeMillis()
            facts.remove("bar")
        }

        @Priority
        fun priority(): Int {
            return 2
        }

        fun isExecuted(): Boolean {
            return isExecuted
        }

        fun getTimestamp(): Long {
            return timestamp
        }
    }
}