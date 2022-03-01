package com.zlasher.cryptomoneda.adapter

import com.zlasher.cryptomoneda.model.Crypto

interface CryptosAdapterListener {

    fun onBuyCryptoClicked(crypto: Crypto){

    }
}