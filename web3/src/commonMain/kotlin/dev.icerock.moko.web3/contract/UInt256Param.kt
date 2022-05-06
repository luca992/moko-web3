/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.icerock.moko.web3.contract

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import dev.icerock.moko.web3.hex.internal.hexStringToByteArray
import dev.icerock.moko.web3.hex.internal.toHex

object UInt256Param : StaticEncoder<BigInteger> {
    @OptIn(ExperimentalStdlibApi::class)
    override fun encode(item: BigInteger): ByteArray {
        val dataByteArray = item.toString(16).hexStringToByteArray(unsafe = true)

        return when {
            dataByteArray.size < 32 -> ByteArray(32 - dataByteArray.size) + dataByteArray
            else -> dataByteArray.copyOf(32)
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun decode(source: ByteArray): BigInteger {
        val value = source.toHex()
        return value.toBigInteger(16)
    }
}
