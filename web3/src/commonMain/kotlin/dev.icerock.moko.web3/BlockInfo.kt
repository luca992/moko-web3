/*
 * Copyright 2021 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

@file:UseSerializers(BigIntegerSerializer::class)

package dev.icerock.moko.web3

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.icerock.moko.web3.hex.Hex256String
import dev.icerock.moko.web3.hex.Hex32String
import dev.icerock.moko.web3.hex.Hex8String
import dev.icerock.moko.web3.serializer.BigIntegerSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class BlockInfo(
    val number: BigInteger?, // null if the block is pending
    val hash: BlockHash,
    val parentHash: Hex32String,
    val nonce: Hex8String,
    val logsBloom: Hex256String,
    val transactionsRoot: Hex32String,
    val stateRoot: Hex32String,
    val miner: WalletAddress,
    val difficulty: BigInteger,
    val totalDifficulty: BigInteger,
    val extraData: String,
    val size: BigInteger,
    val gasLimit: BigInteger,
    val gasUsed: BigInteger,
    val timestamp: BigInteger,
    val transactions: List<TransactionInfo>,
    val uncles: List<BlockHash>
)
