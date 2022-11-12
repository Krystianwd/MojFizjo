package com.example.mojfizjo;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    BottomNavigationView boottomNavigationview;
    FrameLayout frameLayout;
    DrawerLayout drawerLayout;
    //autentykacja
    FirebaseAuth mAuth;

    String currentFragment = null;

    MainPlanRecyclerViewAdapter adapter;

    public ArrayList<PlanModel> planModels = new ArrayList<>();

    //regex e-mail uzywany w Firebase Auth
    public String emailRegex = ".+@.+\\..+";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpPLanModels();
        //String[] mTestArray;
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode(getResources().getString(R.string.jezyk));

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        boottomNavigationview = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.frame_layout);
        adapter = new MainPlanRecyclerViewAdapter(this, planModels, this);
        PlansFragment plansFragment = new PlansFragment(planModels, adapter);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        boottomNavigationview.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    HomeFragment homeFragment = new HomeFragment();
                    selectedFragment(homeFragment);
                    currentFragment = "home";
                    break;
                case R.id.plans:
                    selectPlanFragment(plansFragment);
                    currentFragment = "plans";
                    break;
                case R.id.workout:
                    WorkoutFragment workoutFragment = new WorkoutFragment(planModels);
                    selectedFragment(workoutFragment);
                    currentFragment = "workout";
                    break;
                case R.id.exercises:
                    ExercisesFragment exercisesFragment = new ExercisesFragment();
                    selectedFragment(exercisesFragment);
                    currentFragment = "exercises";
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentFragment", currentFragment);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentFragment = savedInstanceState.getString("currentFragment");
        switch(currentFragment){
            case "home":
                HomeFragment homeFragment = new HomeFragment();
                selectedFragment(homeFragment);
                break;
            case "plans":
                PlansFragment plansFragment = new PlansFragment(planModels, adapter);
                selectPlanFragment(plansFragment);
                break;
            case "workout":
                WorkoutFragment workoutFragment = new WorkoutFragment(planModels);
                selectedFragment(workoutFragment);
                break;
            case "exercises":
                ExercisesFragment exercisesFragment = new ExercisesFragment();
                selectedFragment(exercisesFragment);
                break;
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
        fragmentTransaction.addToBackStack(null).commit();
    }

    public void selectPlanFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, "plansFragment");
        fragmentTransaction.addToBackStack(null).commit();
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
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
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
                            ArrayList<HashMap> exerciseModels1 = new ArrayList<>();
                            HashMap<String, Object> map = (HashMap<String, Object>) docChange.getDocument().getData();
                            String planName = (String) map.get("planName");
                            exerciseModels1 = (ArrayList<HashMap>) map.get("exercises");
                            Log.d(TAG, "setUpPLanModels: "+planName);
                            for (int i = 0; i < Objects.requireNonNull(exerciseModels1).size(); i++) {
                                Log.d(TAG, "setUpPLanModels: " + exerciseModels1.get(0));
                                String exerciseName = (String) exerciseModels1.get(i).get("exerciseName");
                                Long sets = (Long) exerciseModels1.get(i).get("sets");
                                DocumentReference exercise = (DocumentReference) exerciseModels1.get(i).get("exercise");
                                String time = (String) exerciseModels1.get(i).get("time");
                                assert sets != null;
                                exerciseModels.add(new ExerciseModel(exerciseName, exercise, sets.intValue(), time));
                            }

                            //sprawdzenie id uzytkownika
                            String planUID = "";
                            String userUID = "";
                            if(map.get("userID") != null) planUID = (String)map.get("userID");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) userUID = currentUser.getUid();

                            //dodanie do listy
                            if(Objects.equals(planUID, userUID)) planModels.add(new PlanModel(planName, exerciseModels));
                        }
                    }
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_logout:
                drawerLayout.closeDrawer(GravityCompat.START);

                //wylogowanie
                mAuth.signOut();

                //ukrycie menu
                this.hideMenus();

                //przelaczenie widoku na fragment logowania
                Fragment fragment = new LoginFragment();
                this.selectedFragment(fragment);
                break;
            case R.id.nav_change_password:
                    moveToAccountFragment("change_password");
                break;
            case R.id.nav_delete_acc:
                moveToAccountFragment("delete_account");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return true;
    }

    public void moveToAccountFragment(String mode){
        drawerLayout.closeDrawer(GravityCompat.START);
        accountFragment accountFragment = new accountFragment();

        //sprawdzenie, czy uzytkownik istnieje i ma email (nie anonimowy)
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                //przeniesienie na strone konta
                Bundle bundle = new Bundle();
                bundle.putString("accountFragmentMode", mode);
                accountFragment.setArguments(bundle);
                selectedFragment(accountFragment);
            }
        }
    }
}
