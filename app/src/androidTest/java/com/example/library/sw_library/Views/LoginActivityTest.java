package com.example.library.sw_library.Views;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;

import com.example.library.sw_library.R;

import android.content.Context;
import android.content.Intent;

import static org.junit.Assert.*;

public class LoginActivityTest {

    private Button signinButton;
    private EditText emailView;
    private EditText passwordView;


    @Before
    public void setUp() {
        View view;
        emailView = view.findViewById(R.id.email);
        passwordView = view.findViewById(R.id.pwd);

        signinButton = view.findViewById(R.id.signin_button);
    }

    @Test
    public void signInSimulation() {

    }

}