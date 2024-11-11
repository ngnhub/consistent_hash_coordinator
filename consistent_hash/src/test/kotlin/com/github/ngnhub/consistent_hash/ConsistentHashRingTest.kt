package com.github.ngnhub.consistent_hash

import com.github.ngnhub.consistent_hash.exception.CollisionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConsistentHashRingTest {

    @ParameterizedTest
    @MethodSource("keys")
    fun `should return closest values by provided keys`(key: BigInteger, expectedValue: String) {
        // given
        val consistentHashMap = prepareMap()

        // when
        val actualValue = consistentHashMap[key]

        // then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `get should return first value if it is only value`() {
        // given
        ConsistentHashRing<String>()
        val consistentHashRing = ConsistentHashRing<String>()
        val expectedValue = "value1"
        consistentHashRing[BigInteger.valueOf(1L)] = expectedValue
        val key = BigInteger.ZERO

        // when
        val actualValue = consistentHashRing[key]

        // then
        assertEquals(expectedValue, actualValue)
    }

    private fun prepareMap(): ConsistentHashRing<String> {
        val consistentHashRing = ConsistentHashRing<String>()
        for (i in 1..3) {
            val valueOf = BigInteger.valueOf(i + 2L)
            consistentHashRing[valueOf] = "value$valueOf"
        }
        return consistentHashRing
    }

    @Test
    fun `should throw when collision occurred`() {
        // given
        ConsistentHashRing<String>()
        val consistentHashRing = ConsistentHashRing<String>()
        consistentHashRing[BigInteger.ZERO] = "value1"

        // when
        val exc = assertThrows<CollisionException> { consistentHashRing[BigInteger.ZERO] = "value2" }

        // then
        assertEquals("Collision has occurred for key 0. The key must be modified", exc.message)
    }

    @Test
    fun `get should return null if map is empty`() {
        // given
        ConsistentHashRing<String>()
        val consistentHashRing = ConsistentHashRing<String>()

        // when
        val actual = consistentHashRing[BigInteger.ZERO]

        // then
        assertNull(actual)
    }

    @ParameterizedTest
    @MethodSource("keysNextAfter")
    fun `should return next values after provided keys`(key: BigInteger, expectedValue: String) {
        // given
        ConsistentHashRing<String>()
        val consistentHashMap = prepareMap()

        // when
        val actualValue = consistentHashMap.nextAfter(key)

        // then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `next after should return null if map is empty`() {
        // given
        ConsistentHashRing<String>()
        val consistentHashRing = ConsistentHashRing<String>()

        // when
        val actual = consistentHashRing.nextAfter(BigInteger.ZERO)

        // then
        assertNull(actual)
    }

    @Test
    fun `next after should return null if it is a single value in a map`() {
        // given
        ConsistentHashRing<String>()
        val consistentHashRing = ConsistentHashRing<String>()
        consistentHashRing[BigInteger.ZERO] = "value"

        // when
        val actual = consistentHashRing.nextAfter(BigInteger.ZERO)

        // then
        assertNull(actual)
    }

    @Test
    fun `should return valid internal map size`() {
        // given
        ConsistentHashRing<String>()

        // when
        val filledNap = prepareMap()
        val emptyMap = ConsistentHashRing<String>()

        // then
        assertEquals(0, emptyMap.size)
        assertEquals(3, filledNap.size)
    }

    @Test
    fun `should remove value`() {
        // given
        ConsistentHashRing<String>()
        val key = BigInteger.valueOf(1L)
        val value = "value"
        val consistentHashRing = ConsistentHashRing<String>()
        consistentHashRing[key] = value
        assertEquals(1, consistentHashRing.size)
        assertEquals(value, consistentHashRing[key])

        // when
        consistentHashRing - key

        // then
        assertEquals(0, consistentHashRing.size)
        assertNull(consistentHashRing[key])
    }

    companion object {
        @JvmStatic
        fun keys() = listOf(
            Arguments.of(BigInteger.ZERO, "value3"),
            Arguments.of(BigInteger.valueOf(4), "value4"),
            Arguments.of(BigInteger.valueOf(5), "value5"),
            Arguments.of(BigInteger.valueOf(6), "value3"),
            Arguments.of(BigInteger.valueOf(8), "value3"),
        )

        @JvmStatic
        fun keysNextAfter() = listOf(
            Arguments.of(BigInteger.ZERO, "value3"),
            Arguments.of(BigInteger.valueOf(4), "value5"),
            Arguments.of(BigInteger.valueOf(5), "value3"),
            Arguments.of(BigInteger.valueOf(8), "value3"),
        )
    }
}
