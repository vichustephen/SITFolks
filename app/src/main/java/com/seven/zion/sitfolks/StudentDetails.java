package com.seven.zion.sitfolks;

/**
 * Created by Stephen on 08-Mar-17.
 */

public class StudentDetails {

    private String rollno;
    private String department;
    private String name;
    private String admin;

    public StudentDetails()
    {

    }
    public StudentDetails(String name ,String rollno, String department,String admin){
        this.name=name;
        this.department=department;
        this.rollno=rollno;
        this.admin = admin;
    }
    public String getRollno(){
        return rollno;
    }

    public String getDepartment() {
        return department;
    }

    public String getName() {
        return name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRollno(String rollno) {
        this.rollno = rollno;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
