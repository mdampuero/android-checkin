package cl.clickgroup.checkin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import cl.clickgroup.checkin.fragments.CheckInFragment
import cl.clickgroup.checkin.fragments.ScanFragment
import cl.clickgroup.checkin.fragments.SettingFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Opcional: Forzar el modo oscuro o claro
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Forzar modo claro
        // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Forzar modo oscuro

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Set initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ScanFragment())
                .commit()
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
}