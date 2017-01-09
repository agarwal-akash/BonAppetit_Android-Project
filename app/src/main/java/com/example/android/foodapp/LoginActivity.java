package com.example.android.foodapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by Akash on 19-11-2016.
 */

public class LoginActivity extends AppCompatActivity{
    //components
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button google_signin;
    private EditText name;
    private EditText number;
    private TextView about_us;
    private ImageView about_us_image;
    private Button google_signout;
    //constants
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    //variables
    private String name_txt;
    private String phone_no;
    private Long contact_number;
    private RelativeLayout mRelativeLayout;
    private boolean snackSet=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Animation for buttons
        final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        mRelativeLayout=(RelativeLayout)findViewById(R.id.relative_layout);

        //get auth instance
        mAuth=FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        name=(EditText)findViewById(R.id.name);
        number=(EditText)findViewById(R.id.mob);

        //if(!name_txt.equals(""))name_set=true;
        //if(!phone_no.equals("")){ mob_set=true; contact_number= Long.parseLong(phone_no); }
        about_us=(TextView)findViewById(R.id.about_us);
        about_us_image=(ImageView)findViewById(R.id.question_logo);
        //about_us.startAnimation(animBounce);
        about_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs();
            }
        });
        about_us_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutUs();
            }
        });
        //Set snackbar on Name Editext to force user to read aboutUs :P
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(snackSet==false)
                {
                    Snackbar snackbar = Snackbar
                            .make(mRelativeLayout, "Please read about us before you start", Snackbar.LENGTH_INDEFINITE)
                            .setAction("ABOUT US", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    aboutUs();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                    snackSet=true;
                }
            }
        });
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("147176347106-08pg2d1sgbmq9g5lcp7qp8bpfptso3uo.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        google_signin=(Button)findViewById(R.id.sign_in);
        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                name_txt=name.getText().toString();
                phone_no=number.getText().toString();
                if(phone_no.length()!=0) {
                    contact_number = Long.parseLong(phone_no);
                }
                //Toast.makeText(getApplicationContext(),name_txt,Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(),phone_no,Toast.LENGTH_SHORT).show();
                if(name_txt.length()!=0 && phone_no.length()!=0)
                {
                    signIn();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please fill in the details",Toast.LENGTH_SHORT).show();
                }
            }
        });
        google_signout=(Button)findViewById(R.id.sign_out);
        google_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animAlpha);
                signout();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signout()
    {

            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Toast.makeText(getApplicationContext(),"Successfully signed out..",Toast.LENGTH_SHORT).show();
                        }
                    });
                FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(),"Successfully signed out..",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            Toast.makeText(this,"signed in successfully..",Toast.LENGTH_SHORT).show();
            Intent maps=new Intent(this,MapsActivity.class);
            Bundle b=new Bundle();
            b.putString("name",name_txt);
            b.putLong("contact_no",contact_number);
            maps.putExtras(b);
            Toast.makeText(this,"acc. name: "+acct.toString(),Toast.LENGTH_SHORT).show();
            startActivity(maps);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
            //Toast.makeText(this,"error in signing in...",Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar
                    .make(mRelativeLayout, "Error signing in", Snackbar.LENGTH_LONG)
                    .setAction("TRY AGAIN", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });

            snackbar.show();
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            //Toast.makeText(LoginActivity.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            Snackbar st = Snackbar
                                    .make(mRelativeLayout, "Authentication Failed", Snackbar.LENGTH_LONG)
                                    .setAction("TRY AGAIN", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                                            startActivity(i);
                                        }
                                    });

                            st.show();
                        }
                        // ...
                    }
                });
    }
    private void aboutUs()
    {
        Intent i=new Intent(this,AboutUS.class);
        startActivity(i);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
