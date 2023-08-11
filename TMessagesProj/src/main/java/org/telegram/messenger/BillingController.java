package org.telegram.messenger;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.core.util.Pair;

import com.google.android.exoplayer2.util.Util;

import org.telegram.messenger.utils.BillingUtilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.PremiumPreviewFragment;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BillingController {

    private static BillingController instance;

    private String lastPremiumTransaction = "";
    private String lastPremiumToken = "";

    public static BillingController getInstance() {
        if (instance == null) {
            instance = new BillingController(ApplicationLoader.applicationContext);
        }
        return instance;
    }

    private BillingController(Context ctx) {

    }

    public String getLastPremiumTransaction() {
        return lastPremiumTransaction;
    }

    public String getLastPremiumToken() {
        return lastPremiumToken;
    }

    public String formatCurrency(long amount, String currency) {
        return formatCurrency(amount, currency, getCurrencyExp(currency));
    }

    public String formatCurrency(long amount, String currency, int exp) {
        if (currency.isEmpty()) {
            return String.valueOf(amount);
        }
        Currency cur = Currency.getInstance(currency);
        if (cur != null) {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
            numberFormat.setCurrency(cur);
            return numberFormat.format(amount / Math.pow(10, exp));
        }
        return amount + " " + currency;
    }

    @SuppressWarnings("ConstantConditions")
    public int getCurrencyExp(String currency) {
        return 0;
    }

    public boolean isReady() {
        return false;
    }
}
