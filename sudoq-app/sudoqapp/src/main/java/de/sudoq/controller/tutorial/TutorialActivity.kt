/*
 * SudoQ is a Sudoku-App for Adroid Devices with Version 2.2 at least.
 * Copyright (C) 2012  Heiko Klare, Julian Geppert, Jan-Bernhard Kordaß, Jonathan Kieling, Tim Zeitz, Timo Abele
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version. 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.sudoq.controller.tutorial

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import de.sudoq.R
import java.util.*

class TutorialActivity : AppCompatActivity() {
    //private DrawerLayout mDrawerLayout;
    /**
     * {@inheritDoc}
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tutorial2)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab!!.setHomeAsUpIndicator(R.drawable.launcher)
        ab.setDisplayHomeAsUpEnabled(true)
        ab.setDisplayShowTitleEnabled(true)

        //-----------------------------
        //fix for snackbar bug but cant get it casted...
        /*CoordinatorLayout cl = (CoordinatorLayout) findViewById(R.id.main_content);
        CoordinatorLayout.LayoutParams clp = (CoordinatorLayout.LayoutParams) cl.getLayoutParams();
        clp.setBehavior(new AppBarLayoutBehavior());*/

        //(cl.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());
        //((CoordinatorLayout.LayoutParams) findViewById(R.id.main_content).getLayoutParams()).setBehavior(new AppBarLayoutBehavior());
        val coordinatorLayout = findViewById<View>(R.id.main_content) as CoordinatorLayout
        coordinatorLayout.setOnTouchListener { v, event -> true }

        //-----------------------------
        //toolbar

        /*mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }*/
        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        setupViewPager(viewPager)

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return false
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = Adapter(supportFragmentManager)
        adapter.addFragment(FragmentSudoku(), getString(R.string.sf_tutorial_sudoku_title))
        adapter.addFragment(FragmentAssistances(), getString(R.string.sf_tutorial_assistances_title))
        adapter.addFragment(FragmentActionTree(), getString(R.string.sf_tutorial_action_title))
        viewPager.adapter = adapter
    }

    /*private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }*/
    internal class Adapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {
        private val mFragments: MutableList<Fragment> = ArrayList()
        private val mFragmentTitles: MutableList<String> = ArrayList()
        fun addFragment(fragment: Fragment, title: String) {
            mFragments.add(fragment)
            mFragmentTitles.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {
            return mFragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return mFragmentTitles[position]
        }
    }
}