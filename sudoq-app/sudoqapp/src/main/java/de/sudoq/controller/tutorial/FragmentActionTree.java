package de.sudoq.controller.tutorial;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.sudoq.R;
//import android.support.v7.widget.RecyclerView;

public class FragmentActionTree extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup group, Bundle saved)
	{
		return inflater.inflate(R.layout.tutorial_actiontree, group, false);
	}
	
}