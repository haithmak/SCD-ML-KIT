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

import coding.academy.scd_ml_kit.Analyse;
import coding.academy.scd_ml_kit.R;


public class ManualInputFragment extends Fragment {

    private TextView textView;
    private Analyse _analyse;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.textId);
        final TextView textSuggestion = view.findViewById(R.id.textSuggestion);
        final EditText editText = view.findViewById(R.id.textHere);
        _analyse = new Analyse(requireContext().getApplicationContext());


        Button button = view.findViewById(R.id.check_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _analyse.analyseNormalText(editText.getText().toString(), textView, textSuggestion);
            }
        });

    }


}