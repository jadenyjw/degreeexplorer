package com.jadenyjw.degreeexplorer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.*
import java.net.HttpCookie
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private var mAuthTask: UserLoginTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // Set up the login form.

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        if (mAuthTask == null) {
            // Reset errors.
            email.error = null
            password.error = null

            // Store values at the time of the login attempt.
            val emailStr = email.text.toString()
            val passwordStr = password.text.toString()

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_form.visibility = if (show) View.GONE else View.VISIBLE
            login_form.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 0 else 1).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_form.visibility = if (show) View.GONE else View.VISIBLE
                        }
                    })

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_form.visibility = if (show) View.GONE else View.VISIBLE
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        val adapter = ArrayAdapter(this@LoginActivity,
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        email.setAdapter(adapter)
    }

    object ProfileQuery {
        val PROJECTION = arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        val ADDRESS = 0
        val IS_PRIMARY = 1
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    inner class UserLoginTask internal constructor(private val mUTORID: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            val client = OkHttpClient()

            //This block makes the original request.
            var url = URL("https://degreeexplorer.utoronto.ca/degreeexplorer/")
            @Throws(IOException::class)
            fun run(url:String):String {
                val request = Request.Builder()
                        .url(url)
                        .build()
                val response = client.newCall(request).execute()
                return response.body().string()
            }

            var urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.instanceFollowRedirects = false
            urlConnection.sslSocketFactory = DegreeExplorerApplication.sslContext.socketFactory
            urlConnection.connect()
            var responseCode = urlConnection.responseCode
            println(responseCode)
            var location = urlConnection.getHeaderField("Location")
            println(location)

            //This block makes the SAML request.
            url = URL(location)
            urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.instanceFollowRedirects = false
            urlConnection.sslSocketFactory = DegreeExplorerApplication.sslContext.socketFactory
            responseCode = urlConnection.responseCode
            println(responseCode)
            val COOKIES_HEADER = "Set-Cookie"
            val msCookieManager = java.net.CookieManager()
            val cookiesHeader = urlConnection.getHeaderField("Set-Cookie")
            var cookies = HttpCookie.parse(cookiesHeader)
            for (cookie in cookies){
                msCookieManager.cookieStore.add(null, cookie)
                println(cookie)
            }


            location = urlConnection.getHeaderField("Location")
            println(location)

            //Strips the JSESSIONID from the URL.
            var in_session = false
            var new_url = ""
            var jsession_id = ""
            for (char in location) {
                if (char == ';')
                    in_session = true
                if (char == '?')
                    in_session = false

                if (!in_session)
                    new_url += char
                else
                    jsession_id += char
            }

            location = new_url

            //This block logs in the user.
            url = URL("https://idpz.utorauth.utoronto.ca" + location)
            urlConnection = url.openConnection() as HttpsURLConnection
            urlConnection.setRequestProperty("Cookie", msCookieManager.cookieStore.cookies[0].toString() + ";");
            urlConnection.sslSocketFactory = DegreeExplorerApplication.sslContext.socketFactory
            println(msCookieManager.cookieStore.cookies[0].toString())
            urlConnection.instanceFollowRedirects = true
            urlConnection.requestMethod = "POST"
            urlConnection.doOutput = true;

            urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");

            var urlParameters = "j_username=$mUTORID&j_password=$mPassword&_eventId_proceed="

            val wr = DataOutputStream(urlConnection.outputStream)
            val writer = BufferedWriter(OutputStreamWriter(wr, "UTF-8"))
            writer.write(urlParameters)
            writer.flush()
            writer.close()
            wr.close()

            urlConnection.connect()

            responseCode = urlConnection.responseCode
            System.out.println("\nSending 'POST' request to URL : " + url)
            System.out.println("Post parameters : " + urlParameters)
            println("Response Code : " + responseCode)

            println(urlConnection.responseMessage)
            location = urlConnection.getHeaderField("Set-Cookie")
            println(location)


            val `in` = urlConnection.errorStream
            val inputAsString = `in`.bufferedReader().use { it.readText() }
            print(inputAsString)


            return true
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null
            showProgress(false)

            if (success!!) {
                finish()
            } else {
                password.error = getString(R.string.error_incorrect_password)
                password.requestFocus()
            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }
}
