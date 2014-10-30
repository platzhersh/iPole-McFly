package com.ipole.mcfly.lego2d.util;
import com.ipole.mcfly.lego2d.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

//Since this is an object collection, use a FragmentStatePagerAdapter,
//and NOT a FragmentPagerAdapter.
public class BuildingInstructionsPagerAdapter extends PagerAdapter {
	 
	private Activity activity;
    private Integer[] imageIds;
    private LayoutInflater inflater;
	
 // constructor
    public BuildingInstructionsPagerAdapter(Activity activity) {
        this.activity = activity;
        // int l = R.drawable.class.getFields().length;
        int l = 6;
        imageIds = new Integer[l];
        Context c = activity.getApplicationContext();
        Resources r = c.getResources();
        for (int i = 1; i <= l; i++) {
        	imageIds[i-1] = r.getIdentifier("lego_page_"+i, "drawable", c.getPackageName());
        }
    }
	
	@Override
	public boolean isViewFromObject(View view, Object object	) {
		// TODO Auto-generated method stub
		return view == ((RelativeLayout) object);
	}
	
	 @Override
	 public int getCount() {
		 return this.imageIds.length;
	 }
	
	 @Override
	 public CharSequence getPageTitle(int position) {
	     return "OBJECT " + (position + 1);
	 }
 
	 @Override
	    public Object instantiateItem(ViewGroup container, int position) {
	        ImageView imgDisplay;
	        Button btnClose;
	  
	        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
	                false);
	  
	        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
	         
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	        //Bitmap bitmap = activity.getApplicationContext().getResources().getI, defType, defPackage)BitmapFactory.decodeFile(imagePaths.get(position), options);
	        //imgDisplay.setImageBitmap(bitmap);
	        Drawable drawable = activity.getApplicationContext().getResources().getDrawable(imageIds[position]);
	        imgDisplay.setImageDrawable(drawable);
	         
	  
	        ((ViewPager) container).addView(viewLayout);
	  
	        return viewLayout;
	    }
	     
	    @Override
	    public void destroyItem(ViewGroup container, int position, Object object) {
	        ((ViewPager) container).removeView((RelativeLayout) object);
	  
	    }
 
}