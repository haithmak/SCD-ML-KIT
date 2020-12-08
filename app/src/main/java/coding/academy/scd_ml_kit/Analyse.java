package coding.academy.scd_ml_kit;

import android.content.Context;
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

public class Analyse {
    private static final String TAG = "Analyse";

    private Context context;

    public Analyse(Context context) {
        this.context = context;
    }

    // old
    private void checkError(String text) {

        StringBuilder wordStringBuilder = new StringBuilder();

        List<String> keyWords = new ArrayList<>();
        List<String> lines = new ArrayList<>();

        short n = 0;

        for (char c : text.toCharArray()) {

            if (c == ' ' && n == 0) {

                keyWords.add(wordStringBuilder.toString());
                wordStringBuilder = new StringBuilder();

                n += 1;
            } else if (c == '\n') {
                n = 0;
            } else if (n == 0) {
                wordStringBuilder.append(c);
            }

        }

        keyWords.add(wordStringBuilder.toString());


        for (char c : text.toCharArray()) {

            if (c == '\n') {

                lines.add(wordStringBuilder.toString());
                wordStringBuilder = new StringBuilder();


            } else {
                wordStringBuilder.append(c);
            }

        }

        lines.add(wordStringBuilder.toString());

        //Log.d(TAG, "checkError: " + keyWords);

        for (final String word : keyWords) {


            FirebaseFirestore.getInstance()
                    .collection("regex").whereEqualTo("regex_name", word)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<String> regex = new ArrayList<>();

                    for (DocumentSnapshot d : queryDocumentSnapshots) {

                        try {
                            regex = (List<String>) d.get("regex");
                        } catch (Exception x) {

                        }

                        //Log.d(TAG, "onSuccess: " + d.get("regex"));

                    }

                    for (String s : regex) {
                        Log.d(TAG, "onSuccess: " + s);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Exceptions", "onFailure: " + e.getMessage());
                }
            });


        }

    }

    private String result = "";

    public void analyseNormalText(final String text, final TextView errorTextView, final TextView suggestionTextView) {
        errorTextView.setText("");
        suggestionTextView.setText("");

        result = "";

        StringBuilder wordStringBuilder = new StringBuilder();
        List<String> lines = new ArrayList<>();
        String keyWord = "";

        // لترتيب النص كل سطر في ليست
        for (char c : text.toCharArray()) {

            if (c == '\n' || c == ';') {

                if (c == ';') {
                    wordStringBuilder.append(c);
                }

                lines.add(wordStringBuilder.toString());
                wordStringBuilder = new StringBuilder();


            } else {
                wordStringBuilder.append(c);
            }

        }

        // اضافة اخر سطر
        if (!wordStringBuilder.toString().isEmpty() || !wordStringBuilder.toString().equals(" "))
            lines.add(wordStringBuilder.toString());


        // الدوران على السطور لتحليل النص داخلهم
        for (final String line : lines) {

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


            final String finalKeyWord = keyWord;

            FirebaseFirestore.getInstance()
                    .collection("regex").whereEqualTo("regex_name", keyWord)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<String> regex = new ArrayList<>();

                    for (DocumentSnapshot d : queryDocumentSnapshots) {

                        try {
                            regex = (List<String>) d.get("regex");
                        } catch (Exception x) {
                            Log.d(TAG, "Exception : " + x.getMessage());
                        }
                        Log.d(TAG, "النوع : " + d.getString("regex_name") + " | " + d.getString("item_name"));
                        //result += "النوع : " + d.getString("regex_name") + " | " + d.getString("item_name") + "\n";

                    }

                    boolean correct = false;

                    if (regex != null) {

                        Log.d(TAG, "النص : " + line);

                        for (String s : regex) {
                            Log.d(TAG, "الريجكس: " + s);
                            //result += "\nالريجكس: -> " + s + "";

                            if (checkErrors(s, line)) {
                                correct = true;
                            }

                        }

                        if (correct) {
                            Log.d(TAG, "النتيجة : صح");
                            //result += line + "\n | النتيجة : صح" + "\n" + "-----------------------------------------------\n";

                        } else if (!line.isEmpty() && !line.equals(" ")) {
                            Log.d(TAG, "النتيجة : خطا");
                            result += " -> " + line + "\n | النتيجة : خطأ" + "\n" + "------------\n";
                            getSuggestion(finalKeyWord, suggestionTextView);
                        }

                        errorTextView.setText(result);

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


    private boolean checkErrors(String regex, String text) {

        try {

            Pattern pt = Pattern.compile(regex);
            Matcher mt = pt.matcher(text);

            return mt.matches();
        } catch (Exception x) {
            Log.d(TAG, "خطأ عند معالجة الريجكس: " + x.getMessage());
            return false;
        }
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
