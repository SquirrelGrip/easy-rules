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

import org.assertj.core.api.Assertions
import org.jeasy.rules.annotation.*
import org.junit.Before
import org.junit.Test

class RuleDefinitionValidatorTest {
    private var ruleDefinitionValidator: RuleDefinitionValidator? = null
    @Before
    fun setup() {
        ruleDefinitionValidator = RuleDefinitionValidator()
    }

    /*
     * Rule annotation test
     */
    @Test(expected = IllegalArgumentException::class)
    fun notAnnotatedRuleMustNotBeAccepted() {
        ruleDefinitionValidator.validateRuleDefinition(Any())
    }

    @Test
    fun withCustomAnnotationThatIsItselfAnnotatedWithTheRuleAnnotation() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithMetaRuleAnnotation())
    }

    /*
     * Conditions methods tests
     */
    @Test(expected = IllegalArgumentException::class)
    fun conditionMethodMustBeDefined() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithoutConditionMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun conditionMethodMustBePublic() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithNonPublicConditionMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenConditionMethodHasOneNonAnnotatedParameter_thenThisParameterMustBeOfTypeFacts() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithConditionMethodHavingOneArgumentNotOfTypeFacts())
    }

    @Test(expected = IllegalArgumentException::class)
    fun conditionMethodMustReturnBooleanType() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithConditionMethodHavingNonBooleanReturnType())
    }

    @Test(expected = IllegalArgumentException::class)
    fun conditionMethodParametersShouldAllBeAnnotatedWithFactUnlessExactlyOneOfThemIsOfTypeFacts() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithOneParameterNotAnnotatedWithFactAndNotOfTypeFacts())
    }

    /*
     * Action method tests
     */
    @Test(expected = IllegalArgumentException::class)
    fun actionMethodMustBeDefined() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithoutActionMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun actionMethodMustBePublic() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithNonPublicActionMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun actionMethodMustHaveAtMostOneArgumentOfTypeFacts() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithActionMethodHavingOneArgumentNotOfTypeFacts())
    }

    @Test(expected = IllegalArgumentException::class)
    fun actionMethodMustHaveExactlyOneArgumentOfTypeFactsIfAny() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithActionMethodHavingMoreThanOneArgumentOfTypeFacts())
    }

    @Test(expected = IllegalArgumentException::class)
    fun actionMethodMustReturnVoid() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithActionMethodThatReturnsNonVoidType())
    }

    @Test(expected = IllegalArgumentException::class)
    fun actionMethodParametersShouldAllBeAnnotatedWithFactUnlessExactlyOneOfThemIsOfTypeFacts() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithOneParameterNotAnnotatedWithFactAndNotOfTypeFacts())
    }

    /*
     * Priority method tests
     */
    @Test(expected = IllegalArgumentException::class)
    fun priorityMethodMustBeUnique() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithMoreThanOnePriorityMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun priorityMethodMustBePublic() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithNonPublicPriorityMethod())
    }

    @Test(expected = IllegalArgumentException::class)
    fun priorityMethodMustHaveNoArguments() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithPriorityMethodHavingArguments())
    }

    @Test(expected = IllegalArgumentException::class)
    fun priorityMethodReturnTypeMustBeInteger() {
        ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithPriorityMethodHavingNonIntegerReturnType())
    }

    /*
     * Valid definition tests
     */
    @Test
    fun validAnnotationsShouldBeAccepted() {
        try {
            ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfTypeFacts())
            ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithMultipleAnnotatedParametersAndOneParameterOfSubTypeFacts())
            ruleDefinitionValidator.validateRuleDefinition(AnnotatedRuleWithActionMethodHavingOneArgumentOfTypeFacts())
        } catch (throwable: Throwable) {
            Assertions.fail<Any?>("Should not throw exception for valid rule definitions")
        }
    }
}