/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:UseSerializers(BigIntegerSerializer::class)

package dev.icerock.moko.web3

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.icerock.moko.web3.serializer.BigIntegerSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TransactionInfo(
    val hash: TransactionHash,
    val nonce: BigInteger,
    val blockHash: BlockHash,
    val blockNumber: BigInteger?,
    val transactionIndex: BigInteger?,
    val from: EthereumAddress,
    val to: EthereumAddress?,
    val value: BigInteger,
    val gasPrice: BigInteger,
    val gas: BigInteger,
    val input: String
)
