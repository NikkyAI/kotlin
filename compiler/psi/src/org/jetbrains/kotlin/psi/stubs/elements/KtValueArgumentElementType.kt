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
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.stubs.ArgumentValueKind
import org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub
import org.jetbrains.kotlin.psi.stubs.impl.KotlinValueArgumentStubImpl

class KtValueArgumentElementType<T : KtValueArgument>(@NonNls debugName: String, psiClass: Class<T>) :
    KtStubElementType<KotlinValueArgumentStub<T>, T>(debugName, psiClass, KotlinValueArgumentStub::class.java) {

    override fun createStub(psi: T, parentStub: StubElement<*>?): KotlinValueArgumentStub<T> {
        val argumentName: Name? = psi.getArgumentName()?.asName
        val argumentNameStr = if (argumentName != null && !argumentName.isSpecial) {
            argumentName.identifier
        } else {
            null
        }

        return KotlinValueArgumentStubImpl(
            parentStub,
            StringRef.fromString(argumentNameStr),
            ArgumentValueKind.OTHER,
            null
        )
    }

    override fun serialize(stub: KotlinValueArgumentStub<T>, dataStream: StubOutputStream) {
        dataStream.writeName(stub.getName()?.identifier)
        dataStream.writeInt(stub.kind().ordinal)
        dataStream.writeName(stub.value())
    }

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): KotlinValueArgumentStub<T> {
        val argumentName: StringRef? = dataStream.readName()
        val kindInt = dataStream.readInt()
        val value = dataStream.readName()
        return KotlinValueArgumentStubImpl(
            parentStub,
            argumentName,
            ArgumentValueKind.values()[kindInt],
            value
        )
    }
}