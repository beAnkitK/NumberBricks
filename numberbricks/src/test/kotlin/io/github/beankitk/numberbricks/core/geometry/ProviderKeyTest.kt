package io.github.beankitk.numberbricks.core.geometry

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ProviderKeyTest {

    private interface IntProviderKey : ProviderKey<Int> {
        override val family: IntProviderKey
            get() = Companion

        companion object : IntProviderKey
    }

    private interface StringProviderKey : ProviderKey<String> {
        override val family: StringProviderKey
            get() = Companion

        companion object : StringProviderKey
    }

    private object FirstIntProviderKey : IntProviderKey

    private object SecondIntProviderKey : IntProviderKey

    @Test
    fun testFamilyKeys_family_referencesItself() {
        assertSame(IntProviderKey, IntProviderKey.family)
        assertSame(StringProviderKey, StringProviderKey.family)
    }

    @Test
    fun testProviderFamilies_areIndependent() {
        assertNotSame<ProviderKey<*>>(IntProviderKey, StringProviderKey)
    }

    @Test
    fun testProviderKeys_family_referencesItsFamilyKey() {
        assertSame(IntProviderKey, FirstIntProviderKey.family)
    }

    @Test
    fun testProviderKeys_shareSameFamily() {
        assertSame(FirstIntProviderKey.family, SecondIntProviderKey.family)
        assertSame(IntProviderKey, FirstIntProviderKey.family)
        assertSame(IntProviderKey, SecondIntProviderKey.family)
    }

    @Test
    fun testProviderKeys_fromSameFamily_remainDistinct() {
        assertNotSame<IntProviderKey>(FirstIntProviderKey, SecondIntProviderKey)
    }

    // Family keys can currently be chained by overriding [family] to reference another derived key.
    // This is possible at the API level but is not supported by the actual geometry composition,
    // as DigitBuilder and ProviderScope only resolve root family keys.
    @Test
    fun testProviderKeys_canReferenceAnotherProviderKeyAsFamily() {
        val ChainedIntProviderKey = object : IntProviderKey {
            override val family: IntProviderKey
                get() = FirstIntProviderKey
        }

        assertSame(FirstIntProviderKey, ChainedIntProviderKey.family)
        assertNotSame<IntProviderKey>(IntProviderKey, ChainedIntProviderKey.family)
    }
}