package net.veldor.oblepiha_kotlin.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.ActivityQrBinding
import net.veldor.oblepiha_kotlin.model.view_models.QRViewModel
import java.util.*


class QrActivity : AppCompatActivity() {


    private lateinit var viewModel: QRViewModel
    private lateinit var binding: ActivityQrBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(QRViewModel::class.java)
        viewModel.loadQR(intent.getIntExtra(BILL_ID, 0).toString())
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.activity_qr, null, false
        )
        setContentView(binding.rootView)
        viewModel.qr.observe(this, {
            if (it != null) {
                binding.contentLoadingProgressView.visibility = View.GONE
                binding.qrView.setImageBitmap(textToImage(it, 300, 300))
            }
        })
    }

    @Throws(WriterException::class, NullPointerException::class)
    private fun textToImage(text: String, width: Int, height: Int): Bitmap? {


        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_RECTANGLE;
        val bitMatrix: BitMatrix = try {
            MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )
        } catch (e: IllegalArgumentException) {
            return null
        }
        val bitMatrixWidth = bitMatrix.width
        val bitMatrixHeight = bitMatrix.height
        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
        val colorWhite = -0x1
        val colorBlack = -0x1000000
        for (y in 0 until bitMatrixHeight) {
            val offset = y * bitMatrixWidth
            for (x in 0 until bitMatrixWidth) {
                pixels[offset + x] = if (bitMatrix[x, y]) colorBlack else colorWhite
            }
        }
        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444)
        bitmap.setPixels(pixels, 0, width, 0, 0, bitMatrixWidth, bitMatrixHeight)
        return bitmap
    }

    companion object {
        const val BILL_ID = "bill id"
    }
}