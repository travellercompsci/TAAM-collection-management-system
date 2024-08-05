package com.example.taamcms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

@RunWith(MockitoJUnitRunner.class)
public class LoginScreenTests {

    @Mock
    LoginScreenView view;

    @Mock
    LoginScreenModule model;

    @Captor
    ArgumentCaptor<CredentialValidationCallback> callbackCaptor;

    @Test
    public void testPresenterEmptyUsername(){
        when(view.getUsername()).thenReturn("");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        verify(view).showUsernameErr("Please enter a username.");
    }

    @Test
    public void testPresenterEmptyPassword(){
        when(view.getUsername()).thenReturn("abc");
        when(view.getPassword()).thenReturn("");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        verify(view).showPasswordErr("Please enter a password.");
    }

    @Test
    public void testPresenterInvalidUsername(){
        when(view.getUsername()).thenReturn("random");
        when(view.getPassword()).thenReturn("easy");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        InOrder order = inOrder(model, view);
        order.verify(model).isValidCredentials(anyString(), anyString(), callbackCaptor.capture());
        callbackCaptor.getValue().invalidUsername();
        order.verify(view).showUsernameErr("Invalid username");
    }

    @Test
    public void testPresenterInvalidPassword(){
        when(view.getUsername()).thenReturn("user1");
        when(view.getPassword()).thenReturn("easy");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        InOrder order = inOrder(model, view);
        order.verify(model).isValidCredentials(anyString(), anyString(), callbackCaptor.capture());
        callbackCaptor.getValue().invalidPassword();
        order.verify(view).showPasswordErr("Invalid password");
    }

    @Test
    public void testPresenterDatabaseError(){
        when(view.getUsername()).thenReturn("user1");
        when(view.getPassword()).thenReturn("easy");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        InOrder order = inOrder(model, view);
        order.verify(model).isValidCredentials(anyString(), anyString(), callbackCaptor.capture());
        callbackCaptor.getValue().databaseError("err");
        order.verify(view).showGeneralErr("A database error occurred: err");
    }

    @Test
    public void testisValidCredentialsCorrectUsernameArgument(){
        when(view.getUsername()).thenReturn("user1");
        when(view.getPassword()).thenReturn("easy");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(model).isValidCredentials(captor.capture(), anyString(), callbackCaptor.capture());
        assertEquals(captor.getValue(), "user1");
    }

    @Test
    public void testisValidCredentialsCorrectPasswordArgument(){
        when(view.getUsername()).thenReturn("user1");
        when(view.getPassword()).thenReturn("easy");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(model).isValidCredentials(anyString(), captor.capture(), callbackCaptor.capture());
        assertEquals(captor.getValue(), "easy");
    }

    @Test
    public void testPresenterSuccessfulLogin() {
        when(view.getUsername()).thenReturn("user1");
        when(view.getPassword()).thenReturn("1234");
        LoginScreenPresenter presenter = new LoginScreenPresenter(view, model);
        presenter.handleLogin();
        InOrder order = inOrder(model, view);
        order.verify(model).isValidCredentials(anyString(), anyString(), callbackCaptor.capture());
        callbackCaptor.getValue().isSuccessful();
        order.verify(view).loginSuccess();
    }

}