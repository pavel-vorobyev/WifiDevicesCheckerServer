package com.pavelvorobyev.wifideviceschecker.businesslogic

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.InetAddress
import java.util.ArrayList

object WifiDevices {

    fun getWifiDevices(): ArrayList<Device>? {
        var br: BufferedReader? = null
        val result = ArrayList<Device>()
        try {
            br = BufferedReader(FileReader("/proc/net/arp"))
            var line = br.readLine()
            var i = 0
            while (line != null) {
                val splitted
                        = line.split(" +".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (splitted.size >= 4) {
                    val mac = splitted[3]
                    if (mac.matches("..:..:..:..:..:..".toRegex())) {
                        val isReachable =
                            InetAddress.getByName(splitted[0]).isReachable(2500)

                        if (isReachable) {
                            result.add(
                                Device(
                                    splitted[0],
                                    splitted[3],
                                    splitted[5],
                                    isReachable
                                )
                            )
                            i++
                        }
                    }
                }
                line = br.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                br!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    data class Device(

        val ip: String,
        val mac: String,
        val nInterface: String,
        val isReachable: Boolean

    )

}