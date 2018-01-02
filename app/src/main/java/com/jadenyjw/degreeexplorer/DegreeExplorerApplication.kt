package com.jadenyjw.degreeexplorer

import android.app.Application
import java.io.BufferedInputStream
import java.security.KeyStore
import java.security.cert.Certificate

import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by jaden on 1/2/18.
 */

class DegreeExplorerApplication : Application() {

    companion object {

        lateinit var sslContext: SSLContext
    }

    override fun onCreate() {
        super.onCreate()
        // Load CAs from an InputStream
        // (could be from a resource or ByteArrayInputStream or ...)
        val cf = CertificateFactory.getInstance("X.509")
        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
        var ca1Input = BufferedInputStream(resources.openRawResource(R.raw.cert1))

        val ca1: Certificate
        try {
            ca1 = cf.generateCertificate(ca1Input)
            println("ca=" + (ca1 as X509Certificate).subjectDN)
        } finally {
            ca1Input.close()
        }
        val ca2Input = BufferedInputStream(resources.openRawResource(R.raw.cert2))

        val ca2: Certificate
        try {
            ca2 = cf.generateCertificate(ca2Input)
            println("ca=" + (ca2 as X509Certificate).subjectDN)
        } finally {
            ca2Input.close()
        }

        val ca3Input = BufferedInputStream(resources.openRawResource(R.raw.cert3))

        val ca3: Certificate
        try {
            ca3 = cf.generateCertificate(ca3Input)
            println("ca=" + (ca3 as X509Certificate).subjectDN)
        } finally {
            ca3Input.close()
        }


        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)

        keyStore.setCertificateEntry("ca1", ca1)
        keyStore.setCertificateEntry("ca2", ca2)
        keyStore.setCertificateEntry("ca3", ca3)

        // Create a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // Create an SSLContext that uses our TrustManager
        sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.trustManagers, null)

    }
}
