package com.github.ngnhub.consistent_hash

import java.math.BigInteger

interface HashFunction<V> {

    fun hash(value: V): BigInteger
}
