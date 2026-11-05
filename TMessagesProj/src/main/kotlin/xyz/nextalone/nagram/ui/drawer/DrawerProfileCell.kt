package xyz.nextalone.nagram.ui.drawer

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import org.telegram.PhoneFormat.PhoneFormat
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.ApplicationLoader
import org.telegram.messenger.Emoji
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessagesController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.R
import org.telegram.messenger.UserConfig
import org.telegram.messenger.UserObject
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.BaseFragment
import org.telegram.ui.ActionBar.DrawerLayoutContainer
import org.telegram.ui.ActionBar.SimpleTextView
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.Components.AnimatedEmojiDrawable
import org.telegram.ui.Components.AvatarDrawable
import org.telegram.ui.Components.BackupImageView
import org.telegram.ui.Components.BulletinFactory
import org.telegram.ui.Components.CubicBezierInterpolator
import org.telegram.ui.Components.LayoutHelper
import org.telegram.ui.Components.RLottieDrawable
import org.telegram.ui.Components.RLottieImageView
import org.telegram.ui.Components.Reactions.HwEmojis
import org.telegram.ui.Components.SnowflakesEffect
import org.telegram.ui.DialogsActivity
import org.telegram.ui.ThemeActivity
import xyz.nextalone.nagram.helper.DrawerHelper
import tw.nekomimi.nekogram.NekoConfig

