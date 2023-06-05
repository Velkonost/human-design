package com.myhumandesignhd.ui.start.ext

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
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
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.myhumandesignhd.App
import com.myhumandesignhd.BuildConfig
import com.myhumandesignhd.R
import com.myhumandesignhd.ui.start.StartFragment
import com.myhumandesignhd.ui.start.StartFragment.Companion.GOOGLE_REQUEST_CODE
import com.myhumandesignhd.ui.start.StartPage
import com.myhumandesignhd.util.Keyboard
import kotlinx.android.synthetic.main.view_signup.emailBtn
import kotlinx.android.synthetic.main.view_signup.emailContainer
import kotlinx.android.synthetic.main.view_signup.emailET
import kotlinx.android.synthetic.main.view_signup.emailSubtitle
import kotlinx.android.synthetic.main.view_signup.emailTitle
import kotlinx.android.synthetic.main.view_signup.gotoInboxBtn
import kotlinx.android.synthetic.main.view_signup.icBreakline
import kotlinx.android.synthetic.main.view_signup.icEmailBack
import kotlinx.android.synthetic.main.view_signup.icEmailBtn
import kotlinx.android.synthetic.main.view_signup.icFbBtn
import kotlinx.android.synthetic.main.view_signup.icGoogleBtn
import kotlinx.android.synthetic.main.view_signup.icGradient
import kotlinx.android.synthetic.main.view_signup.icInboxBack
import kotlinx.android.synthetic.main.view_signup.icSignupLogo
import kotlinx.android.synthetic.main.view_signup.inboxContainer
import kotlinx.android.synthetic.main.view_signup.inboxFooter
import kotlinx.android.synthetic.main.view_signup.inboxSubtitle
import kotlinx.android.synthetic.main.view_signup.inboxTitle
import kotlinx.android.synthetic.main.view_signup.signupContainer
import kotlinx.android.synthetic.main.view_signup.signupFooter
import kotlinx.android.synthetic.main.view_signup.signupSubtitle
import kotlinx.android.synthetic.main.view_signup.signupText
import kotlinx.android.synthetic.main.view_signup.signupTitle


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
    binding.signupView.isVisible = false

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
            if (emailET.text.toString().isValidEmail()) {
                Keyboard.hide(emailET)

                binding.viewModel!!.loginEmail(emailET.text.toString().trim())
                checkInboxPage(emailET.text.toString().trim())
            } else {

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
            gotoMailInbox()
//            onSignupFinished()
        }

        inboxSubtitle.text = inboxSubtitle.text.toString() + " $email"
        inboxSubtitle.highlight(email)

        inboxFooter.text = inboxFooter.text.toString().replace("yourmail", email)

        inboxFooter.withLinks("click here to have us resend the link") {
            binding.viewModel!!.loginEmail(email)
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
    AccessToken.expireCurrentAccessToken()

    val loginButton = LoginButton(requireContext())
    loginButton.setFragment(this)

    LoginManager.getInstance().registerCallback(facebookCallbackManager,
        object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val newAccessToken = result.accessToken.token
                binding.viewModel!!.loginFb(newAccessToken)
            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {
                Log.d("keke", error.message.toString())
            }
        })

    loginButton.performClick()
}

fun StartFragment.loginGoogle() {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()
        .requestServerAuthCode(BuildConfig.GOOGLE_CLIENT_ID)
        .build()

    val googleClient = GoogleSignIn.getClient(requireActivity(), gso)
    startActivityForResult(googleClient.signInIntent, GOOGLE_REQUEST_CODE)
}

fun StartFragment.handleLoginGoogleResult(completedTask: Task<GoogleSignInAccount>) {
    val account = completedTask.getResult(ApiException::class.java)

    account.serverAuthCode?.let { binding.viewModel!!.getGoogleAccessToken(it) }

}

fun TextView.withLinks(s: String, callback: () -> Unit) {
    makeLinks(
        Pair(s, View.OnClickListener {
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
        }

        override fun onClick(view: View) {
            Selection.setSelection((view as TextView).text as Spannable, 0)
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

fun StartFragment.setupSignupTheme() {
    with(binding.signupView) {
        signupContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        icGradient.isVisible = App.preferences.isDarkTheme
        icSignupLogo.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_signup_logo_dark
            else R.drawable.ic_signup_logo_light
        )

        signupTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        signupSubtitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        signupText.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        icEmailBtn.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_email_signup
            else R.drawable.ic_email_signup_light
        )

        icBreakline.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_breakline_signup
            else R.drawable.ic_breakline_signup_light
        )

        icGoogleBtn.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_google_signup
            else R.drawable.ic_google_signup_light
        )

        icFbBtn.setImageResource(
            if (App.preferences.isDarkTheme) R.drawable.ic_fb_signup
            else R.drawable.ic_fb_signup_light
        )

        signupFooter.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        emailContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        icEmailBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        emailTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        emailSubtitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        emailET.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        inboxContainer.setBackgroundColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.darkColor
            else R.color.lightColor
        ))

        icInboxBack.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        inboxTitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))

        inboxSubtitle.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor0_7
            else R.color.darkColor0_7
        ))

//        gotoInboxBtn

        inboxFooter.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (App.preferences.isDarkTheme) R.color.lightColor
            else R.color.darkColor
        ))
    }
}