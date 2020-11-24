package com.example.vangogh;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

//import utils.View;




public class NavigationDrawer extends FragmentActivity
{
//    private navDrawerAdapter = new NavigationDrawerAdapter();

    protected Toolbar toolbar;

    private void onItemClick(int pos, NavItem item)
    {

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.navigation_drawer_layout, container, false);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer_layout);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        return ;
    }


    public static class NavItem
    {
        private int id, resourceId;
        private String itemName;

        public NavItem(int id, int resourceId, String itemName)
        {
            this.id = id;
            this.resourceId = resourceId;
            this.itemName = itemName;
        }

        public int getId() {
            return id;
        }

        public int getResourceId() {
            return resourceId;
        }

        public String getItemName() {
            return itemName;
        }
    }
}
