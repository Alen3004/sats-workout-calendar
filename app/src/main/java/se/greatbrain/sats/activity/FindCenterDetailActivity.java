package se.greatbrain.sats.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import se.greatbrain.sats.R;
import se.greatbrain.sats.WebViewClientImpl;

/**
 * Created by aymenarbi on 30/04/15.
 */
public class FindCenterDetailActivity extends ActionBarActivity
{
    private WebView mWebView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_center_detail);

        mWebView = (WebView) findViewById(R.id.find_center_detail_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        mWebView.setWebViewClient(webViewClient);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String centerUrl = extras.getString("centerUrl");
            mWebView.loadUrl(centerUrl);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        ActionBar actionBar = getSupportActionBar();
        View actionBarView = getLayoutInflater().inflate(R.layout.action_bar_menu, null);
        actionBar.setCustomView(actionBarView);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        // remove left actionbar padding
        android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar) actionBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        ImageView satsBack = (ImageView) findViewById(R.id.btn_dots_logo_sats_menu);
        satsBack.setImageResource(R.drawable.sats_logo_back);

        satsBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });

        TextView actionBarTitle = (TextView) findViewById(R.id.action_bar_text_view);
        actionBarTitle.setText("HITTA CENTER");
        return super.onCreateOptionsMenu(menu);
    }
}
