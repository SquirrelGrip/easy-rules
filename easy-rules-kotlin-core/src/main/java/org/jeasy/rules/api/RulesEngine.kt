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

/**
 * Rules engine interface.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
interface RulesEngine {
    /**
     * Return the rules engine parameters.
     *
     * @return The rules engine parameters
     */
    fun getParameters(): RulesEngineParameters

    /**
     * Return the list of registered rule listeners.
     *
     * @return the list of registered rule listeners
     */
    fun getRuleListeners(): List<RuleListener> {
        return listOf()
    }

    /**
     * Return the list of registered rules engine listeners.
     *
     * @return the list of registered rules engine listeners
     */
    fun getRulesEngineListeners(): List<RulesEngineListener> {
        return listOf()
    }

    /**
     * Fire all registered rules on given facts.
     */
    fun fire(rules: Rules, facts: Facts)

    /**
     * Check rules without firing them.
     * @return a map with the result of evaluation of each rule
     */
    fun check(rules: Rules, facts: Facts): Map<Rule, Boolean> {
        return mapOf()
    }
}