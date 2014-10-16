package com.ipole.mcfly;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.ipole.mcfly.util.BuildingInstructionsPagerAdapter;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class BuildingInstructions extends FragmentActivity  {
	// When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
	BuildingInstructionsPagerAdapter mBuildingInstructionsPagerAdapter;
    ViewPager mViewPager;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building_instructions);
        
        // create ViewPager
        mBuildingInstructionsPagerAdapter =  new BuildingInstructionsPagerAdapter(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mBuildingInstructionsPagerAdapter);
    }
}
