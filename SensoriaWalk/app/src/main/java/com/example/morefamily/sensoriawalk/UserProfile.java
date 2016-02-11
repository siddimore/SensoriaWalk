package com.example.morefamily.sensoriawalk;

/**
 * Created by MoreFamily on 11/27/2015.
 */
public class UserProfile {

    private String name;
    private String dateOfBirth;
    private String weight;
    private String height;
    private String gender;

    public UserProfile(String fullName, String dob, String g, String w, String h)
    {
        this.name = fullName;
        this.dateOfBirth = dob;
        this.weight = w;
        this.height = h;
        this.gender = g;
    }

    public String getName()
    {
        String Name = null;
        if(name != null) {
            Name = this.name;
        }
        return Name;
    }

    public String getDateOfBirth()
    {
        String DateOfBirth = null;

        if(this.dateOfBirth != null)
        {
            DateOfBirth = this.dateOfBirth;
        }

        return DateOfBirth;
    }

    public String getWeight()
    {
        String Weight = null;

        if(this.weight != null)
        {
            Weight = this.dateOfBirth;
        }

        return Weight;
    }

    public String getHeight()
    {
        String Height = null;

        if(this.height != null)
        {
            Height = this.dateOfBirth;
        }

        return Height;
    }

    public String getGender()
    {
        String Height = null;

        if(this.height != null)
        {
            Height = this.dateOfBirth;
        }

        return Height;
    }

    public void setlastName(String fullName)
    {
        this.name = fullName;
    }

    public void setDOB(String DOB)
    {
        this.dateOfBirth = DOB;
    }

    public void setWeight(String w)
    {
        this.weight = w;
    }

    public void setHeight(String h)
    {
        this.height = h;
    }

    public void setGender(String g)
    {
        this.gender = g;
    }
}



