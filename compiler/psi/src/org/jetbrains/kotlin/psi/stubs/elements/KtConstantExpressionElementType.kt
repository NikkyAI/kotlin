/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements

import com.intellij.psi.stubs.StubElement
import com.intellij.psi.stubs.StubInputStream
import com.intellij.psi.stubs.StubOutputStream
import com.intellij.util.io.StringRef
import org.jetbrains.annotations.NonNls
import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.psi.KtConstantExpression
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.stubs.ArgumentValueKind
import org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinValueArgumentStubImpl

class KtConstantExpressionElementType<T : KtValueArgument>(@NonNls debugName: String, psiClass: Class<T>) :
    KtStubElementType<KotlinValueArgumentStub<T>, T>(debugName, psiClass, KotlinValueArgumentStub::class.java) {

    override fun createStub(psi: T, parentStub: StubElement<*>?): KotlinValueArgumentStub<T> {
        val argumentValue = getArgumentValue(psi)

        return KotlinValueArgumentStubImpl(
            parentStub,
            argumentValue.kind,
            StringRef.fromString(argumentValue.value)
        )
    }

    override fun serialize(stub: KotlinValueArgumentStub<T>, dataStream: StubOutputStream) {
        dataStream.writeInt(stub.kind().ordinal)
        dataStream.writeName(stub.value())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinValueArgumentStub<T> {
        val kindOrdinal = dataStream.readInt()
        val value = dataStream.readName()
        return KotlinValueArgumentStubImpl(
            parentStub,
            ArgumentValueKind.values()[kindOrdinal],
            value
        )
    }

    companion object {
        private class ArgumentValue(val kind: ArgumentValueKind, val value: String?)

        private val NONE_ARGUMENT_VALUE = ArgumentValue(ArgumentValueKind.NONE, null)
        private val OTHER_ARGUMENT_VALUE = ArgumentValue(ArgumentValueKind.OTHER, null)
        private val NULL_ARGUMENT_VALUE = ArgumentValue(ArgumentValueKind.NULL, null)

        private fun getArgumentValue(argumentValue: KtValueArgument): ArgumentValue {
            val argumentExpression = argumentValue.getArgumentExpression() ?: return NONE_ARGUMENT_VALUE

            when (argumentExpression) {
                is KtStringTemplateExpression -> {
                    val singleEntry =
                        argumentExpression.entries.singleOrNull() as? KtLiteralStringTemplateEntry ?: return NONE_ARGUMENT_VALUE
                    return ArgumentValue(ArgumentValueKind.STRING_LITERAL, singleEntry.text)
                }

                is KtConstantExpression -> {
                    return when (argumentExpression.node.elementType) {
                        KtNodeTypes.NULL -> NULL_ARGUMENT_VALUE

                        KtNodeTypes.BOOLEAN_CONSTANT -> {
                            ArgumentValue(ArgumentValueKind.BOOLEAN_CONSTANT, argumentExpression.text)
                        }

                        KtNodeTypes.INTEGER_CONSTANT -> {
                            ArgumentValue(ArgumentValueKind.INTEGER_CONSTANT, argumentExpression.text)
                        }

                        KtNodeTypes.FLOAT_CONSTANT -> {
                            ArgumentValue(ArgumentValueKind.FLOAT_CONSTANT, argumentExpression.text)
                        }

                        KtNodeTypes.CHARACTER_CONSTANT -> {
                            ArgumentValue(ArgumentValueKind.CHARACTER_CONSTANT, argumentExpression.text)
                        }

                        else -> OTHER_ARGUMENT_VALUE
                    }

                }
            }

            return OTHER_ARGUMENT_VALUE
        }
    }
}