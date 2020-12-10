package coding.academy.scd_ml_kit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import coding.academy.scd_ml_kit.R;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;

public class CodeViewActivity extends AppCompatActivity {


    private static final String ARG_CODE = "CODE";
    String Code;
    CodeView codeView ;
    public static Intent newIntent(Context packageContext, String code) {

        Intent intent = new Intent(packageContext, CodeViewActivity.class);
        intent.putExtra(ARG_CODE, code);
        return intent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_codeview);
        codeView = (CodeView) findViewById(R.id.code_view);

        if(getIntent().getSerializableExtra(ARG_CODE)!=null) {
            Code = getIntent().getSerializableExtra(ARG_CODE).toString() ;
            codeView.setOptions(Options.Default.get(this)
                    .withLanguage("java")
                    .withCode(Code)
                    .withTheme(ColorTheme.MONOKAI));
        }

    }

    @Override
    public void onBackPressed() {

    }
}
