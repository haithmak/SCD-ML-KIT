package coding.academy.scd_ml_kit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import coding.academy.scd_ml_kit.Analyse;
import coding.academy.scd_ml_kit.R;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;


public class ManualInputFragment extends Fragment {

    private TextView textView;
    private Analyse _analyse;
    CodeView codeView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText editText = view.findViewById(R.id.textHere);
        _analyse = new Analyse(getContext());


        Button button = view.findViewById(R.id.check_button);
        codeView = view.findViewById(R.id.code_view);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _analyse.analysCode(editText.getText().toString()).observe(getActivity(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {

                        codeView.setOptions(Options.Default.get(getContext())
                                .withLanguage("java")
                                .withCode(s)
                                .withTheme(ColorTheme.MONOKAI));
                    }
                });

            }
        });
    }
}


