package com.fm.openinstalldemo.utils;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fm.openinstalldemo.R;

public class PrivacyDialog extends Dialog {

    public PrivacyDialog(Context context) {
        super(context);
    }

    public PrivacyDialog(Context context, int theme) {
        super(context, theme);
    }

    public static interface Linker {
        void link();
    }

    public static class Builder {
        private View.OnClickListener positiveListener;
        private View.OnClickListener negativeListener;
        private Linker serviceLinker;
        private Linker privacyLinker;

        private Context context;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setPositiveListener(View.OnClickListener listener) {
            this.positiveListener = listener;
            return this;
        }

        public Builder setNegativeListener(View.OnClickListener listener) {
            this.negativeListener = listener;
            return this;
        }

        public Builder setServiceLinker(Linker linker) {
            this.serviceLinker = linker;
            return this;
        }

        public Builder setPrivacyLinker(Linker linker) {
            this.privacyLinker = linker;
            return this;
        }

        public PrivacyDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final PrivacyDialog dialog = new PrivacyDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_privacy, null);

            layout.findViewById(R.id.negative).setOnClickListener(negativeListener);
            layout.findViewById(R.id.positive).setOnClickListener(positiveListener);

            SpannableStringBuilder ssb = new SpannableStringBuilder(context.getString(R.string.privacy_text));
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (serviceLinker != null) {
                        serviceLinker.link();
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(true);
                }
            }, 4, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    if (privacyLinker != null) {
                        privacyLinker.link();
                    }
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(true);
                }
            }, 11, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            TextView privacy = (TextView) layout.findViewById(R.id.privacy_text);
            privacy.setText(ssb);
            privacy.setMovementMethod(LinkMovementMethod.getInstance());
            dialog.setContentView(layout);
            return dialog;

        }

    }

}
