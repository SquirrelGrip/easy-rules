package org.jeasy.rules.core
class AnnotationNotFoundException(targetAnnotation: Class<*>, annotatedType: Class<*>) : Exception("$annotatedType is not annotated with $targetAnnotation")
