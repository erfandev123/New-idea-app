package com.cutechat.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cutechat.app.R;
import com.cutechat.app.databinding.ActivityLoginBinding;
import com.cutechat.app.utils.Constants;
import com.cutechat.app.utils.FirebaseUtils;
import com.cutechat.app.utils.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private ActivityLoginBinding binding;
    private PreferenceManager preferenceManager;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(this);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        setListeners();
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            if (isValidInput()) {
                loginWithEmail();
            }
        });

        binding.btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        binding.txtCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        });
    }

    private boolean isValidInput() {
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.inputEmail.setError("Email is required");
            binding.inputEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmail.setError("Enter a valid email");
            binding.inputEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.inputPassword.setError("Password is required");
            binding.inputPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            binding.inputPassword.setError("Password must be at least 6 characters");
            binding.inputPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void loginWithEmail() {
        showLoading(true);
        String email = binding.inputEmail.getText().toString().trim();
        String password = binding.inputPassword.getText().toString().trim();

        FirebaseUtils.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseUtils.getAuth().getCurrentUser();
                        if (user != null) {
                            loadUserData(user.getUid());
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        showLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseUtils.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseUtils.getAuth().getCurrentUser();
                        if (user != null) {
                            // Check if user exists in database
                            FirebaseUtils.getUsersRef().child(user.getUid())
                                    .get().addOnCompleteListener(dbTask -> {
                                if (dbTask.isSuccessful() && dbTask.getResult().exists()) {
                                    loadUserData(user.getUid());
                                } else {
                                    // New user - redirect to complete profile
                                    Intent intent = new Intent(this, RegisterActivity.class);
                                    intent.putExtra("fromGoogle", true);
                                    intent.putExtra("email", user.getEmail());
                                    intent.putExtra("name", user.getDisplayName());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                    } else {
                        showLoading(false);
                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserData(String userId) {
        FirebaseUtils.getUsersRef().child(userId).get()
                .addOnCompleteListener(task -> {
                    showLoading(false);
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String username = task.getResult().child("username").getValue(String.class);
                        String email = task.getResult().child("email").getValue(String.class);
                        String profileImage = task.getResult().child("profileImageUrl").getValue(String.class);

                        preferenceManager.saveUserData(userId, username, email, profileImage);
                        updateFCMToken(userId);
                        updateOnlineStatus(userId, true);

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateFCMToken(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        preferenceManager.putString(Constants.PREF_FCM_TOKEN, token);
                        FirebaseUtils.getUsersRef().child(userId).child("fcmToken").setValue(token);
                    }
                });
    }

    private void updateOnlineStatus(String userId, boolean online) {
        FirebaseUtils.getUsersRef().child(userId).child("online").setValue(online);
        if (!online) {
            FirebaseUtils.getUsersRef().child(userId).child("lastSeen")
                    .setValue(System.currentTimeMillis());
        }
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!isLoading);
        binding.btnGoogleSignIn.setEnabled(!isLoading);
    }
}
