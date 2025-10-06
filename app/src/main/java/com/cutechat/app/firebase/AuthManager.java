package com.cutechat.app.firebase;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.cutechat.app.models.User;
import com.cutechat.app.ui.main.MainActivity;

public class AuthManager {
    private static final String TAG = "AuthManager";
    private static AuthManager instance;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private GoogleSignInClient googleSignInClient;
    private Context context;

    private AuthManager(Context context) {
        this.context = context;
        this.auth = FirebaseConfig.getInstance().getAuth();
        this.firestore = FirebaseConfig.getInstance().getFirestore();
        setupGoogleSignIn();
    }

    public static synchronized AuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new AuthManager(context);
        }
        return instance;
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("YOUR_WEB_CLIENT_ID") // Replace with your actual web client ID
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }

    public void signUpWithEmail(String email, String password, String name, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Create user profile in Firestore
                            createUserProfile(user, name, callback);
                        }
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void signInWithEmail(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public void signInWithGoogle(Intent data, AuthCallback callback) {
        try {
            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
            
            auth.signInWithCredential(credential)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                // Check if user profile exists
                                checkUserProfile(user, callback);
                            }
                        } else {
                            callback.onError(task.getException().getMessage());
                        }
                    });
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed", e);
            callback.onError("Google sign in failed");
        }
    }

    private void checkUserProfile(FirebaseUser user, AuthCallback callback) {
        firestore.collection("users").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            callback.onSuccess();
                        } else {
                            // Create new user profile
                            createUserProfile(user, user.getDisplayName(), callback);
                        }
                    } else {
                        callback.onError("Failed to check user profile");
                    }
                });
    }

    private void createUserProfile(FirebaseUser user, String name, AuthCallback callback) {
        User userProfile = new User(
                user.getUid(),
                user.getEmail(),
                name != null ? name : "User",
                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
        );

        firestore.collection("users").document(user.getUid())
                .set(userProfile.toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError("Failed to create user profile");
                    }
                });
    }

    public void signOut(AuthCallback callback) {
        auth.signOut();
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            callback.onSuccess();
        });
    }

    public void resetPassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                });
    }

    public interface AuthCallback {
        void onSuccess();
        void onError(String error);
    }
}