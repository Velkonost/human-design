package com.myhumandesignhd.ui.start.ext

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.start.StartFragment
import com.myhumandesignhd.ui.start.StartFragment.Companion.GOOGLE_REQUEST_CODE
import com.myhumandesignhd.ui.start.StartPage
import kotlinx.android.synthetic.main.view_signup.*


fun StartFragment.setupSignup() {
    currentStartPage = StartPage.SIGNUP
    App.preferences.lastLoginPageId = StartPage.SIGNUP.pageId

    binding.startBtn.visibility = View.GONE

    signupFooter.withLinks("Terms of Use") {
        val url = "https://humdesign.info/terms-of-use.php"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }
    signupFooter.withLinks("Privacy Policy") {
        val url = "https://humdesign.info/policy.php"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    with(binding.signupView) {
        isVisible = true

        signupContainer.isVisible = true
        emailContainer.isVisible = false
        inboxContainer.isVisible = false

        icEmailBtn.setOnClickListener {
            loginEmail()
        }

        icGoogleBtn.setOnClickListener {
            loginGoogle()
        }

        icFbBtn.setOnClickListener {
            loginFb()
        }
    }

}

fun StartFragment.onSignupFinished() {
    binding.startBtn.visibility = View.VISIBLE
    setupLetsCalculate()
}

fun StartFragment.loginEmail() {
    with(binding.signupView) {
        emailContainer.isVisible = true
        signupContainer.isVisible = false
        inboxContainer.isVisible = false


        icEmailBack.setOnClickListener {
            setupSignup()
            emailContainer.isVisible = false
        }

        emailBtn.setOnClickListener {
            if (emailET.text.toString().isValidEmail())
                checkInboxPage(emailET.text.toString())
            else {
            }
        }
    }

}

fun StartFragment.checkInboxPage(email: String) {
    with(binding.signupView) {
        inboxContainer.isVisible = true
        signupContainer.isVisible = false
        emailContainer.isVisible = false


        icInboxBack.setOnClickListener {
            loginEmail()
            inboxContainer.isVisible = false
        }

        gotoInboxBtn.setOnClickListener {
//            gotoMailInbox()
            onSignupFinished()
        }

        inboxSubtitle.text = inboxSubtitle.text.toString() + " $email"
        inboxSubtitle.highlight(email)

        inboxFooter.text = inboxFooter.text.toString().replace("yourmail", email)

        inboxFooter.withLinks("click here to have us resend the link") {

        }
    }
}



fun StartFragment.gotoMailInbox() {
    try {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_APP_EMAIL)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        this.startActivity(intent)
    } catch (e: ActivityNotFoundException) {

    }
}

fun StartFragment.loginFb() {

    val loginButton = LoginButton(requireContext())
    loginButton.setReadPermissions(listOf("email"))
    loginButton.permissions
    loginButton.setFragment(this)

    LoginManager.getInstance().registerCallback(facebookCallbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {

                val request = GraphRequest.newMeRequest(result.accessToken) { _object, response ->
//                    AccessToken.expireCurrentAccessToken()
                    // Application code

                    if (_object != null && _object.has("email")) {
                        val email = _object.getString("email")

                        if (email.isValidEmail()) {
                            checkInboxPage(email)
                        }
                    }

                }

                val parameters = Bundle()
                parameters.putString("fields", "email")
                request.parameters = parameters
                request.executeAsync()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(error: FacebookException) {
                // App code
                Log.d("keke", error.message.toString())
            }
        })

    loginButton.performClick()
}

fun StartFragment.loginGoogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()
        .build()

    val googleClient = GoogleSignIn.getClient(requireActivity(), gso)
    startActivityForResult(googleClient.signInIntent, GOOGLE_REQUEST_CODE)
}

fun StartFragment.handleLoginGoogleResult(completedTask: Task<GoogleSignInAccount>) {
    val account = completedTask.getResult(ApiException::class.java)
    Log.d("keke", account.email.toString())

}

fun TextView.withLinks(s: String, callback: () -> Unit) {
    makeLinks(
        Pair(s, View.OnClickListener {
//            showTermsOfService()
            callback.invoke()
        }),
    )
}


fun String.isValidEmail(): Boolean {
    return !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>, underline: Boolean = true) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
//                textPaint.color = ContextCompat.getColor(context, R.color.mainColor)

                textPaint.isUnderlineText = underline
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                link.second.onClick(view)
            }
        }

        this.highlightColor = Color.TRANSPARENT
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)

        if (startIndexOfLink != -1) {
            spannableString.setSpan(
                clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }
    this.movementMethod =
        LinkMovementMethod.getInstance()

    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.highlight(item: String) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    val clickableSpan = object : ClickableSpan() {
        override fun updateDrawState(textPaint: TextPaint) {
            textPaint.color = ContextCompat.getColor(context, R.color.lightColor)


//            textPaint.isUnderlineText = underline
        }

        override fun onClick(view: View) {
            Selection.setSelection((view as TextView).text as Spannable, 0)
//            link.second.onClick(view)
        }
    }

    this.highlightColor = Color.TRANSPARENT
    startIndexOfLink = this.text.toString().indexOf(item, startIndexOfLink + 1)

    if (startIndexOfLink != -1) {
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + item.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    this.movementMethod = LinkMovementMethod.getInstance()

    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}