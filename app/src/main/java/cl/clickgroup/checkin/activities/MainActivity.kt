package cl.clickgroup.checkin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.R
import cl.clickgroup.checkin.fragments.CheckInFragment
import cl.clickgroup.checkin.fragments.ScanFragment
import cl.clickgroup.checkin.fragments.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var needSync: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        needSync = intent.getBooleanExtra("needSync", false)

        if (savedInstanceState == null) {
            if (needSync) {
                showCheckInFragment()
                bottomNavigationView.selectedItemId = R.id.nav_search
            } else {
                showDefaultFragment()
                bottomNavigationView.selectedItemId = R.id.nav_home
            }
        }


        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> ScanFragment()
                R.id.nav_search -> CheckInFragment()
                R.id.nav_profile -> SettingFragment()
                else -> ScanFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }

    private fun showCheckInFragment() {
        val checkInFragment = CheckInFragment().apply {
            arguments = Bundle().apply {
                putBoolean("needSync", needSync)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, checkInFragment)
            .commit()
    }

    private fun showDefaultFragment() {

        val defaultFragment = ScanFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, defaultFragment)
            .commit()
    }
}
