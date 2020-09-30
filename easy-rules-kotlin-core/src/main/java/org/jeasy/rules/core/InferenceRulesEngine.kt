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

import org.jeasy.rules.api.*
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Inference [RulesEngine] implementation.
 *
 * Rules are selected based on given facts and fired according to their natural
 * order which is priority by default. This implementation continuously selects
 * and fires rules until no more rules are applicable.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class InferenceRulesEngine(
        parameters: RulesEngineParameters = RulesEngineParameters()
) : AbstractRulesEngine(parameters) {

    private val delegate: DefaultRulesEngine = DefaultRulesEngine(parameters)

    override fun fire(rules: Rules, facts: Facts) {
        var selectedRules: Set<Rule>
        do {
            LOGGER.debug("Selecting candidate rules based on the following facts: {}", facts)
            selectedRules = selectCandidates(rules, facts)
            if (selectedRules.isNotEmpty()) {
                delegate.fire(Rules(selectedRules), facts)
            } else {
                LOGGER.debug("No candidate rules found for facts: {}", facts)
            }
        } while (selectedRules.isNotEmpty())
    }

    private fun selectCandidates(rules: Rules, facts: Facts): Set<Rule> {
        val candidates: MutableSet<Rule> = TreeSet()
        for (rule in rules) {
            if (rule.evaluate(facts)) {
                candidates.add(rule)
            }
        }
        return candidates
    }

    override fun check(rules: Rules, facts: Facts): Map<Rule, Boolean> {
        return delegate.check(rules, facts)
    }

    /**
     * Register a rule listener.
     * @param ruleListener to register
     */
    override fun registerRuleListener(ruleListener: RuleListener) {
        super.registerRuleListener(ruleListener)
        delegate.registerRuleListener(ruleListener)
    }

    /**
     * Register a list of rule listener.
     * @param ruleListeners to register
     */
    override fun registerRuleListeners(ruleListeners: List<RuleListener>) {
        super.registerRuleListeners(ruleListeners)
        delegate.registerRuleListeners(ruleListeners)
    }

    /**
     * Register a rules engine listener.
     * @param rulesEngineListener to register
     */
    override fun registerRulesEngineListener(rulesEngineListener: RulesEngineListener) {
        super.registerRulesEngineListener(rulesEngineListener)
        delegate.registerRulesEngineListener(rulesEngineListener)
    }

    /**
     * Register a list of rules engine listener.
     * @param rulesEngineListeners to register
     */
    override fun registerRulesEngineListeners(rulesEngineListeners: List<RulesEngineListener>) {
        super.registerRulesEngineListeners(rulesEngineListeners)
        delegate.registerRulesEngineListeners(rulesEngineListeners)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(InferenceRulesEngine::class.java)
    }
    /**
     * Create a new inference rules engine.
     *
     * @param parameters of the engine
     */
}
