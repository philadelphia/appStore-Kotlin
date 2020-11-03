package com.example.installer

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.installer.databinding.ActivityWelcomeBinding
import com.example.installer.utils.DisplayUtil

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isTaskRoot) {
            var intent = Intent()
            var action: String? = intent.action
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                finish()
                return
            }
        }

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var layoutParams: RelativeLayout.LayoutParams =
            (binding.imgIcon.layoutParams) as RelativeLayout.LayoutParams
        layoutParams.topMargin = (DisplayUtil.getWindowHight(this) * 0.3).toInt()
        binding.imgIcon.layoutParams = layoutParams

        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true
        }

        return super.onKeyDown(keyCode, event)
    }
}