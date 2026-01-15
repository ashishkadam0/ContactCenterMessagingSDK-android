package com.ms.lcw.fragmentimpl

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lcw.lsdk.chat.LiveChatMessaging
import com.lcw.lsdk.components.LiveChatMessagingFragment
import com.lcw.lsdk.components.LiveChatMessagingFragment.LCWMessagingFragmentActionListener
import com.ms.lcw.R

class ChatBottomSheetFragment : BottomSheetDialogFragment(), LCWMessagingFragmentActionListener {

    companion object {
        const val TAG = "ChatBottomSheet"

        fun newInstance() = ChatBottomSheetFragment()
    }

    private lateinit var behavior: BottomSheetBehavior<View>
    private var chatFragment: LiveChatMessagingFragment? = null
    private var isClosing = false

    fun getBottomSheetBehavior(): BottomSheetBehavior<View>? =
        if (::behavior.isInitialized) behavior else null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_chat_bottomsheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Load chat fragment only once
        if (childFragmentManager.findFragmentById(R.id.chatContainer) == null) {
            val sdk = LiveChatMessaging.getInstance()

            chatFragment = sdk.getBrandedMessagingFragment(
                requireContext(),
                sdk.omniChannelConfig,
                sdk.chatSDKConfig,
                null
            )

            chatFragment?.let {
                childFragmentManager.beginTransaction()
                    .replace(R.id.chatContainer, it)
            }?.commit()
        }

        // Close button
        view.findViewById<View>(R.id.btnClose).setOnClickListener {
            if (LiveChatMessaging.getInstance().isChatInProgress) {
                chatFragment?.onClose()
            } else {
                dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog ?: return
        val window = dialog.window ?: return

        val sheet = dialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        val screenHeight = resources.displayMetrics.heightPixels
        val expandedHeight = (screenHeight * 0.9f).toInt()   // 90% height
        val collapsedHeight = (screenHeight * 0.1f).toInt()  // 10% visible

        // Set bottom sheet height
        sheet.layoutParams.height = expandedHeight
        sheet.requestLayout()

        behavior = BottomSheetBehavior.from(sheet).apply {

            // Start expanded
            state = BottomSheetBehavior.STATE_EXPANDED

            // Never allow hide
            isHideable = false

            // Disable half-expanded
            isFitToContents = true

            // Collapsed visible height
            peekHeight = collapsedHeight

            isDraggable = true
        }

        // Initial dim (expanded)
        window.setDimAmount(0.5f)

        // Handle dim based on bottom sheet state
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // 👈 Clear background when collapsed
                        window.setDimAmount(0f)
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // 👈 Dim background when expanded
                        window.setDimAmount(0.5f)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Smooth dim transition while dragging
                val offset = slideOffset.coerceAtLeast(0f)
                window.setDimAmount(0.5f * offset)
            }
        })
    }

    override fun onFinish() {
        chatFragment?.onClose()
        LiveChatMessaging.getInstance().unmount()
        dismissCompletely()
    }

    override fun refreshMenu() {

    }

    fun dismissCompletely() {
        if (isClosing) return
        isClosing = true
        cleanupChildFragments()
        dismissAllowingStateLoss()
    }

    private fun cleanupChildFragments() {
        childFragmentManager.fragments.forEach {
            childFragmentManager.beginTransaction()
                .remove(it)
                .commitNowAllowingStateLoss()
        }
        chatFragment = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        cleanupChildFragments()
    }


}
