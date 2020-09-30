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

import org.jeasy.rules.annotation.*
import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.Rule.Companion.DEFAULT_NAME
import org.slf4j.LoggerFactory
import java.io.InvalidClassException
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Main class to create rule proxies from annotated objects.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
class RuleProxy private constructor(
        val target: Any
) : InvocationHandler {

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any?>): Any? {
        return when (method.name) {
            "getName" -> getRuleName()
            "getDescription" -> getRuleDescription()
            "getPriority" -> getRulePriority()
            "compareTo" -> compareToMethod(args)
            "evaluate" -> evaluateMethod(args)
            "execute" -> executeMethod(args)
            "equals" -> equalsMethod(args)
            "hashCode" -> hashCodeMethod()
            "toString" -> toStringMethod()
            else -> null
        }
    }

    @Throws(IllegalAccessException::class, InvocationTargetException::class)
    private fun evaluateMethod(args: Array<Any?>): Any? {
        val facts = args[0] as Facts
        val conditionMethod = getConditionMethod()
        return try {
            val actualParameters = getActualParameters(conditionMethod, facts)
            conditionMethod.invoke(target, *actualParameters.toTypedArray()) // validated upfront
        } catch (e: NoSuchFactException) {
            LOGGER.warn("Rule '{}' has been evaluated to false due to a declared but missing fact '{}' in {}",
                    getTargetClass().name, e.missingFact, facts)
            false
        } catch (e: IllegalArgumentException) {
            LOGGER.warn("Types of injected facts in method '{}' in rule '{}' do not match parameters types",
                    conditionMethod.getName(), getTargetClass().name, e)
            false
        }
    }

    @Throws(IllegalAccessException::class, InvocationTargetException::class)
    private fun executeMethod(args: Array<Any?>): Any? {
        val facts = args[0] as Facts
        for (actionMethodBean in getActionMethodBeans()) {
            val actionMethod = actionMethodBean.method
            val actualParameters = getActualParameters(actionMethod, facts)
            actionMethod.invoke(target, *actualParameters.toTypedArray())
        }
        return null
    }

    @Throws(Exception::class)
    private fun compareToMethod(args: Array<Any?>): Any? {
        val compareToMethod = getCompareToMethod()
        val otherRule = args[0] // validated upfront
        return if (compareToMethod != null && otherRule != null && Proxy.isProxyClass(otherRule.javaClass)) {
            require(compareToMethod.parameters.size == 1) { "compareTo method must have a single argument" }
            val ruleProxy = Proxy.getInvocationHandler(otherRule) as RuleProxy
            compareToMethod.invoke(target, ruleProxy.target)
        } else {
            compareTo(otherRule as org.jeasy.rules.api.Rule)
        }
    }

    private fun getActualParameters(method: Method, facts: Facts): List<Any> {
        return method.parameterAnnotations.map {
            if (it.size == 1) {
                val factName: String = (it[0] as Fact).value
                val fact = facts.get<Any>(factName) ?: throw NoSuchFactException("No fact named '$factName' found in known facts: %n$facts", factName)
                fact
            } else {
                facts //validated upfront, there may be only one parameter not annotated and which is of type Facts.class
            }
        }
     }

    @Throws(Exception::class)
    private fun equalsMethod(args: Array<Any?>): Boolean {
        if (args[0] !is org.jeasy.rules.api.Rule) {
            return false
        }
        val otherRule = args[0] as org.jeasy.rules.api.Rule
        val otherPriority = otherRule.priority
        val priority = getRulePriority()
        if (priority != otherPriority) {
            return false
        }
        val otherName = otherRule.name
        val name = getRuleName()
        if (name != otherName) {
            return false
        }
        val otherDescription = otherRule.description
        val description = getRuleDescription()
        return description == otherDescription
    }

    @Throws(Exception::class)
    private fun hashCodeMethod(): Int {
        var result = getRuleName().hashCode()
        val priority = getRulePriority()
        val description = getRuleDescription()
        result = 31 * result + (description.hashCode() ?: 0)
        result = 31 * result + priority
        return result
    }

    private fun getToStringMethod(): Method? {
        val methods = getMethods()
        for (method in methods) {
            if ("toString" == method.name) {
                return method
            }
        }
        return null
    }

    @Throws(Exception::class)
    private fun toStringMethod(): String {
        val toStringMethod = getToStringMethod()
        return if (toStringMethod != null) {
            toStringMethod.invoke(target) as String
        } else {
            getRuleName()
        }
    }

    @Throws(Exception::class)
    private operator fun compareTo(otherRule: org.jeasy.rules.api.Rule): Int {
        val otherPriority = otherRule.priority
        val priority = getRulePriority()
        return when {
            priority < otherPriority -> -1
            priority > otherPriority -> 1
            else -> {
                val otherName = otherRule.name
                val name = getRuleName()
                name.compareTo(otherName)
            }
        }
    }

    @Throws(Exception::class)
    private fun getRulePriority(): Int {
        var priority: Int = org.jeasy.rules.api.Rule.DEFAULT_PRIORITY
        val rule = getRuleAnnotation()
        if (rule.priority != org.jeasy.rules.api.Rule.DEFAULT_PRIORITY) {
            priority = rule.priority
        }
        val methods = getMethods()
        for (method in methods) {
            if (method.isAnnotationPresent(Priority::class.java)) {
                return method.invoke(target) as Int
            }
        }
        return priority
    }

    private fun getConditionMethod() = getMethods().firstOrNull {
        it.isAnnotationPresent(Condition::class.java)
    } ?: throw AnnotationNotFoundException(Condition::class.java, target::class.java)

    private fun getActionMethodBeans() = getMethods()
            .filter {
                it.isAnnotationPresent(Action::class.java)
            }
            .map {
                val actionAnnotation = it.getAnnotation(Action::class.java)
                val order: Int = actionAnnotation.order
                ActionMethodOrderBean(it, order)
            }

    private fun getCompareToMethod() = getMethods().firstOrNull {
        it.name == "compareTo"
    }

    private fun getMethods() = getTargetClass().methods

    private fun getRuleAnnotation() = Utils.findAnnotation(Rule::class.java, getTargetClass())

    private fun getRuleName(): String {
        val rule = getRuleAnnotation()
        return if (rule.name == DEFAULT_NAME) getTargetClass().simpleName else rule.name
    }

    private fun getRuleDescription(): String {
        // Default description = "when " + conditionMethodName + " then " + comma separated actionMethodsNames
        val description = StringBuilder()
        appendConditionMethodName(description)
        appendActionMethodsNames(description)
        val rule = getRuleAnnotation()
        return if (rule.description == org.jeasy.rules.api.Rule.DEFAULT_DESCRIPTION) description.toString() else rule.description
    }

    private fun appendConditionMethodName(description: StringBuilder) {
        val method = getConditionMethod()
        if (method != null) {
            description.append("when ")
            description.append(method.name)
            description.append(" then ")
        }
    }

    private fun appendActionMethodsNames(description: StringBuilder) {
        val iterator = getActionMethodBeans().iterator()
        while (iterator.hasNext()) {
            description.append(iterator.next().method.name)
            if (iterator.hasNext()) {
                description.append(",")
            }
        }
    }

    private fun getTargetClass(): Class<*> {
        return target.javaClass
    }

    companion object {
        private val ruleDefinitionValidator: RuleDefinitionValidator = RuleDefinitionValidator()
        private val LOGGER = LoggerFactory.getLogger(RuleProxy::class.java)

        /**
         * Makes the rule object implement the [Rule] interface.
         *
         * @param rule the annotated rule object.
         * @return a proxy that implements the [Rule] interface.
         */
        fun asRule(rule: Any): org.jeasy.rules.api.Rule {
            return if (rule is org.jeasy.rules.api.Rule) {
                rule
            } else {
                ruleDefinitionValidator.validateRuleDefinition(rule)
                Proxy.newProxyInstance(
                        org.jeasy.rules.api.Rule::class.java.classLoader, arrayOf<Class<*>?>(org.jeasy.rules.api.Rule::class.java, Comparable::class.java),
                        RuleProxy(rule)) as org.jeasy.rules.api.Rule
            }
        }
    }
}