package net.veldor.oblepiha_kotlin.view

import android.app.KeyguardManager
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.os.PowerManager.WakeLock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.AlertViewModel

class AlarmActivity : AppCompatActivity() {
    private var vibrator: Vibrator? = null
    private var r: Ringtone? = null
    private lateinit var mMyViewModel: AlertViewModel
    private lateinit var mWakeLock: WakeLock
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMyViewModel = ViewModelProvider(this).get(AlertViewModel::class.java)
        val win = window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            km.requestDismissKeyguard(this, null)
        } else {
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            win.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        val pm = getSystemService(
            POWER_SERVICE
        ) as PowerManager
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "myapp:surprise") as WakeLock
        mWakeLock.acquire(360000)
        setContentView(R.layout.activity_alarm)
        App.instance.isAlertWindowShowed = true
        setupUI()

        // вибрирую
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mVibratePattern = longArrayOf(0, 400, 800, 600, 800, 800, 800, 1000)
            val mAmplitudes = intArrayOf(0, 255, 0, 255, 0, 255, 0, 255)
            vibrator!!.vibrate(VibrationEffect.createWaveform(mVibratePattern, mAmplitudes, 1))
        } else {
            //deprecated in API 26
            vibrator!!.vibrate(500)
        }
        val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        r = RingtoneManager.getRingtone(applicationContext, notification)
        r?.play()
    }

    private fun setupUI() {
        val mAlertTextView = findViewById<TextView>(R.id.alert_text)
        if (mAlertTextView != null) {
            val info: ApiCurrentStatusResponse? = App.instance.mCurrentStatusResponse.getValue()
            if (info != null) {
                val sb = StringBuilder()
                sb.append("Тревога!\n")
                sb.append("Участок ")
                sb.append(info.cottage_number)
                sb.append("\n")
                sb.append("Контакт ")
                sb.append(info.perimeter_state)
                sb.append("\n")
                sb.append("Получено в ")
                sb.append(GrammarHandler.timeToString(info.connection_time))
                mAlertTextView.text = sb
            }
        }
        val mAlertAcceptedButtonView = findViewById<Button>(R.id.alerts_accepted)
        mAlertAcceptedButtonView.setOnClickListener { v: View? ->
            Log.d("surprise", "Alarm onClick 67: alert accepted")
            // отправлю уведомление о том, что тревоги обработаны и закрою окно
            mMyViewModel.sendAlertsAccepted()
            finish()
        }
        val callSecurityButtonView = findViewById<Button>(R.id.call_security)
        callSecurityButtonView.setOnClickListener { v: View? ->
            val intent =
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+79503690182"))
            startActivity(intent)
            // отправлю уведомление о том, что тревоги обработаны и закрою окно
            mMyViewModel.sendAlertsAccepted()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator!!.cancel()
        mWakeLock.release()
        App.instance.isAlertWindowShowed = false
        r!!.stop()
    }

    companion object {
        const val RAW_DATA_EXTRA = "raw data"
    }
}