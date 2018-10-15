/*
 * Copyright 2010-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.stubs.ArgumentValueKind
import org.jetbrains.kotlin.psi.stubs.KotlinValueArgumentStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KotlinValueArgumentStubImpl<T : KtValueArgument>(
    parent: StubElement<out PsiElement>?,
    private val kind: ArgumentValueKind,
    private val value: StringRef?
) : KotlinStubBaseImpl<T>(parent, KtStubElementTypes.VALUE_ARGUMENT), KotlinValueArgumentStub<T> {
    override fun kind(): ArgumentValueKind = kind
    override fun value(): String? = StringRef.toString(value)
}