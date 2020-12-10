package coding.academy.scd_ml_kit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import coding.academy.scd_ml_kit.fragments.CameraFragment;
import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.ColorTheme;

public class Analyse {
    private static final String TAG = "Analyse";

    private Context context;



    public Analyse(Context context)
    {
        this.context = context;
    }
    private String result = "";


    public static <T> List<T> convertArrayToList(T array[])
    {

        // Create an empty List
        List<T> list = new ArrayList<>();

        // Iterate through the array
        for (T t : array) {
            // Add each element into the list
            list.add(t);
        }

        // Return the converted List
        return list;
    }

    private static final String ARG_CODE = "CODE";
    public void analyseNormalText(final String text, final TextView errorTextView, final TextView suggestionTextView) {
        errorTextView.setText("");
        suggestionTextView.setText("");

        result = "";


        String keyWord = "";

        List<String> lines = convertArrayToList(text.split("\n")) ;

        Log.e("camera" , "size = " + lines.size() +"  " + lines.toString()) ;

        // الدوران على السطور لتحليل النص داخلهم
        for (final String line : lines) {
            Log.e("camera" , "line = " + line);
                    // فحص الكلمة الاولى لمعرفة نوع البيانات
            if (line.startsWith("if")) {
                keyWord = "if";
            } else if (line.startsWith("for")) {
                keyWord = "for";
            } else if (line.startsWith("int")) {
                keyWord = "int";
            } else if (line.startsWith("switch")) {
                keyWord = "switch";
            } else if (line.startsWith("String")) {
                keyWord = "String";
            } else if (line.startsWith("return")) {
                keyWord = "return";
            } else if (line.startsWith("float")) {
                keyWord = "float";
            } else if (line.startsWith("double")) {
                keyWord = "double";
            } else if (line.startsWith("private void") || line.startsWith("public void") || line.startsWith("public static void") || line.startsWith("private static void") || line.startsWith("void")) {
                keyWord = "function";
            }
            else {
                keyWord = "" ;
            }

            final String finalKeyWord = keyWord;
            Log.e("camera" , "finalKeyWord = " + finalKeyWord) ;

            FirebaseFirestore.getInstance()
                    .collection("regex").whereEqualTo("regex_name", keyWord)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<String> regexList = new ArrayList<>();
                    boolean islineCorrect = false ;


                    for (DocumentSnapshot d : queryDocumentSnapshots) {

                        try {
                            regexList = (List<String>) d.get("regex");

                        } catch (Exception x) {
                            Log.d(TAG, "Exception : " + x.getMessage());
                        }
                        Log.d(TAG, "النوع : " + d.getString("regex_name") + " | " + d.getString("item_name"));

                    }

                    if (regexList != null || !regexList.isEmpty()  ) {

                        islineCorrect = checkErrors(regexList , line) ;

                        if(!islineCorrect){
                            getSuggestion(finalKeyWord , suggestionTextView) ;
                        }
                     //  result += " -> " + line + " "+ islineCorrect + "\n";
                        result += line + "\n";
                        Intent intent = new Intent(context , CodeViewActivity.class);

                        Bundle bundle = new Bundle();

                        bundle.putString(ARG_CODE , result );
                        intent.putExtras(bundle) ;
                        context.startActivity(intent);



                        /*errorTextView.setOptions(Options.Default.get(context)
                                .withLanguage("java")
                                .withCode(result)
                                .withTheme(ColorTheme.MONOKAI));

                         */

                    }




                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }

    }

    private boolean checkErrors(List<String> regexList , String text) {

        boolean crroect = false ;
        try {

            for (String regex : regexList) {
                Pattern pt = Pattern.compile(regex);
                Matcher mt = pt.matcher(text);
                if(mt.matches()){
                    crroect =true ;
                    break;
                }
            }
        } catch (Exception x) {
            Log.d(TAG, "خطأ عند معالجة الريجكس: " + x.getMessage());
        }


        return crroect;
    }






    //"suggestion"
    private void getSuggestion(String keyWord, final TextView textView) {

        FirebaseFirestore.getInstance()
                .collection("regex").whereEqualTo("regex_name", keyWord)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                List<String> suggestion = new ArrayList<>();

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (documentSnapshot.get("suggestion") != null) {
                        suggestion = (List<String>) documentSnapshot.get("suggestion");
                    }
                }

                String text = "الاقتراحات للمتغير \n";
                for (String s : suggestion) {
                    text += s + "\n------------------\n";
                }

                textView.append(text + "\n");


            }
        });

    }
}
