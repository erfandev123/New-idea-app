package com.cutechat.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cutechat.app.R;
import com.cutechat.app.databinding.ActivityAuthBinding;
import com.cutechat.app.firebase.AuthManager;
import com.cutechat.app.ui.main.MainActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    
    private ActivityAuthBinding binding;
    private AuthManager authManager;
    private boolean isLoginMode = true;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        authManager = AuthManager.getInstance(this);
        
        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        
        setupUI();
        setupClickListeners();
    }

    private void setupUI() {
        // Set initial state
        updateUI();
        
        // Setup password visibility toggle
        binding.btnTogglePassword.setOnClickListener(v -> {
            if (binding.etPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                binding.etPassword.setTransformationMethod(null);
                binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility_off);
            } else {
                binding.etPassword.setTransformationMethod(new PasswordTransformationMethod());
                binding.btnTogglePassword.setImageResource(R.drawable.ic_visibility);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnAuth.setOnClickListener(this);
        binding.btnGoogle.setOnClickListener(this);
        binding.btnToggleMode.setOnClickListener(this);
        binding.btnForgotPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_auth) {
            handleAuth();
        } else if (v.getId() == R.id.btn_google) {
            handleGoogleSignIn();
        } else if (v.getId() == R.id.btn_toggle_mode) {
            toggleMode();
        } else if (v.getId() == R.id.btn_forgot_password) {
            handleForgotPassword();
        }
    }

    private void handleAuth() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String name = binding.etName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.etPassword.setError("Password is required");
            return;
        }

        if (!isLoginMode && TextUtils.isEmpty(name)) {
            binding.etName.setError("Name is required");
            return;
        }

        if (password.length() < 6) {
            binding.etPassword.setError("Password must be at least 6 characters");
            return;
        }

        showLoading(true);

        if (isLoginMode) {
            authManager.signInWithEmail(email, password, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    showLoading(false);
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    showError(error);
                }
            });
        } else {
            authManager.signUpWithEmail(email, password, name, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    showLoading(false);
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    showError(error);
                }
            });
        }
    }

    private void handleGoogleSignIn() {
        Intent signInIntent = authManager.getGoogleSignInClient().getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            showLoading(true);
            authManager.signInWithGoogle(data, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess() {
                    showLoading(false);
                    startActivity(new Intent(AuthActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onError(String error) {
                    showLoading(false);
                    showError(error);
                }
            });
        }
    }

    private void handleForgotPassword() {
        String email = binding.etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.setError("Please enter your email first");
            return;
        }

        authManager.resetPassword(email, new AuthManager.AuthCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(AuthActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                showError(error);
            }
        });
    }

    private void toggleMode() {
        isLoginMode = !isLoginMode;
        updateUI();
    }

    private void updateUI() {
        if (isLoginMode) {
            binding.tvTitle.setText("Welcome Back! 👋");
            binding.tvSubtitle.setText("Sign in to continue");
            binding.btnAuth.setText("Sign In");
            binding.btnToggleMode.setText("Don't have an account? Sign Up");
            binding.etName.setVisibility(View.GONE);
            binding.btnForgotPassword.setVisibility(View.VISIBLE);
        } else {
            binding.tvTitle.setText("Create Account ✨");
            binding.tvSubtitle.setText("Join CuteChat today!");
            binding.btnAuth.setText("Sign Up");
            binding.btnToggleMode.setText("Already have an account? Sign In");
            binding.etName.setVisibility(View.VISIBLE);
            binding.btnForgotPassword.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnAuth.setEnabled(!show);
        binding.btnGoogle.setEnabled(!show);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}