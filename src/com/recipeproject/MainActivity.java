package com.recipeproject;

import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
	
	private LinearLayout nameContainer;
	private LinearLayout addressContainer;
	private LinearLayout parentContainer;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		createNameContainer();
		createAddressContainer();
		createParentContainer();
		setContentView(parentContainer);
	}
	
	private void createNameContainer()
	{
		nameContainer = new LinearLayout(this);
		nameContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		nameContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		TextView nameLabel = new TextView(this);
		nameLabel.setText("Name: ");
		nameContainer.addView(nameLabel);
		
		TextView nameValueLabel = new TextView(this);
		nameValueLabel.setText("Erin Victoria Hamalainen");
		nameContainer.addView(nameValueLabel);
	}
	
	private void createAddressContainer()
	{
		addressContainer = new LinearLayout(this);
		addressContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addressContainer.setOrientation(LinearLayout.VERTICAL);
		
		TextView addressLabel = new TextView(this);
		addressLabel.setText("Address: ");
		
		TextView addressValueLabel = new TextView(this);
		addressValueLabel.setText("1212 East Passyunk Avenue");
		
		addressContainer.addView(addressLabel);
		addressContainer.addView(addressValueLabel);
	}
	
	private void createParentContainer()
	{
		parentContainer = new LinearLayout(this);
		parentContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		parentContainer.setOrientation(LinearLayout.VERTICAL);
		
		parentContainer.addView(nameContainer);
		parentContainer.addView(addressContainer);
	}
}
