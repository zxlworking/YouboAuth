package cn.iubo.youboauth;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;



public class LicenseActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.license_layout);
        Button acceptBtn = (Button)findViewById(R.id.cancelBtn);
        acceptBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        TextView licenseText = (TextView) findViewById(R.id.license_content);
        String filename = "youbo_software_license.htm";
        String content = Utils.getStringFromHtmlFile(this, filename);
        licenseText.setText(Html.fromHtml(content));
    }


}
