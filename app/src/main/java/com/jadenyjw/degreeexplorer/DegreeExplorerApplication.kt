package com.jadenyjw.degreeexplorer

import android.app.Application
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.cert.Certificate

import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by jaden on 1/2/18.
 */

class DegreeExplorerApplication : Application() {

    companion object {

        lateinit var sslContext: SSLContext
        lateinit var trustManager: X509TrustManager
    }

    override fun onCreate() {
        super.onCreate()

        val cf = CertificateFactory.getInstance("X.509")

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)

        //Load CAs from pem files in resources.
        var ca1Input = BufferedInputStream(resources.openRawResource(R.raw.cert1))
        var ca1: Certificate
        ca1Input.use { ca1Input ->
            ca1 = cf.generateCertificate(ca1Input)
            keyStore.setCertificateEntry("ca1", ca1)
        }
        val ca2Input = BufferedInputStream(resources.openRawResource(R.raw.cert2))
        var ca2: Certificate
        ca2Input.use { ca2Input ->
            ca2 = cf.generateCertificate(ca2Input)
            keyStore.setCertificateEntry("ca2", ca2)
        }

        val ca3Input = BufferedInputStream(resources.openRawResource(R.raw.cert3))
        var ca3: Certificate
        ca3Input.use { ca3Input ->
            ca3 = cf.generateCertificate(ca3Input)
            keyStore.setCertificateEntry("ca3", ca3)
        }


        // Create a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        trustManager = tmf.trustManagers[0] as X509TrustManager
        // Create an SSLContext that uses our TrustManager
        sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

    }
}
