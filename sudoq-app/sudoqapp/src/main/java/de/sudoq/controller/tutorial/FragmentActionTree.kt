package de.sudoq.controller.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.sudoq.R

//import android.support.v7.widget.RecyclerView;
class FragmentActionTree : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, group: ViewGroup?, saved: Bundle?): View? {
        return inflater.inflate(R.layout.tutorial_actiontree, group, false)
    }
}