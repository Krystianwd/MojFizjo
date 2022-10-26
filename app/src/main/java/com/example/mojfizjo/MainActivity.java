package com.example.mojfizjo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.mojfizjo.Adapters.MainPlanRecyclerViewAdapter;
import com.example.mojfizjo.Fragment.ExercisesFragment;
import com.example.mojfizjo.Fragment.HomeFragment;
import com.example.mojfizjo.Fragment.LoginFragment;
import com.example.mojfizjo.Fragment.PlansFragment.PlansFragment;
import com.example.mojfizjo.Fragment.WorkoutFragment;
import com.example.mojfizjo.Fragment.accountFragment;

import com.example.mojfizjo.Models.ExerciseModel;
import com.example.mojfizjo.Models.PlanModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    BottomNavigationView boottomNavigationview;
    FrameLayout frameLayout;

    //autentykacja
    FirebaseAuth mAuth;

    public ArrayList<PlanModel> planModels = new ArrayList<>();
    public ArrayList<PlanModel> planModelsNonEmpty = new ArrayList<>();

    //regex e-mail uzywany w Firebase Auth
    public String emailRegex = ".+@.+\\..+";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpPLanModels();

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(getResources().getString(R.string.jezyk));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        boottomNavigationview = findViewById(R.id.bottom_navigation);
        frameLayout = findViewById(R.id.frame_layout);
        MainPlanRecyclerViewAdapter adapter = new MainPlanRecyclerViewAdapter(this, planModels, this);
        PlansFragment plansFragment = new PlansFragment(planModels, adapter);

        boottomNavigationview.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    HomeFragment homeFragment = new HomeFragment();
                    selectedFragment(homeFragment);
                    break;
                case R.id.plans:
                    selectPlanFragment(plansFragment);
                    break;
                case R.id.workout:
                    WorkoutFragment workoutFragment = new WorkoutFragment(planModels);
                    selectedFragment(workoutFragment);
                    break;
                case R.id.exercises:
                    ExercisesFragment exercisesFragment = new ExercisesFragment();
                    selectedFragment(exercisesFragment);
                    break;
            }
            return false;
        });
        boottomNavigationview.setSelectedItemId(R.id.home);

    }

    @Override
    public void onStart() {
        super.onStart();

        //test, nie usuwac
        Log.d(TAG, emailRegex);

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

            //pokazanie menu
            showMenus();

            Log.d(TAG, "Uzytkownik zarejestrowany");
        } else {

            //ukrycie menu
            hideMenus();

            //Jezeli uzytkownik nie jest zarejestrowany, przekierowanie na strone logowania
            Fragment fragment = new LoginFragment();
            selectedFragment(fragment);
        }
    }

    public void showMenus() {
        boottomNavigationview = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        boottomNavigationview.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
    }

    public void hideMenus() {
        boottomNavigationview = findViewById(R.id.bottom_navigation);
        toolbar = findViewById(R.id.toolbar);
        boottomNavigationview.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
    }

    public void selectedFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void selectPlanFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, "plansFragment");
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification:
                Toast.makeText(this, getResources().getString(R.string.powiadomienia), Toast.LENGTH_SHORT).show();
                break;
            case R.id.search:
                Toast.makeText(this, "Wyszukaj", Toast.LENGTH_SHORT).show();
                break;
            case R.id.account:
                accountFragment accountFragment = new accountFragment();
                boolean isRegisteredAccount = false;

                //sprawdzenie, czy uzytkownik istnieje i ma email (nie anonimowy)
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String email = user.getEmail();
                    if (email != null) {
                        isRegisteredAccount = true;
                    }
                }
                //przeniesienie na strone konta
                Bundle bundle = new Bundle();
                bundle.putBoolean("isRegisteredAccount", isRegisteredAccount);
                accountFragment.setArguments(bundle);
                selectedFragment(accountFragment);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public void setUpPLanModels() {
        //uchwyt do bazy danych z dokumentami Firebase Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("plans")
                .addSnapshotListener((value, error) -> {
                    assert value != null;
                    for (DocumentChange docChange : value.getDocumentChanges()) {
                        if (docChange.getType() == DocumentChange.Type.ADDED) {
                            ArrayList<ExerciseModel> exerciseModels = new ArrayList<>();
                            HashMap<String, Object> map = (HashMap<String, Object>) docChange.getDocument().getData();
                            String planName = (String) map.get("planName");
                            ArrayList<HashMap> exerciseModels1 = (ArrayList<HashMap>) map.get("exercises");
                            for (int i = 0; i < exerciseModels1.size(); i++) {
                                Log.d(TAG, "setUpPLanModels: " + exerciseModels1.get(0));
                                String exerciseName = (String) exerciseModels1.get(i).get("exerciseName");
                                Long sets = (Long) exerciseModels1.get(i).get("sets");
                                DocumentReference exercise = (DocumentReference) exerciseModels1.get(i).get("exercise");
                                String time = (String) exerciseModels1.get(i).get("time");
                                exerciseModels.add(new ExerciseModel(exerciseName, exercise, sets.intValue(), time));
                            }
                            planModels.add(new PlanModel(planName, exerciseModels));
                        }
                    }
                });
    }
}


//    public void setUpPLanModels1() {
//        Log.d(MotionEffect.TAG, "onEvent: Wykonalem set up plan");
//        //uchwyt do bazy danych z dokumentami Firebase Firestore
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("plans")
//                .addSnapshotListener((value, error) -> {
//                    assert value != null;
//                    for (DocumentChange docChange : value.getDocumentChanges()) {
//                        if (docChange.getType() == DocumentChange.Type.ADDED) {
//                            ArrayList<ExerciseModel> exerciseModels = new ArrayList<>();
//                            db.collection("plans").document(docChange.getDocument().getId()).collection("exercises")
//                                    .addSnapshotListener((value1, error1) -> {
//                                        assert value1 != null;
//                                        for (DocumentChange exerDocChange : value1.getDocumentChanges()) {
//                                            if (exerDocChange.getType() == DocumentChange.Type.ADDED) {
//                                                DocumentReference ref = exerDocChange.getDocument().getDocumentReference("exercise");
//                                                assert ref != null;
//                                                ref.addSnapshotListener((value11, error11) -> {
//                                                    assert value11 != null;
//                                                    String exerciseName = value11.getString("name");
//                                                    Long sets = exerDocChange.getDocument().getLong("sets");
//                                                    String time = exerDocChange.getDocument().getString("time");
//                                                    assert sets != null;
//                                                    exerciseModels.add(new ExerciseModel(exerciseName, ref, sets.intValue(), time));
//
//                                                    //wypelnienie listy planow, ktore nie sa puste i nie maja duplikatow
//                                                    boolean duplicateExists = false;
//                                                    String name = docChange.getDocument().getString("planName");
//                                                    for (PlanModel planModel: planModelsNonEmpty){
//                                                        if (Objects.equals(planModel.getPlanName(), name)) {
//                                                            duplicateExists = true;
//                                                            break;
//                                                        }
//                                                    }
//                                                    if (!duplicateExists){
//                                                        PlanModel p = new PlanModel(name, exerciseModels);
//                                                        planModelsNonEmpty.add(p);
//                                                    }
//                                                });
//
//                                            }
//
//                                        }
//
//                                        //wypelnienie listy wszystkich planow
//                                        planModels.add(new PlanModel(docChange.getDocument().getString("planName"), exerciseModels));
////                                        Log.d(TAG, "onEvent: "+planModels.get(0).getExerciseModel().get(0).getSets());
//                                    });
//                        }
//                    }
//
//                });
//    }