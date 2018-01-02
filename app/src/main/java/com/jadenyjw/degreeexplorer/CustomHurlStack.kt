package com.jadenyjw.degreeexplorer

import com.android.volley.toolbox.HurlStack

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

import javax.net.ssl.HttpsURLConnection

/**
 * Created by jaden on 1/2/18.
 */

class CustomHurlStack : HurlStack() {
    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        val httpsURLConnection = super.createConnection(url) as HttpsURLConnection
        try {
            httpsURLConnection.sslSocketFactory = DegreeExplorerApplication.sslContext.socketFactory

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return httpsURLConnection
    }
}
