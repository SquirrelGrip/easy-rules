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

/**
 * This class encapsulates a set of rules and represents a rules namespace.
 * Rules must have a unique name within a rules namespace.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class Rules : Iterable<Rule> {
    private var rules: MutableSet<Rule> = sortedSetOf()

    /**
     * Create a new [Rules] object.
     *
     * @param rules to register
     */
    constructor(rules: Set<Rule>) {
        this.rules = sortedSetOf(*rules.toTypedArray())
    }

    /**
     * Create a new [Rules] object.
     *
     * @param rules to register
     */
    constructor(vararg rules: Rule) {
        register(*rules)
    }

    /**
     * Register one or more new rules.
     *
     * @param rules to register, must not be null
     */
    fun register(vararg rules: Rule) {
        for (rule in rules) {
            this.rules.add(rule)
        }
    }

    /**
     * Unregister one or more rules.
     *
     * @param rules to unregister, must not be null
     */
    fun unregister(vararg rules: Rule) {
        for (rule in rules) {
            this.rules.remove(rule)
        }
    }

    /**
     * Unregister a rule by name.
     *
     * @param ruleName name of the rule to unregister, must not be null
     */
    fun unregister(ruleName: String) {
        Objects.requireNonNull(ruleName)
        val rule = findRuleByName(ruleName)
        rule?.let { unregister(it) }
    }

    /**
     * Check if the rule set is empty.
     *
     * @return true if the rule set is empty, false otherwise
     */
    fun isEmpty(): Boolean {
        return rules.isEmpty()
    }

    /**
     * Clear rules.
     */
    fun clear() {
        rules.clear()
    }

    /**
     * Return how many rules are currently registered.
     *
     * @return the number of rules currently registered
     */
    fun size(): Int {
        return rules.size
    }

    /**
     * Return an iterator on the rules set. It is not intended to remove rules
     * using this iterator.
     * @return an iterator on the rules set
     */
    override fun iterator(): MutableIterator<Rule> {
        return rules.iterator()
    }

    private fun findRuleByName(ruleName: String): Rule? {
        return rules
                .filter { rule: Rule -> rule.name.equals(ruleName, ignoreCase = true) }
                .firstOrNull()
    }
}