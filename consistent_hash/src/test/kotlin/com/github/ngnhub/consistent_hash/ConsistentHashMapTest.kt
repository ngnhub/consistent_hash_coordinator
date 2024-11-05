package com.github.ngnhub.consistent_hash

import com.github.ngnhub.consistent_hash.exception.CollisionException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConsistentHashMapTest {

    @ParameterizedTest
    @MethodSource("keys")
    fun `should return closest values by provided keys`(mockedKeyHash: BigInteger, expectedValue: String) {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash("node1") } returns BigInteger.valueOf(1)
        every { mockedHashFunction.hash("node2") } returns BigInteger.valueOf(5)
        every { mockedHashFunction.hash("node3") } returns BigInteger.valueOf(7)
        val consistentHashMap = prepareMap(mockedHashFunction)
        val key = "key" /* key's value doesn't matter. its hash will be mocked anyway*/
        every { mockedHashFunction.hash(key) } returns mockedKeyHash

        // when
        val actualValue = consistentHashMap[key]

        // then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `get should return first value if it is only value`() {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash("node1") } returns BigInteger.valueOf(1)
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)
        val expectedValue = "value1"
        consistentHashMap["node1"] = expectedValue
        val key = "key"
        every { mockedHashFunction.hash(key) } returns BigInteger.valueOf(2)

        // when
        val actualValue = consistentHashMap[key]

        // then
        assertEquals(expectedValue, actualValue)
    }

    private fun prepareMap(mockedHashFunction: HashFunction<String>): ConsistentHashMap<String, String> {
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)
        for (i in 1..3) {
            val node = "node$i"
            consistentHashMap[node] = "value$i"
        }
        return consistentHashMap
    }

    @Test
    fun `should throw when collision occurred`() {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash(any()) } returns BigInteger.valueOf(1)
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)
        consistentHashMap["node1"] = "value1"

        // when
        val exc = assertThrows<CollisionException> { consistentHashMap["node2"] = "value2" }

        // then
        assertEquals("Collision has occurred for key node2. The key must be modified", exc.message)
    }

    @Test
    fun `get should return null if map is empty`() {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash(any()) } returns BigInteger.valueOf(1)
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)

        // when
        val actual = consistentHashMap["key"]

        // then
        assertNull(actual)
    }

    @ParameterizedTest
    @MethodSource("keysNextAfter")
    fun `should return next values after provided keys`(mockedKeyHash: BigInteger, expectedValue: String) {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash("node1") } returns BigInteger.valueOf(1)
        every { mockedHashFunction.hash("node2") } returns BigInteger.valueOf(5)
        every { mockedHashFunction.hash("node3") } returns BigInteger.valueOf(7)
        val consistentHashMap = prepareMap(mockedHashFunction)
        val key = "key" /* key's value doesn't matter. its hash will be mocked anyway*/
        every { mockedHashFunction.hash(key) } returns mockedKeyHash

        // when
        val actualValue = consistentHashMap.nextAfter(key)

        // then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `next after should return first value if it is only value`() {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash("node1") } returns BigInteger.valueOf(1)
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)
        val expectedValue = "value1"
        consistentHashMap["node1"] = expectedValue
        val key = "key"
        every { mockedHashFunction.hash(key) } returns BigInteger.valueOf(2)

        // when
        val actualValue = consistentHashMap.nextAfter(key)

        // then
        assertEquals(expectedValue, actualValue)
    }

    @Test
    fun `next after should return null if map is empty`() {
        // given
        val mockedHashFunction = mockk<HashFunction<String>>()
        every { mockedHashFunction.hash(any()) } returns BigInteger.valueOf(1)
        val consistentHashMap = ConsistentHashMap<String, String>(mockedHashFunction)

        // when
        val actual = consistentHashMap.nextAfter("key")

        // then
        assertNull(actual)
    }

    companion object {
        @JvmStatic
        fun keys() = listOf(
            Arguments.of(BigInteger.ZERO, "value1"),
            Arguments.of(BigInteger.valueOf(4), "value2"),
            Arguments.of(BigInteger.valueOf(5), "value2"),
            Arguments.of(BigInteger.valueOf(6), "value3"),
            Arguments.of(BigInteger.valueOf(8), "value1"),
        )

        @JvmStatic
        fun keysNextAfter() = listOf(
            Arguments.of(BigInteger.ZERO, "value1"),
            Arguments.of(BigInteger.valueOf(4), "value2"),
            Arguments.of(BigInteger.valueOf(5), "value3"),
            Arguments.of(BigInteger.valueOf(8), "value1"),
        )
    }
}