class DrawerProfileCell(
    context: Context,
    private val drawerLayoutContainer: DrawerLayoutContainer,
    private val onAccountsToggle: () -> Unit,
) : FrameLayout(context), NotificationCenter.NotificationCenterDelegate {

    private val avatarImageView: BackupImageView
    private val accountRowView: View
    private val nameTextView: SimpleTextView
    private val phoneTextView: TextView
    private val shadowView: ImageView
    private val arrowView: ImageView
    private val darkThemeView: RLottieImageView

    private val srcRect = Rect()
    private val destRect = Rect()
    private val paint = Paint()
    private var currentColor: Int? = null
    private var currentIconColor: Int? = null
    private var iconColorAnimator: ValueAnimator? = null
    private var pendingCrossfadeFrom: Int? = null
    private var snowflakesEffect: SnowflakesEffect? = null
    private var accountsShown = false

    val status: AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable

    private var lastAccount = -1
    private var lastUser: TLRPC.User? = null
    private var premiumStar: Drawable? = null

    companion object {
        private var sunDrawable: RLottieDrawable? = null
    }

    init {
        shadowView = ImageView(context)
        shadowView.visibility = INVISIBLE
        shadowView.scaleType = ImageView.ScaleType.FIT_XY
        shadowView.setImageResource(R.drawable.compose_panel_shadow)
        addView(shadowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 70, Gravity.LEFT or Gravity.BOTTOM))

        accountRowView = View(context)
        accountRowView.setOnClickListener {
            onAccountsToggle()
        }
        addView(accountRowView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, 59, Gravity.LEFT or Gravity.BOTTOM))

        avatarImageView = BackupImageView(context)
        avatarImageView.imageReceiver.setRoundRadius(AndroidUtilities.dp(32f))
        avatarImageView.setOnClickListener {
            DrawerHelper.openMyProfile(drawerLayoutContainer)
        }
        addView(avatarImageView, LayoutHelper.createFrame(64, 64f, Gravity.LEFT or Gravity.BOTTOM, 16f, 0f, 0f, 67f))

        nameTextView = object : SimpleTextView(context) {
            override fun invalidate() {
                if (HwEmojis.grab(this)) return
                super.invalidate()
            }

            @Suppress("DEPRECATION")
            override fun invalidate(l: Int, t: Int, r: Int, b: Int) {
                if (HwEmojis.grab(this)) return
                super.invalidate(l, t, r, b)
            }

            override fun invalidateDrawable(who: Drawable) {
                if (HwEmojis.grab(this)) return
                super.invalidateDrawable(who)
            }

            @Suppress("DEPRECATION")
            override fun invalidate(dirty: Rect) {
                if (HwEmojis.grab(this)) return
                super.invalidate(dirty)
            }
        }
        nameTextView.setPadding(0, AndroidUtilities.dp(4f), 0, AndroidUtilities.dp(4f))
        nameTextView.setTextSize(15)
        nameTextView.setTypeface(AndroidUtilities.bold())
        nameTextView.setGravity(Gravity.LEFT or Gravity.CENTER_VERTICAL)
        nameTextView.setEllipsizeByGradient(true)
        nameTextView.setRightDrawableOutside(true)
        addView(
            nameTextView,
            LayoutHelper.createFrame(
                LayoutHelper.MATCH_PARENT,
                LayoutHelper.WRAP_CONTENT.toFloat(),
                Gravity.LEFT or Gravity.BOTTOM,
                16f,
                0f,
                52f,
                28f
            )
        )

        phoneTextView = TextView(context)
        phoneTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f)
        phoneTextView.setLines(1)
        phoneTextView.maxLines = 1
        phoneTextView.isSingleLine = true
        phoneTextView.gravity = Gravity.LEFT
        addView(
            phoneTextView,
            LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.WRAP_CONTENT.toFloat(), Gravity.LEFT or Gravity.BOTTOM, 16f, 0f, 52f, 9f)
        )

        arrowView = ImageView(context)
        arrowView.scaleType = ImageView.ScaleType.CENTER
        arrowView.setImageResource(R.drawable.msg_expand)
        arrowView.setColorFilter(PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuName), PorterDuff.Mode.SRC_IN))
        addView(arrowView, LayoutHelper.createFrame(59, 59, Gravity.RIGHT or Gravity.BOTTOM))
        setArrowState(false)

        val playDrawable = sunDrawable == null
        if (playDrawable) {
            sunDrawable = RLottieDrawable(R.raw.sun, AndroidUtilities.dp(28f), AndroidUtilities.dp(28f), true, null)
            sunDrawable!!.setPlayInDirectionOfCustomEndFrame(true)
            if (Theme.isCurrentThemeDay()) {
                sunDrawable!!.setCustomEndFrame(0)
                sunDrawable!!.setCurrentFrame(0)
            } else {
                sunDrawable!!.setCurrentFrame(35)
                sunDrawable!!.setCustomEndFrame(36)
            }
        }

        darkThemeView = object : RLottieImageView(context) {
            override fun onInitializeAccessibilityNodeInfo(info: AccessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(info)
                info.text = if (Theme.isCurrentThemeDark())
                    LocaleController.getString(R.string.AccDescrSwitchToDayTheme)
                else
                    LocaleController.getString(R.string.AccDescrSwitchToNightTheme)
            }
        }
        darkThemeView.isFocusable = true
        darkThemeView.background = Theme.createCircleSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 0, 0)
        sunDrawable!!.colorFilter = PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuName), PorterDuff.Mode.SRC_IN)
        darkThemeView.scaleType = ImageView.ScaleType.CENTER
        darkThemeView.setAnimation(sunDrawable)
        if (Build.VERSION.SDK_INT >= 21) {
            darkThemeView.background = Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1, AndroidUtilities.dp(17f))
            Theme.setRippleDrawableForceSoftware(darkThemeView.background as RippleDrawable)
        }
        if (!playDrawable && sunDrawable!!.customEndFrame != sunDrawable!!.currentFrame) {
            darkThemeView.playAnimation()
        }

        darkThemeView.setOnClickListener {
            if (DialogsActivity.switchingTheme) return@setOnClickListener
            DialogsActivity.switchingTheme = true
            val preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", Activity.MODE_PRIVATE)
            var dayThemeName = preferences.getString("lastDayTheme", "Blue")!!
            if (Theme.getTheme(dayThemeName) == null || Theme.getTheme(dayThemeName).isDark) {
                dayThemeName = "Blue"
            }
            var nightThemeName = preferences.getString("lastDarkTheme", "Dark Blue")!!
            if (Theme.getTheme(nightThemeName) == null || !Theme.getTheme(nightThemeName).isDark) {
                nightThemeName = "Dark Blue"
            }
            var themeInfo = Theme.getActiveTheme()
            if (dayThemeName == nightThemeName) {
                if (themeInfo.isDark || dayThemeName == "Dark Blue" || dayThemeName == "Night") {
                    dayThemeName = "Blue"
                } else {
                    nightThemeName = "Dark Blue"
                }
            }

            val toDark = dayThemeName == themeInfo.key
            if (toDark) {
                themeInfo = Theme.getTheme(nightThemeName)
                sunDrawable!!.setCustomEndFrame(36)
            } else {
                themeInfo = Theme.getTheme(dayThemeName)
                sunDrawable!!.setCustomEndFrame(0)
            }
            if (!toDark) {
                val fromColor = currentIconColor
                pendingCrossfadeFrom = fromColor
                if (fromColor != null) {
                    sunDrawable!!.colorFilter = PorterDuffColorFilter(fromColor, PorterDuff.Mode.SRC_IN)
                }
                darkThemeView.visibility = INVISIBLE
                android.view.Choreographer.getInstance().postFrameCallback {
                    android.view.Choreographer.getInstance().postFrameCallback {
                        darkThemeView.playAnimation()
                        switchTheme(themeInfo, toDark)
                    }
                }
            } else {
                darkThemeView.playAnimation()
                switchTheme(themeInfo, toDark)
            }

            val openSettings = Runnable {
                drawerLayoutContainer.drawer?.closeDrawer(false)
                DrawerHelper.parentLayoutRef?.presentFragment(ThemeActivity(ThemeActivity.THEME_TYPE_NIGHT))
            }
            val lastFragment: BaseFragment? = DrawerHelper.parentLayoutRef?.lastFragment
            val bf: BulletinFactory? = lastFragment?.let { BulletinFactory.of(it) }
            Theme.turnOffAutoNight(bf, openSettings)
        }
        darkThemeView.setOnLongClickListener {
            drawerLayoutContainer.drawer?.closeDrawer(false)
            DrawerHelper.parentLayoutRef?.presentFragment(ThemeActivity(ThemeActivity.THEME_TYPE_BASIC))
            true
        }
        addView(darkThemeView, LayoutHelper.createFrame(48, 48f, Gravity.RIGHT or Gravity.BOTTOM, 0f, 0f, 6f, 90f))

        if (Theme.getEventType() == 0) {
            snowflakesEffect = SnowflakesEffect(0)
            snowflakesEffect!!.setColorKey(Theme.key_chats_menuName)
        }

        status = AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(20f))
        nameTextView.setRightDrawable(status)
        nameTextView.setRightDrawableOnClick {
            val user = lastUser ?: return@setRightDrawableOnClick
            if (user.premium || UserObject.getEmojiStatusDocumentId(user) != null) {
                DrawerHelper.showSelectStatusDialog(this, drawerLayoutContainer)
            }
        }
    }

    fun getEmojiStatusLocation(rect: Rect) {
        val rightDrawable = nameTextView.rightDrawable
        if (rightDrawable == null) {
            rect.set(nameTextView.width - 1, nameTextView.height / 2 - 1, nameTextView.width + 1, nameTextView.height / 2 + 1)
        } else {
            rect.set(rightDrawable.bounds)
        }
        rect.offset(nameTextView.x.toInt(), nameTextView.y.toInt())
    }

    fun getEmojiStatusDrawableParent(): View = nameTextView

    private fun startIconColorCrossfade(from: Int, to: Int) {
        iconColorAnimator?.cancel()
        sunDrawable?.colorFilter = PorterDuffColorFilter(from, PorterDuff.Mode.SRC_IN)
        iconColorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
            duration = 400
            addUpdateListener {
                val c = it.animatedValue as Int
                sunDrawable?.colorFilter = PorterDuffColorFilter(c, PorterDuff.Mode.SRC_IN)
            }
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(a: android.animation.Animator) {
                    if (iconColorAnimator === a) iconColorAnimator = null
                    sunDrawable?.colorFilter = PorterDuffColorFilter(to, PorterDuff.Mode.SRC_IN)
                }
            })
            start()
        }
    }

    private fun switchTheme(themeInfo: Theme.ThemeInfo, toDark: Boolean, sourceView: RLottieImageView? = darkThemeView) {
        val pos = IntArray(2)
        darkThemeView.getLocationInWindow(pos)
        pos[0] += darkThemeView.measuredWidth / 2
        pos[1] += darkThemeView.measuredHeight / 2
        NotificationCenter.getGlobalInstance()
            .postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, pos, -1, toDark, sourceView)
    }

    @Suppress("DEPRECATION")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        status.attach()
        updateColors()
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded)
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme)
        for (i in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        status.detach()
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded)
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme)
        for (i in 0 until UserConfig.MAX_ACCOUNT_COUNT) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged)
        }
        if (lastAccount >= 0) {
            NotificationCenter.getInstance(lastAccount).removeObserver(this, NotificationCenter.userEmojiStatusUpdated)
            NotificationCenter.getInstance(lastAccount).removeObserver(this, NotificationCenter.updateInterfaces)
            lastAccount = -1
        }
        val rightDrawable = nameTextView.rightDrawable
        if (rightDrawable is AnimatedEmojiDrawable.WrapSizeDrawable) {
            val inner = rightDrawable.drawable
            if (inner is AnimatedEmojiDrawable) {
                inner.removeView(nameTextView)
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148f) + AndroidUtilities.statusBarHeight, MeasureSpec.EXACTLY)
            )
        } else {
            try {
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148f), MeasureSpec.EXACTLY)
                )
            } catch (e: Exception) {
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(148f))
                FileLog.e(e)
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val backgroundDrawable = Theme.getCachedWallpaper()
        val backgroundKey = applyBackground(false)
        val useImageBackground = backgroundKey != Theme.key_chats_menuTopBackground &&
            Theme.isCustomTheme() && !Theme.isPatternWallpaper() &&
            backgroundDrawable != null &&
            backgroundDrawable !is ColorDrawable &&
            backgroundDrawable !is GradientDrawable
        var drawCatsShadow = false
        val shadowColor: Int
        if (!useImageBackground && Theme.hasThemeKey(Theme.key_chats_menuTopShadowCats)) {
            shadowColor = Theme.getColor(Theme.key_chats_menuTopShadowCats)
            drawCatsShadow = true
        } else {
            shadowColor = if (Theme.hasThemeKey(Theme.key_chats_menuTopShadow))
                Theme.getColor(Theme.key_chats_menuTopShadow)
            else
                Theme.getServiceMessageColor() or -0x1000000
        }
        if (currentColor == null || currentColor != shadowColor) {
            currentColor = shadowColor
            shadowView.drawable.setColorFilter(PorterDuffColorFilter(shadowColor, PorterDuff.Mode.MULTIPLY))
        }
        val iconColor = pickIconColor(backgroundKey, useImageBackground)
        if (currentIconColor == null || currentIconColor != iconColor) {
            val prev = currentIconColor
            currentIconColor = iconColor
            val crossfadeFrom = pendingCrossfadeFrom ?: prev
            if (pendingCrossfadeFrom != null && crossfadeFrom != null && crossfadeFrom != iconColor) {
                pendingCrossfadeFrom = null
                startIconColorCrossfade(crossfadeFrom, iconColor)
            } else if (iconColorAnimator == null && pendingCrossfadeFrom == null) {
                sunDrawable!!.colorFilter = PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN)
            }
            arrowView.setColorFilter(PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN))
        }
        nameTextView.setTextColor(Theme.getColor(Theme.key_chats_menuName))
        if (useImageBackground) {
            phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhone))
            if (shadowView.visibility != VISIBLE) shadowView.visibility = VISIBLE
            if (backgroundDrawable is ColorDrawable || backgroundDrawable is GradientDrawable) {
                backgroundDrawable.setBounds(0, 0, measuredWidth, measuredHeight)
                backgroundDrawable.draw(canvas)
            } else if (backgroundDrawable is BitmapDrawable) {
                val bitmap: Bitmap = backgroundDrawable.bitmap
                val scaleX = measuredWidth.toFloat() / bitmap.width.toFloat()
                val scaleY = measuredHeight.toFloat() / bitmap.height.toFloat()
                val scale = maxOf(scaleX, scaleY)
                val width = (measuredWidth / scale).toInt()
                val height = (measuredHeight / scale).toInt()
                val x = (bitmap.width - width) / 2
                val y = (bitmap.height - height) / 2
                srcRect.set(x, y, x + width, y + height)
                destRect.set(0, 0, measuredWidth, measuredHeight)
                try {
                    canvas.drawBitmap(bitmap, srcRect, destRect, paint)
                } catch (e: Throwable) {
                    FileLog.e(e)
                }
            }
        } else {
            val visibility = if (drawCatsShadow) VISIBLE else INVISIBLE
            if (shadowView.visibility != visibility) shadowView.visibility = visibility
            phoneTextView.setTextColor(Theme.getColor(Theme.key_chats_menuPhoneCats))
            super.onDraw(canvas)
        }

        snowflakesEffect?.onDraw(this, canvas)
    }

    fun isAccountsShown(): Boolean = accountsShown

    fun setAccountsShown(value: Boolean, animated: Boolean) {
        if (accountsShown == value) return
        accountsShown = value
        setArrowState(animated)
    }

    @Suppress("DEPRECATION")
    fun setUser(user: TLRPC.User?, accounts: Boolean) {
        val account = UserConfig.selectedAccount
        if (account != lastAccount) {
            if (lastAccount >= 0) {
                NotificationCenter.getInstance(lastAccount).removeObserver(this, NotificationCenter.userEmojiStatusUpdated)
                NotificationCenter.getInstance(lastAccount).removeObserver(this, NotificationCenter.updateInterfaces)
            }
            lastAccount = account
            NotificationCenter.getInstance(lastAccount).addObserver(this, NotificationCenter.userEmojiStatusUpdated)
            NotificationCenter.getInstance(lastAccount).addObserver(this, NotificationCenter.updateInterfaces)
        }
        lastUser = user
        if (user == null) return
        accountsShown = accounts
        setArrowState(false)
        var text: CharSequence = UserObject.getUserName(user)
        try {
            text = Emoji.replaceEmoji(text, nameTextView.paint.fontMetricsInt, false)
        } catch (_: Exception) {
        }

        nameTextView.setText(text)
        val emojiStatusId = UserObject.getEmojiStatusDocumentId(user)
        if (emojiStatusId != null) {
            val isCollectible = user.emoji_status is TLRPC.TL_emojiStatusCollectible
            nameTextView.setDrawablePadding(AndroidUtilities.dp(4f))
            status.set(emojiStatusId, true)
            status.setParticles(isCollectible, true)
        } else if (user.premium) {
            nameTextView.setDrawablePadding(AndroidUtilities.dp(4f))
            if (premiumStar == null) {
                premiumStar = ContextCompat.getDrawable(context, R.drawable.msg_premium_liststar)!!.mutate()
            }
            premiumStar!!.setColorFilter(PorterDuffColorFilter(Theme.getColor(Theme.key_chats_menuPhoneCats), PorterDuff.Mode.MULTIPLY))
            status.set(premiumStar, true)
            status.setParticles(false, true)
        } else {
            status.set(null as Drawable?, true)
            status.setParticles(false, true)
        }
        status.setColor(Theme.getColor(if (Theme.isCurrentThemeDark()) Theme.key_chats_verifiedBackground else Theme.key_chats_menuPhoneCats))
        phoneTextView.text = if (NekoConfig.hidePhone.Bool()) {
            LocaleController.getString(R.string.PhoneHidden)
        } else if (!user.phone.isNullOrEmpty()) {
            PhoneFormat.getInstance().format("+" + user.phone)
        } else {
            ""
        }
        val avatarDrawable = AvatarDrawable(user)
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundInProfileBlue))
        avatarImageView.setForUserOrChat(user, avatarDrawable)
        applyBackground(true)
    }

    private fun pickIconColor(backgroundKey: Int, useImageBackground: Boolean): Int {
        val nameColor = Theme.getColor(Theme.key_chats_menuName)
        if (useImageBackground) return nameColor
        val bgColor = Theme.getColor(backgroundKey)
        val bgLight = ColorUtils.calculateLuminance(bgColor) > 0.5
        val nameLight = ColorUtils.calculateLuminance(nameColor) > 0.5
        return if (bgLight && nameLight) Theme.getColor(Theme.key_chats_menuItemIcon) else nameColor
    }

    fun applyBackground(force: Boolean): Int {
        val currentTag = tag as? Int
        val backgroundKey = if (Theme.hasThemeKey(Theme.key_chats_menuTopBackground) && Theme.getColor(Theme.key_chats_menuTopBackground) != 0)
            Theme.key_chats_menuTopBackground
        else
            Theme.key_chats_menuTopBackgroundCats
        if (force || currentTag == null || backgroundKey != currentTag) {
            setBackgroundColor(Theme.getColor(backgroundKey))
            accountRowView.background = Theme.createSelectorDrawable(
                Theme.getColor(Theme.key_listSelector),
                Theme.RIPPLE_MASK_ALL,
            )
            tag = backgroundKey
        }
        return backgroundKey
    }

    fun updateColors() {
        snowflakesEffect?.updateColors()
        status.setColor(Theme.getColor(if (Theme.isCurrentThemeDark()) Theme.key_chats_verifiedBackground else Theme.key_chats_menuPhoneCats))
    }

    private fun setArrowState(animated: Boolean) {
        val rotation = if (accountsShown) 180.0f else 0.0f
        if (animated) {
            arrowView.animate().rotation(rotation).setDuration(220).setInterpolator(CubicBezierInterpolator.EASE_OUT).start()
        } else {
            arrowView.animate().cancel()
            arrowView.rotation = rotation
        }
        accountRowView.contentDescription = if (accountsShown)
            LocaleController.getString(R.string.AccDescrHideAccounts)
        else
            LocaleController.getString(R.string.AccDescrShowAccounts)
    }

    override fun didReceivedNotification(id: Int, account: Int, vararg args: Any?) {
        when (id) {
            NotificationCenter.emojiLoaded -> nameTextView.invalidate()
            NotificationCenter.needSetDayNightTheme -> {
                currentColor = null
                currentIconColor = null
                applyBackground(true)
                updateColors()
                invalidate()
            }

            NotificationCenter.userEmojiStatusUpdated -> setUser(args.getOrNull(0) as? TLRPC.User, accountsShown)
            NotificationCenter.currentUserPremiumStatusChanged -> setUser(
                UserConfig.getInstance(UserConfig.selectedAccount).currentUser,
                accountsShown
            )

            NotificationCenter.updateInterfaces -> {
                val flags = args.getOrNull(0) as? Int ?: 0
                if (flags and MessagesController.UPDATE_MASK_NAME != 0 ||
                    flags and MessagesController.UPDATE_MASK_AVATAR != 0 ||
                    flags and MessagesController.UPDATE_MASK_STATUS != 0 ||
                    flags and MessagesController.UPDATE_MASK_PHONE != 0 ||
                    flags and MessagesController.UPDATE_MASK_EMOJI_STATUS != 0
                ) {
                    setUser(UserConfig.getInstance(UserConfig.selectedAccount).currentUser, accountsShown)
                }
            }
        }
    }

    fun updateSunDrawable(toDark: Boolean) {
        sunDrawable?.setCustomEndFrame(if (toDark) 36 else 0)
        darkThemeView.playAnimation()
    }
}
