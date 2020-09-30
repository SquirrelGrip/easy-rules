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


/**
 * Basic rule implementation class that provides common methods.
 *
 * You can extend this class and override [BasicRule.evaluate] and [BasicRule.execute] to provide rule
 * conditions and actions logic.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
open class BasicRule(
        override val name: String = Rule.DEFAULT_NAME,
        override val description: String = Rule.DEFAULT_DESCRIPTION,
        override val priority: Int = Rule.DEFAULT_PRIORITY
) : Rule {
    /**
     * {@inheritDoc}
     */
    override fun evaluate(facts: Facts): Boolean {
        return false
    }

    /**
     * {@inheritDoc}
     */
    @Throws(Exception::class)
    override fun execute(facts: Facts) {
        // no op
    }

    /*
     * Rules are unique according to their names within a rules engine registry.
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val basicRule = o as BasicRule
        if (priority != basicRule.priority) return false
        return if (name != basicRule.name) false else description == basicRule.description
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + if (description != null) description.hashCode() else 0
        result = 31 * result + priority
        return result
    }

    override fun toString(): String {
        return name
    }

}
