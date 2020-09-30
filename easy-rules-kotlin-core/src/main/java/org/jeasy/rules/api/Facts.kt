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

import java.util.*
import kotlin.Comparator

/**
 * This class encapsulates a set of facts and represents a facts namespace.
 * Facts have unique names within a `Facts` object.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
open class Facts : Iterable<Fact<*>> {
    private val facts: MutableSet<Fact<*>> = sortedSetOf()

    /**
     * Add a fact, replacing any fact with the same name.
     *
     * @param name of the fact to add, must not be null
     * @param value of the fact to add, must not be null
     */
    fun put(name: String, value: Any) {
        add(Fact(name, value))
    }

    /**
     * Add a fact, replacing any fact with the same name.
     *
     * @param fact to add, must not be null
     */
    fun <T> add(fact: Fact<T>) {
        val retrievedFact = getFact(fact.name)
        retrievedFact?.let { remove(it) }
        facts.add(fact)
    }

    /**
     * Remove a fact by name.
     *
     * @param factName name of the fact to remove, must not be null
     */
    fun remove(factName: String) {
        val fact = getFact(factName)
        fact?.let { remove(it) }
    }

    /**
     * Remove a fact.
     *
     * @param fact to remove, must not be null
     */
    fun <T> remove(fact: Fact<T>) {
        facts.remove(fact)
    }

    /**
     * Get the value of a fact by its name. This is a convenience method provided
     * as a short version of `getFact(factName).getValue()`.
     *
     * @param factName name of the fact, must not be null
     * @param <T> type of the fact's value
     * @return the value of the fact having the given name, or null if there is
     * no fact with the given name
    </T> */
    operator fun <T> get(factName: String): T? {
        val fact = getFact(factName)
        return if (fact != null) {
            fact.value as T
        } else null
    }

    /**
     * Get a fact by name.
     *
     * @param factName name of the fact, must not be null
     * @return the fact having the given name, or null if there is no fact with the given name
     */
    fun getFact(factName: String): Fact<*>? = facts
            .firstOrNull {
                fact: Fact<*> -> fact.name == factName
            }

    /**
     * Return a copy of the facts as a map. It is not intended to manipulate
     * facts outside of the rules engine (aka other than manipulating them through rules).
     *
     * @return a copy of the current facts as a [Map]
     */
    fun asMap(): Map<String, Any?> {
        return facts.map {
            it.name to it.value
        }.toMap()
    }

    /**
     * Return an iterator on the set of facts. It is not intended to remove
     * facts using this iterator outside of the rules engine (aka other than doing it through rules)
     *
     * @return an iterator on the set of facts
     */
    override fun iterator(): MutableIterator<Fact<*>> {
        return facts.iterator()
    }

    /**
     * Clear facts.
     */
    fun clear() {
        facts.clear()
    }

    override fun toString(): String {
        val iterator = facts.iterator()
        val stringBuilder = StringBuilder("[")
        while (iterator.hasNext()) {
            stringBuilder.append(iterator.next().toString())
            if (iterator.hasNext()) {
                stringBuilder.append(",")
            }
        }
        stringBuilder.append("]")
        return stringBuilder.toString()
    }
}