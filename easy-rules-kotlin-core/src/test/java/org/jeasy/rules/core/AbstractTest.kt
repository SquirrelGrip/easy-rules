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

import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rule
import org.jeasy.rules.api.Rules
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
abstract class AbstractTest {
    @Mock
    lateinit var rule1: Rule

    @Mock
    lateinit var rule2: Rule

    @Mock
    lateinit var fact1: Any

    @Mock
    lateinit var fact2: Any

    lateinit var facts: Facts
    lateinit var rules: Rules
    lateinit var rulesEngine: DefaultRulesEngine

    @Before
    open fun setup() {
        facts = Facts()
        facts.put("fact1", fact1)
        facts.put("fact2", fact2)
        rules = Rules()
        rulesEngine = DefaultRulesEngine()
    }

}

class DummyRule : Rule {
    override val name: String = "Dummy"
    override val description: String = "Dummy Rule"
    override val priority: Int = 3

    override fun evaluate(facts: Facts): Boolean {
        return true
    }

    override fun execute(facts: Facts) {
        isExecuted = true
    }

    private var isExecuted = false

    fun isExecuted(): Boolean {
        return isExecuted
    }

}

class WeatherRule : Rule {
    override val name: String = "Weather Rule"
    override val description: String = "Weather Rule"
    override val priority: Int = 1

    override fun evaluate(facts: Facts): Boolean {
        return facts["rain"] ?: false
    }

    override fun execute(facts: Facts) {
        println("It rains, take an umbrella!")
        isExecuted = true
    }

    private var isExecuted = false

    fun isExecuted(): Boolean {
        return isExecuted
    }
}

class AnotherDummyRule : Rule {
    private var isExecuted = false

    fun isExecuted(): Boolean {
        return isExecuted
    }

    override val name: String = "Another Dummy Rule"
    override val description: String = "Another Dummy Rule"
    override val priority: Int = 1

    override fun evaluate(facts: Facts): Boolean {
        return true
    }

    override fun execute(facts: Facts) {
        isExecuted = true
    }
}

class AgeRule : Rule {
    private var isExecuted = false

    fun isExecuted(): Boolean {
        return isExecuted
    }

    override val name: String = "Age Rule"
    override val description: String = "Age Rule"
    override val priority: Int = 1

    override fun evaluate(facts: Facts): Boolean {
        val age: Int = facts.getFact("age")?.value as Int
        return age >= 18
    }

    override fun execute(facts: Facts) {
        println("You are an adult")
        isExecuted = true
    }
}

