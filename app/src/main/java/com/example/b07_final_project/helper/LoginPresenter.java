package com.example.b07_final_project.helper;

import android.text.TextUtils;
import android.widget.Toast;

import com.example.b07_final_project.customer_dashboard.LoginCustomerActivity;
import com.example.b07_final_project.customer_dashboard.SignUpCustomerActivity;
import com.example.b07_final_project.owner_dashboard.Login_Owner;
import com.example.b07_final_project.owner_dashboard.SignUp_Owner;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LoginPresenter implements LoginPresenterInterface {

    // for accessing LoginPresenter

    private static LoginPresenter ID;

    public static LoginPresenter getID() {
        if (ID == null)
            ID = new LoginPresenter();
        return ID;
    }

    private final Model database;

    private LoginPresenter() {
        database = new FirebaseModel();
    }

    // for setting program state (login specifically)

    private final Presenter singleton = Singleton.getID();

    // constants

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PHNNUMBER_REGEX = Pattern.compile("\\d{10}");

    // methods

    @Override
    public Customer loginCustomer(String email, String password) {

        ArrayList<IDobj> customers = database.getAllIDobj(IDobj.CUSTOMER);

        for (IDobj o : customers){
            Customer c = (Customer) o;

            if (c.getEmail().equals(email)){
                if (c.getPassword().equals(password)) {
                    singleton.setCurrentLogin(database.getIDobj(c)); // get a new one so it's not same as return
                    return c;
                } else
                    return null;
            }
        }
        return null;
    }

    @Override
    public Owner loginOwner(String email, String password) {

        ArrayList<IDobj> owners = database.getAllIDobj(IDobj.OWNER);

        for (IDobj o : owners){
            Owner owner = (Owner) o;

            if (owner.getEmail().equals(email)){
                if (owner.getPassword().equals(password)) {
                    singleton.setCurrentLogin(database.getIDobj(owner)); // get a new one so it's not same as return
                    return owner;
                } else
                    return null;
            }
        }
        return null;
    }

    @Override
    public Boolean customerExists(String email){

        ArrayList<IDobj> customers = database.getAllIDobj(IDobj.CUSTOMER);

        for (IDobj o : customers){
            Customer c = (Customer) o;

            if (c.getEmail().equals(email))
                return true;
        }
        return false;
    }

    @Override
    public boolean storeExists(String storeName) {

        ArrayList<IDobj> stores = database.getAllIDobj(IDobj.STORE);

        for (IDobj o : stores){
            Store store = (Store) o;

            if (store.getName().equals(storeName))
                return true;
        }
        return false;
    }

    @Override
    public Boolean ownerExists(String email){

        ArrayList<IDobj> owners = database.getAllIDobj(IDobj.OWNER);

        for (IDobj o : owners){
            Owner owner = (Owner) o;

            if (owner.getEmail().equals(email))
                return true;
        }
        return false;
    }

    @Override
    public Customer newCustomer(String email, String name, String password) {

        Customer customer = (Customer) database.newIDobj(IDobj.CUSTOMER);

        customer.setEmail(email);
        customer.setName(name);
        customer.setPassword(password);

        customer.save();

        singleton.setCurrentLogin(customer);

        return customer;
    }

    @Override
    public Owner newOwner(String email, String name, String password, String phoneNumber, String storename) {

        Owner owner = (Owner) database.newIDobj(IDobj.OWNER);
        Store store = (Store) database.newIDobj(IDobj.STORE);

        database.addRelation(owner, store);

        owner.setEmail(email);
        owner.setPassword(password);
        owner.setName(name);
        owner.setPhoneNumber(phoneNumber);

        owner.save();

        store.setName(storename);
        store.save();

        singleton.setCurrentLogin(owner);

        return owner;
    }

    @Override
    public void customerLoginClicked(MVPview view, String email, String password) {

        //Email
        if (TextUtils.isEmpty(email)) { //email is empty
            Toast.makeText((LoginCustomerActivity) view, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText((LoginCustomerActivity) view, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        //Password
        if (TextUtils.isEmpty(password)) { //password is empty
            Toast.makeText((LoginCustomerActivity) view, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Customer customer = loginCustomer(email, password);

        if (customer == null) {
            Toast.makeText((LoginCustomerActivity) view, "Incorrect customer email or password.", Toast.LENGTH_SHORT).show();
            return;
        }

        ((LoginCustomerActivity)view).emptyTextBoxes();

        // If entered Correctly then Login
        ((LoginCustomerActivity)view).customerLoggedIn();
    }

    @Override
    public void ownerLoginClicked(MVPview view, String email, String password) {

        //Email
        if(TextUtils.isEmpty(email)){ //email is empty
            Toast.makeText((Login_Owner) view, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }       else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()){
            Toast.makeText((Login_Owner) view, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        //Password
        if(TextUtils.isEmpty(password)){ //password is empty
            Toast.makeText((Login_Owner) view, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        Owner owner = loginOwner(email, password);

        if (owner == null) {
            Toast.makeText((Login_Owner) view, "Incorrect owner email or password.", Toast.LENGTH_SHORT).show();
            return;
        }

        ((Login_Owner)view).emptyTextBoxes();

        // If entered Correctly then Login
        ((Login_Owner)view).ownerLoggedIn();
    }


    @Override
    public void customerSignupClicked(MVPview view, String name, String email, String password, String confirmpassword) {

        //check if stings are empty using TextUtils
        //Name
        if(TextUtils.isEmpty(name)){ //email is empty
            Toast.makeText((SignUpCustomerActivity) view, "Please enter Name", Toast.LENGTH_SHORT).show();
            //stop further execution
            return;
        }

        //Email
        if(TextUtils.isEmpty(email)){ //email is empty
            Toast.makeText((SignUpCustomerActivity) view, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()){
            Toast.makeText((SignUpCustomerActivity) view, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        //Password
        if(TextUtils.isEmpty(password)){ //password is empty
            Toast.makeText((SignUpCustomerActivity) view, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;
        }else if(password.length() < 8){
            Toast.makeText((SignUpCustomerActivity) view, "Password must have at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //Confirm Password
        if(!password.equals(confirmpassword)){
            Toast.makeText((SignUpCustomerActivity) view, "Your passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (customerExists(email)){
            // customer already exists
            Toast.makeText((SignUpCustomerActivity) view, "Customer Already Exists", Toast.LENGTH_SHORT).show();
            return;
        }

        ((SignUpCustomerActivity)view).emptyTextBoxes();

        // also logs you in
        newCustomer(email, name, password);

        ((SignUpCustomerActivity)view).customerSignedUp();

    }

    @Override
    public void ownerSignupClicked(MVPview view, String name, String email, String password, String confirmPassword, String phoneNumber, String storeName) {

        //check if stings are empty using TextUtils
        //Store Name
        if ( TextUtils.isEmpty(storeName)){
            Toast.makeText((SignUp_Owner) view, "Enter a store Name", Toast.LENGTH_SHORT).show();
            return;
        } else if(storeExists(storeName)){
            Toast.makeText((SignUp_Owner) view, "Store Name already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        //Email
        if (TextUtils.isEmpty(email)) { //email is empty
            Toast.makeText((SignUp_Owner) view, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        } else if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            Toast.makeText((SignUp_Owner) view, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        } else if(ownerExists(email)){
            Toast.makeText((SignUp_Owner) view, "Owner already exists", Toast.LENGTH_SHORT).show();
            return;
        }
        //Phone
        if (TextUtils.isEmpty(email)) { //email is empty
            Toast.makeText((SignUp_Owner) view, "Please enter Phone number", Toast.LENGTH_SHORT).show();
            return;
        } else if (!VALID_PHNNUMBER_REGEX.matcher(phoneNumber).find()) {
            Toast.makeText((SignUp_Owner) view, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show();
            return;
        }

        //Password
        if (TextUtils.isEmpty(password)) { //password is empty
            Toast.makeText((SignUp_Owner) view, "Please enter Password", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.length() < 8) {
            Toast.makeText((SignUp_Owner) view, "Password must have at least 8 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //Confirm Password
        if (!password.equals(confirmPassword)) {
            Toast.makeText((SignUp_Owner) view, "Your passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // also logs you in
        newOwner(email, name, password, phoneNumber, storeName);

        ((SignUp_Owner) view).emptyTextBoxes();

        ((SignUp_Owner) view).ownerSignedUp();
    }

}