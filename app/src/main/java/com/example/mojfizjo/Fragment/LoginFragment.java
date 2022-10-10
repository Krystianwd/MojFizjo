package com.example.mojfizjo.Fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mojfizjo.MainActivity;
import com.example.mojfizjo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment implements View.OnClickListener{

    public LoginFragment() {
        // Required empty public constructor
    }

    View view;

    //autentykacja
    FirebaseAuth mAuth;

    //pola tekstowe
    EditText emailText;
    EditText passwordText;

    //walidacja
    Boolean isValidEmailAddress = false;
    Boolean isCorrectLoginInputProvided = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(getResources().getString(R.string.jezyk));

        //ustawienie sluchaczy przyciskow
        Button loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(this);
        Button forgotPasswordButton = view.findViewById(R.id.forgot_password_button);
        forgotPasswordButton.setOnClickListener(this);
        Button registrationPageButton = view.findViewById(R.id.registration_page_button);
        registrationPageButton.setOnClickListener(this);
        Button skipLoginButton = view.findViewById(R.id.skip_login_button);
        skipLoginButton.setOnClickListener(this);

        //ustawienie pól tekstowych
        emailText = view.findViewById(R.id.input_email);
        passwordText = view.findViewById(R.id.input_password);

        return view;
    }

    @Override
    public void onClick(View view) {

        //pobranie nazwy wcisnietego przycisku
        String selectedButton = (String) view.getContentDescription();

        //pobranie kontekstu aktywnosci glownej - potrzebne do ponizszych funkcji
        MainActivity mainActivity = (MainActivity)getContext();
        assert mainActivity != null;

        //przycisk "zaloguj sie"
        if(Objects.equals(selectedButton, getResources().getString(R.string.zaloguj))) {

            //walidacja pól
            isCorrectLoginInputProvided = validateLoginInput();
            if (isCorrectLoginInputProvided) {

                //pobranie wpisanych wartosci
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                //sprawdzenie czy uzytkownik nie jest obecnie zalogowany
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {

                    //logowanie
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(requireActivity(), task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {

                                        // Check if user's email is verified
                                        boolean emailVerified = user.isEmailVerified();
                                        if(emailVerified){
                                            //logowanie zakonczone sukcesem, e-mail zweryfikowany
                                            Log.d(TAG, "e-mail verified");

                                            //pokazanie menu dolnego i gornego
                                            mainActivity.showMenus();

                                            //przelaczenie widoku na fragment domowy
                                            Fragment fragment = new HomeFragment();
                                            mainActivity.selectedFragment(fragment);
                                        }
                                        else {
                                            //niezweryfikowany adres e-mail
                                            emailText.setError(getResources().getString(R.string.niezweryfikowany_mail));
                                            //wylogowanie obecnego uzytkownika
                                            mAuth.signOut();
                                        }
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    passwordText.setError(getResources().getString(R.string.zle_haslo));
                                }
                            });

                }
            }

        }

        //przycisk "nie pamietam hasla"
        else if(Objects.equals(selectedButton, getResources().getString(R.string.brak_hasla))) {

            //walidacja maila
            isValidEmailAddress = validateEmail();
            if (isValidEmailAddress) {

                //pobranie maila
                String emailAddress = emailText.getText().toString();

                //sprawdzenie, czy ten e-mail jest w uzyciu
                mAuth.fetchSignInMethodsForEmail(emailAddress)
                        .addOnCompleteListener(task0 -> {
                            //funkcja zwraca pusta tablice kiedy e-mail nie nalezy do zadnego konta w bd
                            boolean isEmailInUse = !Objects.requireNonNull(task0.getResult().getSignInMethods()).isEmpty();
                            if (isEmailInUse) {

                                //sprawdzenie czy uzytkownik nie jest obecnie zalogowany
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser == null) {
                                    //wyslanie maila z resetem hasla
                                    mAuth.sendPasswordResetEmail(emailAddress)
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "Email sent.");
                                                    Toast.makeText(getContext(), getResources().getString(R.string.wiadomosc_reset_hasla), Toast.LENGTH_LONG).show();
                                                } else {
                                                    String exceptionText = String.valueOf(task.getException());
                                                    exceptionText = exceptionText.replace("com.google.firebase.auth.", "");
                                                    Toast.makeText(getContext(), getResources().getString(R.string.blad_wysylania_maila) + exceptionText, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                            } else {
                                //uzytkownik o takim mailu nie istnieje
                                emailText.setError(getResources().getString(R.string.nieznany_mail));
                            }

                        });

            }

        }

        //przycisk "zarejestruj sie"
        else if(Objects.equals(selectedButton, getResources().getString(R.string.zarejestruj))) {

            //przelaczenie widoku na fragment rejestracji
            Fragment fragment = new RegistrationFragment();
            mainActivity.selectedFragment(fragment);
        }

        //przycisk "kontynuuj bez logowania"
        else if(Objects.equals(selectedButton, getResources().getString(R.string.bezlogowania))){
                //rejestracja anonimowa
                mAuth.signInAnonymously()
                        .addOnCompleteListener(requireActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");

                                //pokazanie menu dolnego i gornego
                                mainActivity.showMenus();

                                //przelaczenie widoku na fragment domowy
                                Fragment fragment = new HomeFragment();
                                mainActivity.selectedFragment(fragment);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                String exceptionText = String.valueOf(task.getException());
                                exceptionText = exceptionText.replace("com.google.firebase.auth.", "");
                                Toast.makeText(getContext(), getResources().getString(R.string.blad_logowania) + exceptionText, Toast.LENGTH_SHORT).show();
                            }
                        });

        }
    }

    private Boolean validateEmail(){

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

        return true;
    }

    private Boolean validateLoginInput(){

        //walidacja e-maila
        isValidEmailAddress = validateEmail();
        if (!isValidEmailAddress) return false;
        //czy haslo nie jest puste
        if (passwordText.length()==0){
            passwordText.setError(getResources().getString(R.string.walidacja_brak_hasla));
            return false;
        }

        return true;
    }
}