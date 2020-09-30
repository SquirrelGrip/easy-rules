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

import java.lang.reflect.Method

/**
 * Utility class that associates an action method and its execution order.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
internal class ActionMethodOrderBean(
        val method: Method,
        val order: Int
) : Comparable<ActionMethodOrderBean> {

    override fun compareTo(actionMethodOrderBean: ActionMethodOrderBean): Int {
        return if (order < actionMethodOrderBean.order) {
            -1
        } else if (order > actionMethodOrderBean.order) {
            1
        } else {
            if (method == actionMethodOrderBean.method) 0 else 1
        }
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is ActionMethodOrderBean) return false
        return if (order != o.order) false else method == o.method
    }

    override fun hashCode(): Int {
        var result = method.hashCode()
        result = 31 * result + order
        return result
    }
}