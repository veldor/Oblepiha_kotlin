package net.veldor.oblepiha_kotlin.model.utils

import android.util.Log
import java.text.DecimalFormat

class RawDataHandler(private val mRawData: String) {
    var batteryLevel = 0
    var packetType: Int
    var activationType: String? = null
    var pingInterval: String? = null
    var indicationTime: String? = null
    var externalTemperature: String? = null
    var pin1Type: String? = null
    var pin2Type: String? = null
    var pin3Type: String? = null
    var pin4Type: String? = null
    var pin1Value: String? = null
    var pin2Value: String? = null
    var pin3Value: String? = null
    var pin4Value: String? = null
    private fun revertHex(s: String): String {
        val sa = s.toCharArray()
        return sa[6].toString() +
                sa[7] +
                sa[4] +
                sa[5] +
                sa[2] +
                sa[3] +
                sa[0] +
                sa[1]
    }

    private fun intFromHex(hex: String): Long {
        return hex.toInt(16).toLong()
    }

    fun getUsed(channel: Int, initial_value: String): String {
        // получу данные
        val data = intFromHex(revertHex(mRawData.substring(8 + channel * 8, 16 + channel * 8)))
        val dData = data.toString().toDouble() / 3200
        val fullData = initial_value.toDouble() + dData
        val df = DecimalFormat("#.000")
        return df.format(fullData).replace(",", ".")
    }

    init {
        packetType = mRawData.substring(0, 2).toInt()
    }
}