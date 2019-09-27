package com.pavelvorobyev.wifideviceschecker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.pavelvorobyev.wifidevicescheckerserver.R
import io.ktor.util.KtorExperimentalAPI
import kotlinx.android.synthetic.main.activity_main.*

@KtorExperimentalAPI
class MainActivity : AppCompatActivity(), MainView {

    lateinit var presenterImpl: MainPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenterImpl = MainPresenterImpl(this)

        startServerBtnView.setOnClickListener {
            val port = portInputView.text.toString()
            presenterImpl.startServer(port, applicationContext)
        }
    }

    override fun setInfoText(string: String) {
        infoView.text = string
    }

    override fun hideStartServerbtn() {
        startServerBtnView.visibility = View.GONE
    }

}
