package xyz.nextalone.nagram.ui.drawer

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.AndroidUtilities.dp
import org.telegram.messenger.ContactsController
import org.telegram.messenger.DialogObject
import org.telegram.messenger.Emoji
import org.telegram.messenger.MessagesController
import org.telegram.messenger.MessagesStorage
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.NotificationsController
import org.telegram.messenger.UserConfig
import org.telegram.messenger.UserObject
import org.telegram.tgnet.ConnectionsManager
import org.telegram.ui.ActionBar.SimpleTextView
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AnimatedEmojiDrawable
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.BackupImageView
import org.telegram.ui.Components.GroupCreateCheckBox
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.Premium.PremiumGradient

class DrawerUserCell(context: Context) : FrameLayout(context), NotificationCenter.NotificationCenterDelegate {

    private val textView: SimpleTextView
    private val imageView: BackupImageView
    private val avatarDrawable: AvatarDrawable
    private val checkBox: GroupCreateCheckBox
    private val botVerification: AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable
    private val status: AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable

    var accountNumber: Int = 0
        private set
    private val rect = RectF()

    init {
        avatarDrawable = AvatarDrawable()
        avatarDrawable.setTextSize(dp(20f))

        imageView = BackupImageView(context)
        imageView.setRoundRadius(dp(18f))
        addView(imageView, LayoutHelper.createFrame(36, 36f, Gravity.LEFT or Gravity.TOP, 14f, 6f, 0f, 0f))

        textView = SimpleTextView(context)
        textView.setPadding(0, dp(4f), 0, dp(4f))
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
        textView.setTextSize(15)
        textView.setTypeface(AndroidUtilities.bold())
        textView.setMaxLines(1)
        textView.setGravity(Gravity.LEFT or Gravity.CENTER_VERTICAL)
        textView.setEllipsizeByGradient(24)
        addView(textView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.CENTER_VERTICAL, 72f, 0f, 14f, 0f))

        botVerification = AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(textView, dp(18f))
        status = AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(textView, dp(20f))
        textView.setRightDrawable(status)

        checkBox = GroupCreateCheckBox(context)
        checkBox.setChecked(true, false)
        checkBox.setCheckScale(0.9f)
        checkBox.setInnerRadDiff(dp(1.5f))
        checkBox.setColorKeysOverrides(Theme.key_chats_unreadCounterText, Theme.key_chats_unreadCounter, Theme.key_chats_menuBackground)
        addView(checkBox, LayoutHelper.createFrame(18, 18f, Gravity.LEFT or Gravity.TOP, 37f, 27f, 0f, 0f))

        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(dp(48f), MeasureSpec.EXACTLY)
        )
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        textView.setTextColor(Theme.getColor(Theme.key_chats_menuItemText))
        status.attach()
        botVerification.attach()
        for (i in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged)
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces)
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        status.detach()
        botVerification.detach()
        for (i in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged)
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces)
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded)

        val rightDrawable = textView.rightDrawable
        if (rightDrawable is AnimatedEmojiDrawable.WrapSizeDrawable) {
            val inner = rightDrawable.drawable
            if (inner is AnimatedEmojiDrawable) {
                inner.removeView(textView)
            }
        }
    }

    override fun didReceivedNotification(id: Int, account: Int, vararg args: Any?) {
        when (id) {
            NotificationCenter.currentUserPremiumStatusChanged -> {
                if (account == accountNumber) setAccount(accountNumber)
            }
            NotificationCenter.emojiLoaded -> textView.invalidate()
            NotificationCenter.updateInterfaces -> {
                val flags = args.getOrNull(0) as? Int ?: 0
                if (flags and MessagesController.UPDATE_MASK_EMOJI_STATUS > 0) {
                    setAccount(accountNumber)
                }
            }
        }
    }

    fun setAccount(account: Int) {
        accountNumber = account
        setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground))
        val user = UserConfig.getInstance(accountNumber).currentUser ?: return
        avatarDrawable.setInfo(account, user)
        var text: CharSequence = ContactsController.formatName(user.first_name, user.last_name)
        try {
            text = Emoji.replaceEmoji(text, textView.paint.fontMetricsInt, false)
        } catch (_: Exception) {}
        textView.setText(text)
        val emojiStatusId = UserObject.getEmojiStatusDocumentId(user)
        if (emojiStatusId != null) {
            textView.setDrawablePadding(dp(4f))
            status.set(emojiStatusId, true)
            status.setParticles(DialogObject.isEmojiStatusCollectible(user.emoji_status), true)
            textView.setRightDrawableOutside(true)
        } else if (MessagesController.getInstance(account).isPremiumUser(user)) {
            textView.setDrawablePadding(dp(6f))
            status.set(PremiumGradient.getInstance().premiumStarDrawableMini, true)
            status.setParticles(false, true)
            textView.setRightDrawableOutside(true)
        } else {
            status.set(null as Drawable?, true)
            status.setParticles(false, true)
            textView.setRightDrawableOutside(false)
        }
        val botVerificationId = DialogObject.getBotVerificationIcon(user)
        if (botVerificationId == 0L || ConnectionsManager.getInstance(account).isTestBackend() != ConnectionsManager.getInstance(UserConfig.selectedAccount).isTestBackend()) {
            botVerification.set(null as Drawable?, false)
            textView.setLeftDrawable(null)
        } else {
            botVerification.set(botVerificationId, false)
            botVerification.setColor(Theme.getColor(Theme.key_featuredStickers_addButton))
            textView.setLeftDrawable(botVerification)
        }
        status.setColor(Theme.getColor(Theme.key_chats_verifiedBackground))
        imageView.imageReceiver.setCurrentAccount(account)
        imageView.setForUserOrChat(user, avatarDrawable)
        checkBox.visibility = if (account == UserConfig.selectedAccount) VISIBLE else INVISIBLE
    }

    override fun onDraw(canvas: Canvas) {
        if (UserConfig.getActivatedAccountsCount() <= 1 || !NotificationsController.getInstance(accountNumber).showBadgeNumber) {
            textView.setRightPadding(0)
            return
        }
        val counter = MessagesStorage.getInstance(accountNumber).mainUnreadCount
        if (counter <= 0) {
            textView.setRightPadding(0)
            return
        }

        val text = String.format("%d", counter)
        val countTop = dp(12.5f)
        val textWidth = Math.ceil(Theme.dialogs_countTextPaint.measureText(text).toDouble()).toInt()
        val countWidth = maxOf(dp(10f), textWidth)
        val countLeft = measuredWidth - countWidth - dp(25f)

        val x = countLeft - dp(5.5f)
        rect.set(x.toFloat(), countTop.toFloat(), (x + countWidth + dp(14f)).toFloat(), (countTop + dp(23f)).toFloat())
        canvas.drawRoundRect(rect, 11.5f * AndroidUtilities.density, 11.5f * AndroidUtilities.density, Theme.dialogs_countPaint)

        canvas.drawText(text, rect.left + (rect.width() - textWidth) / 2, countTop + dp(16f).toFloat(), Theme.dialogs_countTextPaint)

        textView.setRightPadding(countWidth + dp(26f))
    }

    override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
    }
}
