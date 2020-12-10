package coding.academy.scd_ml_kit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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
   FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance() ;

   MutableLiveData<String> CodeLiveData = new MutableLiveData<>();


    public MutableLiveData<String> analysCode(final String code) {
        String keyWord = "";
        final List<String> lines = convertArrayToList(code.split("\n"));
        for (int i = 0; i < lines.size(); i++) {
            final String line = lines.get(i);
            Log.e("camera", "line = " + line);
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
            } else {
                keyWord = "";
            }

            final String finalKeyWord = keyWord;
            firebaseFirestore
                    .collection("regex").whereEqualTo("regex_name", keyWord)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                    List<String> regexList = new ArrayList<>();
                    boolean islineCorrect = false ;
                    List<String> suggestion = new ArrayList<>();

                    for (DocumentSnapshot d : queryDocumentSnapshots) {

                        try {
                            regexList = (List<String>) d.get("regex");

                            if (d.get("suggestion") != null) {
                                suggestion = (List<String>) d.get("suggestion");
                            }

                        } catch (Exception x) {
                            Log.d(TAG, "Exception : " + x.getMessage());
                        }
                        Log.d(TAG, "النوع : " + d.getString("regex_name") + " | " + d.getString("item_name"));

                    }

                    if (regexList != null || !regexList.isEmpty()  ) {

                        islineCorrect = checkErrors(regexList , line) ;

                        if(!islineCorrect){


                             result +=  "xxx" +line + "xxx"+ "\n" +  getSuggestion(suggestion) +"\n" ;
                       //     result += line + "\n" ;
                        }else {
                            result += line + "\n";

                        }

                             CodeLiveData.setValue(result);

                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });



        }
        return CodeLiveData;
    }


    public void analyseNormalText(final String text, final TextView errorTextView, final TextView suggestionTextView) {
        errorTextView.setText("");
        suggestionTextView.setText("");

        final Intent intent = new Intent(context , CodeViewActivity.class);
        result = "";


        String keyWord = "";

        final List<String> lines = convertArrayToList(text.split("\n")) ;



        Log.e("camera" , "size = " + lines.size() +"  " + lines.toString()) ;

        // الدوران على السطور لتحليل النص داخلهم

        for(int i =0 ; i < lines.size() ; i++)
        {
            final String line =lines.get(i) ;
            final  int j=i;
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
            firebaseFirestore
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
                           // result += line + "\n" +  getSuggestion(finalKeyWord) ;
                            result += line + "\n" ;

                        }else {
                            result += line + "\n";
                        }

                     //  result += " -> " + line + " "+ islineCorrect + "\n";
                       // if(j==lines.size()-1){


                            Bundle bundle = new Bundle();

                            bundle.putString(ARG_CODE , result );
                            intent.putExtras(bundle) ;
                            context.startActivity(intent);
                      //  }


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





    String suggestions ;
    //"suggestion"
    private String getSuggestion(List<String> suggestionsList) {

                String text = "/* Suggestion \n ";
                for (String s : suggestionsList) {
                    text += s + "\n";
                }
                suggestions = text + "\n */";

        return suggestions ;
    }
}
