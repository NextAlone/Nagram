package tw.nekomimi.nekogram.utils

import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.LocaleController
import org.telegram.messenger.MessagesController
import org.telegram.messenger.R
import org.telegram.tgnet.ConnectionsManager
import org.telegram.tgnet.TLRPC
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.LaunchActivity
import org.telegram.ui.TwoStepVerificationActivity

object PrivacyUtil {

    @JvmStatic
    fun postCheckAll(ctx: Context, account: Int) {

        if (!MessagesController.getMainSettings(account).getBoolean("privacy_warning_skip_phone_number", false)) {

            postCheckPhoneNumberVisible(ctx, account)

        }

        if (!MessagesController.getMainSettings(account).getBoolean("privacy_warning_skip_add_by_phone", false)) {

            postCheckAddMeByPhone(ctx, account)

        }

        if (!MessagesController.getMainSettings(account).getBoolean("privacy_warning_skip_p2p", false)) {

            postCheckAllowP2p(ctx, account)

        }

        if (!MessagesController.getMainSettings(account).getBoolean("privacy_warning_skip_2fa", false)) {

            postCheckAllow2fa(ctx, account)

        }

    }

    private fun postCheckPhoneNumberVisible(ctx: Context, account: Int) {

        ConnectionsManager.getInstance(account).sendRequest(TLRPC.TL_account_getPrivacy().apply {

            key = TLRPC.TL_inputPrivacyKeyPhoneNumber()

        }, { response, _ ->

            if (response is TLRPC.TL_account_privacyRules) {

                if (response.rules.isEmpty()) {

                    showPrivacyAlert(ctx, account, 0)

                } else {

                    response.rules.forEach {

                        if (it is TLRPC.TL_privacyValueAllowAll) {

                            showPrivacyAlert(ctx, account, 0)

                            return@forEach

                        }

                    }

                }

            }

        }, ConnectionsManager.RequestFlagFailOnServerErrors)

    }

    private fun postCheckAddMeByPhone(ctx: Context, account: Int) {

        ConnectionsManager.getInstance(account).sendRequest(TLRPC.TL_account_getPrivacy().apply {

            key = TLRPC.TL_inputPrivacyKeyAddedByPhone()

        }, { response, _ ->

            if (response is TLRPC.TL_account_privacyRules) {

                if (response.rules.isEmpty()) {

                    showPrivacyAlert(ctx, account, 1)

                } else {

                    response.rules.forEach {

                        if (it is TLRPC.TL_privacyValueAllowAll) {

                            showPrivacyAlert(ctx, account, 1)

                            return@forEach

                        }

                    }

                }

            }

        }, ConnectionsManager.RequestFlagFailOnServerErrors)

    }

    private fun postCheckAllowP2p(ctx: Context, account: Int) {

        ConnectionsManager.getInstance(account).sendRequest(TLRPC.TL_account_getPrivacy().apply {

            key = TLRPC.TL_inputPrivacyKeyPhoneP2P()

        }, { response, _ ->

            if (response is TLRPC.TL_account_privacyRules) {

                if (response.rules.isEmpty()) {

                    showPrivacyAlert(ctx, account, 2)

                } else {

                    response.rules.forEach {

                        if (it is TLRPC.TL_privacyValueAllowAll) {

                            showPrivacyAlert(ctx, account, 2)

                            return@forEach

                        }

                    }

                }

            }

        }, ConnectionsManager.RequestFlagFailOnServerErrors)

    }

    private fun postCheckAllow2fa(ctx: Context, account: Int) {

        ConnectionsManager.getInstance(account).sendRequest(TLRPC.TL_account_getPassword(), { response, _ ->

            if (response is TLRPC.TL_account_password) {

                if (!response.has_password) {

                    show2faAlert(ctx, account, response)
                }

            }

        }, ConnectionsManager.RequestFlagFailOnServerErrors)

    }

    private fun showPrivacyAlert(ctx: Context, account: Int, type: Int) {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(LocaleController.getString("PrivacyNotice", R.string.PrivacyNotice))

        builder.setMessage(AndroidUtilities.replaceTags(when (type) {
            0 -> LocaleController.getString("PrivacyNoticePhoneVisible", R.string.PrivacyNoticePhoneVisible)
            1 -> LocaleController.getString("PrivacyNoticeAddByPhone", R.string.PrivacyNoticeAddByPhone)
            else -> LocaleController.getString("PrivacyNoticeP2p", R.string.PrivacyNoticeP2p)
        }))

        builder.setPositiveButton(LocaleController.getString("ApplySuggestion", R.string.ApplySuggestion)) { _, _ ->

            ConnectionsManager.getInstance(account).sendRequest(TLRPC.TL_account_setPrivacy().apply {

                key = when (type) {

                    0 -> TLRPC.TL_inputPrivacyKeyPhoneNumber()
                    1 -> TLRPC.TL_inputPrivacyKeyAddedByPhone()
                    else -> TLRPC.TL_inputPrivacyKeyPhoneP2P()

                }

                rules = arrayListOf(when (type) {

                    0 -> TLRPC.TL_inputPrivacyValueDisallowAll()
                    1 -> TLRPC.TL_inputPrivacyValueAllowContacts()
                    else -> TLRPC.TL_inputPrivacyValueDisallowAll()

                })

            }) { _, _ -> /* ignored */ }


        }

        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null)

        builder.setNeutralButton(LocaleController.getString("DoNotRemindAgain", R.string.DoNotRemindAgain)) { _, _ ->

            MessagesController.getMainSettings(account).edit().putBoolean("privacy_warning_skip_${when (type) {
                0 -> "phone_number"
                1 -> "add_by_phone"
                2 -> "p2p"
                else -> "2fa"
            }
            }", true).apply()

        }

        runCatching {

            (builder.show().getButton(DialogInterface.BUTTON_NEUTRAL) as TextView?)?.setTextColor(Theme.getColor(Theme.key_dialogTextRed2))

        }

    }

    private fun show2faAlert(ctx: Context, account: Int, password: TLRPC.TL_account_password) {

        val builder = AlertDialog.Builder(ctx)

        builder.setTitle(LocaleController.getString("PrivacyNotice", R.string.PrivacyNotice))

        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("PrivacyNotice2fa", R.string.PrivacyNotice2fa)))

        builder.setPositiveButton(LocaleController.getString("Set", R.string.Set)) { _, _ ->

            if (ctx is LaunchActivity) {

                UIUtil.runOnUIThread(Runnable {

                    ctx.presentFragment(TwoStepVerificationActivity(account, password))

                })

            }

        }

        builder.setNeutralButton(LocaleController.getString("Cancel", R.string.Cancel), null)

        builder.setNeutralButton(LocaleController.getString("DoNotRemindAgain", R.string.DoNotRemindAgain)) { _, _ ->

            MessagesController.getMainSettings(account).edit().putBoolean("privacy_warning_skip_2fa", true).apply()

        }

        runCatching {

            (builder.show().getButton(DialogInterface.BUTTON_NEUTRAL) as TextView?)?.setTextColor(Theme.getColor(Theme.key_dialogTextRed2))

        }

    }

}