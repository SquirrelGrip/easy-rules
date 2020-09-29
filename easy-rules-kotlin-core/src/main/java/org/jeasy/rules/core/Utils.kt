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

internal object Utils {
    @Throws(AnnotationNotFoundException::class)
    fun <A : Annotation> findAnnotation(targetAnnotation: Class<A>, annotatedType: Class<*>): A {
        var foundAnnotation = annotatedType.getAnnotation(targetAnnotation)
        if (foundAnnotation == null) {
            for (annotation in annotatedType.annotations) {
                val annotationType: Class<out Annotation> = annotation.javaClass
                if (annotationType.isAnnotationPresent(targetAnnotation)) {
                    return annotationType.getAnnotation(targetAnnotation)
                }
            }
            throw AnnotationNotFoundException(targetAnnotation, annotatedType)
        }
        return foundAnnotation
    }

    fun isAnnotationPresent(targetAnnotation: Class<out Annotation>, annotatedType: Class<*>): Boolean {
        return findAnnotation(targetAnnotation, annotatedType) != null
    }
}