package com.ms.lcw

import android.os.*
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lcw.lsdk.builder.LCWOmniChannelConfigBuilder
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.chat.Responses.GetMessageResponse
import com.lcw.lsdk.data.model.ErrorResponse
import com.lcw.lsdk.data.requests.ChatSDKConfig
import com.lcw.lsdk.data.requests.ChatSDKMessage
import com.lcw.lsdk.data.requests.OmnichannelConfig
import com.lcw.lsdk.listeners.LCWMessagingDelegate
import com.ms.lcw.Constants.authTkn
import com.ms.lcw.Constants.orgId
import com.ms.lcw.Constants.orgUrl
import com.ms.lcw.Constants.orgWidgetId
import com.ms.lcw.fragmentimpl.ChatBottomSheetFragment
class ChatFragmentActivity : AppCompatActivity() {

    private lateinit var etOrgId: EditText
    private lateinit var etWidgetId: EditText
    private lateinit var etUrl: EditText
    private lateinit var etAuth: EditText
    private lateinit var btnText: TextView
    private lateinit var etScript: EditText
    private lateinit var btnCopyFCM: Button
    private lateinit var btnReset: Button
    private lateinit var utility: Utility

    private var isMinimised = false
    private var chatSheetFragment: ChatBottomSheetFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initViews()
        setupToolbar()
        utility = Utility()
        setDefaultConfig()
        initSDK()
    }

    private fun initViews() {
        etOrgId = findViewById(R.id.et_orgId)
        etWidgetId = findViewById(R.id.et_orgWidgetId)
        etUrl = findViewById(R.id.et_widgetUrl)
        etAuth = findViewById(R.id.et_auth)
        btnText = findViewById(R.id.btnText)
        etScript = findViewById(R.id.etScript)
        btnCopyFCM = findViewById(R.id.btnFCM)
        btnReset = findViewById(R.id.btnReset)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.MessagingToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun initSDK() {
        val omnichannelConfig = OmnichannelConfig(
            orgId = etOrgId.text.toString(),
            orgUrl = etUrl.text.toString(),
            widgetId = etWidgetId.text.toString()
        )

        val chatSdkConfig = ChatSDKConfig()
        val builder = LCWOmniChannelConfigBuilder
            .EngagementBuilder(omnichannelConfig, chatSdkConfig)
            .build()

        LiveChatMessaging.getInstance().initialize(
            this,
            builder,
            etAuth.text.toString(),
            "test"
        )

        LiveChatMessaging.getInstance().setLCWMessagingDelegate(chatDelegate)
    }

    fun launchChat(view: View?) {

        val fm = supportFragmentManager

        val existing =
            fm.findFragmentByTag(ChatBottomSheetFragment.TAG)
                    as? ChatBottomSheetFragment

        if (existing == null || !existing.isAdded) {
            // Create NEW fragment after dismiss
            chatSheetFragment = ChatBottomSheetFragment.newInstance()
            chatSheetFragment!!.show(fm, ChatBottomSheetFragment.TAG)
            return
        }

        // Fragment is alive → safe to toggle
        val behavior = existing.getBottomSheetBehavior()

        behavior?.let {
            it.state =
                if (it.state == BottomSheetBehavior.STATE_EXPANDED)
                    BottomSheetBehavior.STATE_COLLAPSED
                else
                    BottomSheetBehavior.STATE_EXPANDED
        }
    }


    private val chatDelegate = object : LCWMessagingDelegate {
        override fun onChatMinimizeButtonClick() {
            isMinimised = true
            btnText.text =
                if (LiveChatMessaging.getInstance().isChatInProgress)
                    "Restore Chat" else "Let's Chat"
        }

        override fun onChatCloseButtonClicked() {
            Log.d(TAG, "Chat closed")
        }

        override fun onViewDisplayed() {}
        override fun onChatInitiated() {}
        override fun onCustomerChatEnded() {
        }
        override fun onAgentChatEnded() {}
        override fun onAgentAssigned(content: String) {}
        override fun onLinkClicked(link: String) {}
        override fun onNewCustomerMessage(message: ChatSDKMessage) {}
        override fun onNewMessageReceived(message: GetMessageResponse?) {}
        override fun onError(error: ErrorResponse?) {}
        override fun onPreChatSurveyDisplayed() {}
        override fun onPostChatSurveyDisplayed(isExternalLink: Boolean) {}
        override fun onChatRestored() {}
        override fun onHeaderUtilityClicked() {}
        override fun onBotSignInAuth(content: String) {}
    }

    private fun setDefaultConfig() {
        val omnichannelConfig = utility.retrieveItem(this, "OC")
        val auth = utility.getAuth(this, "OCAuth")

        if (omnichannelConfig != null) {
            etUrl.setText(omnichannelConfig.orgUrl)
            etWidgetId.setText(omnichannelConfig.widgetId)
            etOrgId.setText(omnichannelConfig.orgId)
            etAuth.setText(auth)
        } else {
            etUrl.setText(orgUrl)
            etWidgetId.setText(orgWidgetId)
            etOrgId.setText(orgId)
            etAuth.setText(authTkn)
        }
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}
