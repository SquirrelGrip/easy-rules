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
 * Parameters of a rules engine.
 *
 *
 *  * When parameters are used with a [DefaultRulesEngine], they are applied on **all registered rules**.
 *  * When parameters are used with a [InferenceRulesEngine], they are applied on **candidate rules in each iteration**.
 *
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class RulesEngineParameters {
    /**
     * Parameter to skip next applicable rules when a rule is applied.
     */
    private var skipOnFirstAppliedRule = false

    /**
     * Parameter to skip next applicable rules when a rule is non triggered
     */
    private var skipOnFirstNonTriggeredRule = false

    /**
     * Parameter to skip next applicable rules when a rule has failed.
     */
    private var skipOnFirstFailedRule = false

    /**
     * Parameter to skip next rules if priority exceeds a user defined threshold.
     */
    private var priorityThreshold: Int

    /**
     * Create a new [RulesEngineParameters] with default values.
     */
    constructor() {
        priorityThreshold = DEFAULT_RULE_PRIORITY_THRESHOLD
    }

    /**
     * Create a new [RulesEngineParameters].
     *
     * @param skipOnFirstAppliedRule parameter to skip next applicable rules on first applied rule.
     * @param skipOnFirstFailedRule parameter to skip next applicable rules on first failed rule.
     * @param skipOnFirstNonTriggeredRule parameter to skip next applicable rules on first non triggered rule.
     * @param priorityThreshold threshold after which rules should be skipped.
     */
    constructor(skipOnFirstAppliedRule: Boolean, skipOnFirstFailedRule: Boolean, skipOnFirstNonTriggeredRule: Boolean, priorityThreshold: Int) {
        this.skipOnFirstAppliedRule = skipOnFirstAppliedRule
        this.skipOnFirstFailedRule = skipOnFirstFailedRule
        this.skipOnFirstNonTriggeredRule = skipOnFirstNonTriggeredRule
        this.priorityThreshold = priorityThreshold
    }

    fun getPriorityThreshold(): Int {
        return priorityThreshold
    }

    fun setPriorityThreshold(priorityThreshold: Int) {
        this.priorityThreshold = priorityThreshold
    }

    fun priorityThreshold(priorityThreshold: Int): RulesEngineParameters? {
        setPriorityThreshold(priorityThreshold)
        return this
    }

    fun isSkipOnFirstAppliedRule(): Boolean {
        return skipOnFirstAppliedRule
    }

    fun setSkipOnFirstAppliedRule(skipOnFirstAppliedRule: Boolean) {
        this.skipOnFirstAppliedRule = skipOnFirstAppliedRule
    }

    fun skipOnFirstAppliedRule(skipOnFirstAppliedRule: Boolean): RulesEngineParameters? {
        setSkipOnFirstAppliedRule(skipOnFirstAppliedRule)
        return this
    }

    fun isSkipOnFirstNonTriggeredRule(): Boolean {
        return skipOnFirstNonTriggeredRule
    }

    fun setSkipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule: Boolean) {
        this.skipOnFirstNonTriggeredRule = skipOnFirstNonTriggeredRule
    }

    fun skipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule: Boolean): RulesEngineParameters? {
        setSkipOnFirstNonTriggeredRule(skipOnFirstNonTriggeredRule)
        return this
    }

    fun isSkipOnFirstFailedRule(): Boolean {
        return skipOnFirstFailedRule
    }

    fun setSkipOnFirstFailedRule(skipOnFirstFailedRule: Boolean) {
        this.skipOnFirstFailedRule = skipOnFirstFailedRule
    }

    fun skipOnFirstFailedRule(skipOnFirstFailedRule: Boolean): RulesEngineParameters? {
        setSkipOnFirstFailedRule(skipOnFirstFailedRule)
        return this
    }

    override fun toString(): String {
        return "Engine parameters { " +
                "skipOnFirstAppliedRule = " + skipOnFirstAppliedRule +
                ", skipOnFirstNonTriggeredRule = " + skipOnFirstNonTriggeredRule +
                ", skipOnFirstFailedRule = " + skipOnFirstFailedRule +
                ", priorityThreshold = " + priorityThreshold +
                " }"
    }

    companion object {
        /**
         * Default rule priority threshold.
         */
        const val DEFAULT_RULE_PRIORITY_THRESHOLD = Int.MAX_VALUE
    }
}