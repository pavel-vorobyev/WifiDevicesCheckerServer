package com.pavelvorobyev.wifideviceschecker.view

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.pavelvorobyev.wifideviceschecker.businesslogic.WifiDevices
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.util.KtorExperimentalAPI
import org.json.JSONObject
import java.lang.NumberFormatException

@SuppressLint("DefaultLocale")
@KtorExperimentalAPI
class MainPresenterImpl(override var view: MainView) : MainPresenter {

    private var server: CIOApplicationEngine? = null

    private fun createServer(port: Int = 27015) {
        server = embeddedServer(CIO, port) {
            routing {
                get("/check") {
                    val requestParams = this.context.request.queryParameters
                    var targetMac = requestParams["mac"]

                    if (targetMac == null) {
                        val macMissedResponseObj = JSONObject()
                        macMissedResponseObj.put("pointer", "mac")
                        macMissedResponseObj.put("reason", "missed")

                        call.respondText(macMissedResponseObj.toString(), contentType = ContentType.Application.Json,
                            status = HttpStatusCode.BadRequest)
                    }

                    val devices = WifiDevices.getWifiDevices()
                    var targetDevice: WifiDevices.Device? = null
                    targetMac = targetMac?.toLowerCase()

                    for (device in devices!!) {
                        println("MAC: ${device.mac}")

                        if (device.mac == targetMac) {
                            targetDevice = device
                            break
                        }
                    }

                    val responseObj = JSONObject()
                    responseObj.put("exists", targetDevice != null)

                    responseObj.put("ip", targetDevice?.ip.toString())
                    responseObj.put("mac", targetDevice?.mac.toString())
                    responseObj.put("interface", targetDevice?.nInterface.toString())

                    val response = responseObj.toString()
                    call.respondText(response, status = HttpStatusCode.OK, contentType = ContentType.Application.Json)
                }
            }
        }
    }

    override fun startServer(port: String, context: Context) {
        if (port.isBlank()) {
            Toast.makeText(context, "Port can not be blank", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            port.toInt()
        }catch (e: NumberFormatException) {
            e.printStackTrace()
            Toast.makeText(context, "Port must be numeric", Toast.LENGTH_SHORT).show()
            return
        }

        if (port.toInt() !in 1..65535) {
            Toast.makeText(context, "Port is not in range 1...65535", Toast.LENGTH_SHORT).show()
            return
        }

        createServer(port.toInt())
        server?.start()

        view.hideStartServerbtn()
        view.setInfoText("Server is running on {YOUR_PUBLIC_IP}:$port")
    }
}