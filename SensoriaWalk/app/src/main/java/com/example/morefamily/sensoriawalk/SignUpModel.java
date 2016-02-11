package com.example.morefamily.sensoriawalk;

/**
 * Created by MoreFamily on 12/6/2015.
 */
public class SignUpModel
{
    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String Email;

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public boolean isSkipActivation() {
        return SkipActivation;
    }

    public void setSkipActivation(boolean skipActivation) {
        SkipActivation = skipActivation;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getReturnUrl() {
        return ReturnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        ReturnUrl = returnUrl;
    }

    public String FirstName;
    public String LastName;
    public String Password;
    public boolean SkipActivation;
    public int Status;
    public String ReturnUrl;
}
