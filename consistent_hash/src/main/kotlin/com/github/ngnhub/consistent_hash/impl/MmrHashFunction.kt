package com.github.ngnhub.consistent_hash.impl

import com.github.ngnhub.consistent_hash.HashFunction
import com.google.common.hash.Hashing
import java.math.BigInteger
import java.nio.charset.StandardCharsets

class MmrHashFunction : HashFunction<String> {

    override fun hash(value: String): BigInteger {
        val murmur3128 = Hashing.murmur3_128()
        val hashString = murmur3128.hashString(value, StandardCharsets.UTF_8).asBytes()
        val index = BigInteger(1, hashString)
        return index
    }
}
