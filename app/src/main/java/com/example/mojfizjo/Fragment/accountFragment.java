package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.R;

import com.example.mojfizjo.UserSettings;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class accountFragment extends Fragment  implements View.OnClickListener{



    public accountFragment() {
        // Required empty public constructor
    }

    View view;
    MainActivity mainActivity;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    //elementy ukladu
    EditText emailText;
    EditText passwordText;
    EditText newPasswordText;
    Button confirmButton;
    TextView accountHeader;

    //wybor funkcji przycisku
    String confirmButtonFunction;

    //walidacja
    Boolean isCorrectLoginInputProvided = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(getResources().getString(R.string.jezyk));

        //uchwyt do bazy danych z dokumentami Firebase Firestore
        db = FirebaseFirestore.getInstance();

        //ustawienie sluchaczy przyciskow
        confirmButton = view.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(this);

        //pola tekstowe
        emailText = view.findViewById(R.id.input_email);
        passwordText = view.findViewById(R.id.input_password);
        newPasswordText = view.findViewById(R.id.input_new_password);

        //naglowek
        accountHeader = view.findViewById(R.id.account_header);

        //ukrycie domyslnie ukladu usuniecia konta i zmiany hasla
        hideChangePasswordLayout();
        hideDeleteAccountLayout();

        emailText.setError(getResources().getString(R.string.walidacja_brak_maila));
        passwordText.setError(getResources().getString(R.string.walidacja_brak_hasla));

        assert getArguments() != null;
        String mode = getArguments().getString("accountFragmentMode");
        switch (mode){
            case "change_password":
                showChangePasswordLayout();
                break;
            case "delete_account":
                showDeleteAccountLayout();
                break;
        }

        return view;
    }

    private void hideChangePasswordLayout(){
        confirmButton.setVisibility(View.GONE);
        emailText.setVisibility(View.GONE);
        passwordText.setVisibility(View.GONE);
        newPasswordText.setVisibility(View.GONE);
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showChangePasswordLayout(){
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setBackgroundTintList(requireContext().getResources().getColorStateList(R.color.teal_200));
        confirmButton.setTextColor(requireContext().getResources().getColorStateList(R.color.black));
        confirmButton.setText(getResources().getString(R.string.potwierdz));
        confirmButtonFunction = "change_password";
        emailText.setVisibility(View.VISIBLE);
        passwordText.setVisibility(View.VISIBLE);
        newPasswordText.setVisibility(View.VISIBLE);
        accountHeader.setText(getResources().getString(R.string.zmiana_hasla));
    }

    private void hideDeleteAccountLayout(){
        confirmButton.setVisibility(View.GONE);
        emailText.setVisibility(View.GONE);
        passwordText.setVisibility(View.GONE);
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void showDeleteAccountLayout(){
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setBackgroundTintList(requireContext().getResources().getColorStateList(R.color.colorAccent));
        confirmButton.setTextColor(requireContext().getResources().getColorStateList(R.color.white));
        confirmButton.setText(getResources().getString(R.string.usun_konto2));
        confirmButtonFunction = "delete_account";
        emailText.setVisibility(View.VISIBLE);
        passwordText.setVisibility(View.VISIBLE);
        accountHeader.setText(getResources().getString(R.string.usuwanie_konta));
    }

    @Override
    public void onClick(View view) {

        //pobranie nazwy wcisnietego przycisku
        String selectedButton = (String) view.getContentDescription();

        //pobranie kontekstu aktywnosci glownej - potrzebne do ponizszych funkcji
        mainActivity = (MainActivity)getContext();
        assert mainActivity != null;

        if(Objects.equals(selectedButton, getResources().getString(R.string.potwierdz))) {

            //walidacja danych w polach tekstowych
            isCorrectLoginInputProvided = validateLoginInfo();
            if(isCorrectLoginInputProvided){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    // Get auth credentials from the user for re-authentication
                    AuthCredential credential = EmailAuthProvider.getCredential(emailText.getText().toString(), passwordText.getText().toString());
                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                            .addOnSuccessListener(task -> {

                                    //w zaleznosci od kontekstu wcisniecia przycisku "potwierdz"
                                    Log.d(TAG, "User re-authenticated.");

                                    //zmiana hasla
                                    if (Objects.equals(confirmButtonFunction, "change_password")) {
                                        boolean isValidNewPassword = accountFragment.this.validatePasswordField(newPasswordText);
                                        if (isValidNewPassword) {
                                            String newPassword = newPasswordText.getText().toString();
                                            user.updatePassword(newPassword)
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            Log.d(TAG, "User password updated.");
                                                            Toast.makeText(accountFragment.this.getContext(), accountFragment.this.getResources().getString(R.string.haslo_zmienione), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }

                                    //usuwanie konta
                                    else if (Objects.equals(confirmButtonFunction, "delete_account")) {
                                        user.delete()
                                                .addOnCompleteListener(task12 -> {
                                                    if (task12.isSuccessful()) {
                                                        Log.d(TAG, "User account deleted.");
                                                        //usuniecie ustawien uzytkownika z bd
                                                        db.collection("user_settings")
                                                                .get()
                                                                .addOnCompleteListener(task2 -> {
                                                                    if(task2.isSuccessful()){

                                                                        for (QueryDocumentSnapshot document : task2.getResult()) {

                                                                            //pobranie danych ustawien uzytkownikow
                                                                            UserSettings userSettings = document.toObject(UserSettings.class);
                                                                            userSettings.setUID(document.getId());
                                                                            String userSettings_userID = userSettings.getUserID();
                                                                            String usersettings_ID = userSettings.getUID();

                                                                            //sprawdzenie, czy uzytkownika ma zapisane ustawienia
                                                                            if(Objects.equals(userSettings_userID, user.getUid())) {

                                                                                //usuniecie ustawien
                                                                                db.collection("user_settings")
                                                                                        .document(usersettings_ID)
                                                                                        .delete()
                                                                                        .addOnCompleteListener(task3->{
                                                                                            if(task3.isSuccessful())Log.d(TAG, "Document deleted");
                                                                                            else Log.e(TAG, "Error deleting document: ", task3.getException());
                                                                                        });
                                                                            }
                                                                        }

                                                                    }
                                                                    else {
                                                                        Log.e(TAG, "Error getting documents: ", task2.getException());
                                                                    }
                                                                });
                                                        //go back to login screen
                                                        accountFragment.this.logOut();
                                                    }
                                                });
                                    }


                            })
                            .addOnFailureListener(task -> {
                                //bledne haslo
                                Log.e(TAG, "Re-authentication failed!");
                                passwordText.setError(getResources().getString(R.string.bledne_haslo));
                            });
                }
            }

        }
    }

    //wylogowanie uzytkownika
    private void logOut(){
        //ustawienie ukladu
        hideChangePasswordLayout();
        hideDeleteAccountLayout();

        //wylogowanie
        mAuth.signOut();

        //ukrycie menu
        mainActivity.hideMenus();

        //przelaczenie widoku na fragment logowania
        Fragment fragment = new LoginFragment();
        mainActivity.selectedFragment(fragment);
    }

    private Boolean validatePasswordField(EditText password){
        //walidacja dowolnego pola tekstowego zawierajaceo haslo
        if (password.length()==0){
            password.setError(getResources().getString(R.string.walidacja_brak_hasla));
            return false;
        } else if (password.length()<6) {
            password.setError(getResources().getString(R.string.walidacja_za_krotkie_haslo));
            return false;
        }
        return true;
    }

    private Boolean validateLoginInfo(){
        //walidacja e-maila
        if (emailText.length()==0){
            emailText.setError(getResources().getString(R.string.walidacja_brak_maila));
            return false;
        } else {
            //pobranie kontekstu aktywnosci glownej - potrzebne do ponizszych funkcji
            MainActivity mainActivity = (MainActivity)getContext();
            assert mainActivity != null;
            Pattern p = Pattern.compile(mainActivity.emailRegex); //regex e-mail uzywany w Firebase
            Matcher m = p.matcher(emailText.getText().toString());
            if(!m.matches()){
                emailText.setError(getResources().getString(R.string.walidacja_zly_mail));
                return false;
            } else {
                //sprawdzenie, czy uzytkownik podal swoj email (nie probuje zmienic danych kogos innego)
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    if(!Objects.equals(email, emailText.getText().toString())) {
                        emailText.setError(getResources().getString(R.string.bledny_email));
                        return false;
                    }
                }

            }
        }
        //walidacja hasla
        return validatePasswordField(passwordText);
    }

}