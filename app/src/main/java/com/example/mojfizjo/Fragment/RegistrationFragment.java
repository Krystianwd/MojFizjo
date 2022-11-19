package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.constraintlayout.helper.widget.MotionEffect;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegistrationFragment extends Fragment implements View.OnClickListener{

    View view;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    //pola tekstowe
    EditText emailText;
    EditText passwordText;
    EditText repeatPasswordText;

    //wiadomosc o mailu weryfikacyjnym
    TextView verificationMessage;

    //walidacja
    Boolean isCorrectRegistrationInputProvided = false;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(getResources().getString(R.string.jezyk));

        //uchwyt do bazy danych z dokumentami Firebase Firestore
        db = FirebaseFirestore.getInstance();

        //ustawienie sluchaczy przyciskow
        Button registerButton = view.findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
        Button returnToLoginButton = view.findViewById(R.id.return_to_login_button);
        returnToLoginButton.setOnClickListener(this);

        //ustawienie pól tekstowych
        emailText = view.findViewById(R.id.input_email);
        passwordText = view.findViewById(R.id.input_password);
        repeatPasswordText = view.findViewById(R.id.input_password_repeat);

        //ustawienie wiadomosci
        verificationMessage = view.findViewById(R.id.verificationMessage);

        return view;
    }

    @Override
    public void onClick(View view) {

        //pobranie nazwy wcisnietego przycisku
        String selectedButton = (String) view.getContentDescription();

        //pobranie kontekstu aktywnosci glownej - potrzebne do ponizszych funkcji
        MainActivity mainActivity = (MainActivity) getContext();
        assert mainActivity != null;

        //przycisk "zarejestruj się"
        if (Objects.equals(selectedButton, getResources().getString(R.string.zarejestruj))) {

            //walidacja pól
            isCorrectRegistrationInputProvided = validateRegistrationInput();
            if (isCorrectRegistrationInputProvided) {

                //pobranie wpisanych wartosci
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                //sprawdzenie czy uzytkownik nie jest obecnie zalogowany
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {

                    //stworzenie konta
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    //pobraine ID
                                    final String newUserID = user.getUid();

                                    //wyslanie maila weryfikacyjnego
                                    user.sendEmailVerification()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    verificationMessage.setVisibility(View.VISIBLE);

                                                    //stworzenie obiektu ustawien uzytkownika w bd
                                                    db.collection("user_settings")
                                                            .get()
                                                            .addOnCompleteListener(task2 -> {
                                                                if(task2.isSuccessful()){

                                                                    boolean currentUserSettingsFound = false;

                                                                    for (QueryDocumentSnapshot document : task2.getResult()) {

                                                                        //pobranie danych ustawien uzytkownikow
                                                                        UserSettings userSettings = document.toObject(UserSettings.class);
                                                                        userSettings.setUID(document.getId());
                                                                        String userSettings_userID = userSettings.getUserID();

                                                                        //sprawdzenie, czy istnieja dane dla nowego uzytkownika
                                                                        if(Objects.equals(userSettings_userID, newUserID)) currentUserSettingsFound = true;
                                                                    }

                                                                    //jezeli w bd nie ma ustawien dla uzytkownika o tym id, tworzymy je
                                                                    if(!currentUserSettingsFound){

                                                                        //domyslne ustawienia
                                                                        UserSettings newUserSettings = new UserSettings();
                                                                        newUserSettings.setUserID(newUserID);
                                                                        newUserSettings.setNotificationHours(0);
                                                                        newUserSettings.setNotifyAboutSteps(false);
                                                                        newUserSettings.setNotifyAboutWater(false);
                                                                        newUserSettings.setNotifyAboutWorkout(false);
                                                                        newUserSettings.setStepsAmount(0);
                                                                        newUserSettings.setStepsDone(false);
                                                                        newUserSettings.setStepsNumber(0);
                                                                        newUserSettings.setWaterDaysAmount(0);
                                                                        newUserSettings.setWaterDone(false);
                                                                        newUserSettings.setWaterLiters(0);
                                                                        newUserSettings.setWorkoutDaysAmount(0);

                                                                        //dodanie do bd
                                                                        db.collection("user_settings")
                                                                                .add(newUserSettings)
                                                                                .addOnFailureListener(e -> Log.e(MotionEffect.TAG, "onFailure: Can't add user settings. "+ e.getCause()))
                                                                                .addOnSuccessListener(documentReference -> Log.d(MotionEffect.TAG, "onComplete: Added settings for new user. " + documentReference.getId()));
                                                                    }

                                                                }
                                                                else {
                                                                    Log.e(TAG, "Error getting documents: ", task2.getException());
                                                                }
                                                            });

                                                    //wylogowanie obecnego uzytkownika
                                                    mAuth.signOut();

                                                } else {
                                                    Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                                    Toast.makeText(getContext(), getResources().getString(R.string.blad_wysylania_maila) + task1.getException(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(task1 -> {
                                                Log.e(TAG, "createUserWithEmail:failure", task1.getCause());
                                                Toast.makeText(getContext(), getResources().getString(R.string.blad_wysylania_maila) + task1.getCause(), Toast.LENGTH_SHORT).show();
                                            });

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    String exceptionText = String.valueOf(task.getException());
                                    exceptionText = exceptionText.replace("com.google.firebase.auth.", "");
                                    Toast.makeText(getContext(), getResources().getString(R.string.blad_rejestracji) + exceptionText, Toast.LENGTH_SHORT).show();
                                }
                            });
                }

            }
        }

        //przycisk "powrot do logowania"
        else if(Objects.equals(selectedButton, getResources().getString(R.string.powrot_do_logowania))) {

            //wylogowanie obecnego uzytkownika
            mAuth.signOut();

            //przelaczenie widoku na fragment rejestracji
            Fragment fragment = new LoginFragment();
            mainActivity.selectedFragment(fragment);
        }
    }

    private Boolean validateRegistrationInput(){

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
            }
        }

        //walidacja hasla
        if (passwordText.length()==0){
            passwordText.setError(getResources().getString(R.string.walidacja_brak_hasla));
            return false;
        } else if (passwordText.length()<6) {
            passwordText.setError(getResources().getString(R.string.walidacja_za_krotkie_haslo));
            return false;
        }

        //walidacja powtorzonego hasla
        if (repeatPasswordText.length()==0){
            repeatPasswordText.setError(getResources().getString(R.string.walidacja_brak_hasla2));
            return false;
        } else if ( !repeatPasswordText.getText().toString().equals( passwordText.getText().toString() ) ){
            repeatPasswordText.setError(getResources().getString(R.string.walidacja_rozne_hasla));
            return false;
        }

        return true;
    }
}