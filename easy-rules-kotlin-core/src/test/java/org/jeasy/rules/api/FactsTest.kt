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
package org.jeasy.rules.api

import org.assertj.core.api.Assertions
import org.junit.Test

class FactsTest {
    private val facts: Facts = Facts()
    @Test
    fun factsMustHaveUniqueName() {
        facts.add(Fact("foo", 1))
        facts.add(Fact("foo", 2))
        Assertions.assertThat(facts).hasSize(1)
        val fact = facts.getFact("foo")
        Assertions.assertThat(fact?.value).isEqualTo(2)
    }

    @Test
    fun testAdd() {
        val fact1 = Fact("foo", 1)
        val fact2 = Fact("bar", 2)
        facts.add(fact1)
        facts.add(fact2)
        Assertions.assertThat(facts).contains(fact1)
        Assertions.assertThat(facts).contains(fact2)
    }

    @Test
    fun testPut() {
        facts.put("foo", 1)
        facts.put("bar", 2)
        Assertions.assertThat(facts).contains(Fact("foo", 1))
        Assertions.assertThat(facts).contains(Fact("bar", 2))
    }

    @Test
    fun testRemove() {
        val foo = Fact("foo", 1)
        facts.add(foo)
        facts.remove(foo)
        Assertions.assertThat(facts).isEmpty()
    }

    @Test
    fun testRemoveByName() {
        val foo = Fact("foo", 1)
        facts.add(foo)
        facts.remove("foo")
        Assertions.assertThat(facts).isEmpty()
    }

    @Test
    fun testGet() {
        val fact = Fact("foo", 1)
        facts.add(fact)
        val value = facts.get<Int?>("foo")
        Assertions.assertThat(value).isEqualTo(1)
    }

    @Test
    fun testGetFact() {
        val fact = Fact("foo", 1)
        facts.add(fact)
        val retrievedFact = facts.getFact("foo")
        Assertions.assertThat(retrievedFact).isEqualTo(fact)
    }

    @Test
    fun testAsMap() {
        val fact1 = Fact("foo", 1)
        val fact2 = Fact("bar", 2)
        facts.add(fact1)
        facts.add(fact2)
        val map = facts.asMap()
        Assertions.assertThat(map).containsKeys("foo", "bar")
        Assertions.assertThat(map).containsValues(1, 2)
    }

    @Test
    fun testClear() {
        val facts = Facts()
        facts.add(Fact("foo", 1))
        facts.clear()
        Assertions.assertThat(facts).isEmpty()
    }
}