package com.zlasher.cryptomoneda.model

import java.util.*

class Crypto(var name: String = "", var imageUrl: String = "", var available: Int = 0) {

    fun getDocumenId(): String {
        return name.lowercase(Locale.getDefault())
    }
}