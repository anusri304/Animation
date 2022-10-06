package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 75.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonBackgroundColor = 0
    private var buttonTextColor = 0
    private var buttonLoadingColor = 0
    private var buttonCircleColor = 0
    private var buttonTextStr = ""
    private var percentageAnimation = 0

    private val valueAnimator = ValueAnimator.ofInt(0,360).setDuration(2500)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonTextStr = resources.getString(R.string.button_loading)

                valueAnimator.start()

            }
            ButtonState.Completed -> {
                buttonTextStr = resources.getString(R.string.button_name)
                valueAnimator.cancel()

                percentageAnimation = 0
            }
        }
        invalidate()

    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs,R.styleable.LoadingButton)
        buttonBackgroundColor = typedArray.getColor(R.styleable.LoadingButton_backgroundColor, 0)
        buttonTextColor = typedArray.getColor(R.styleable.LoadingButton_textColor, 0)
        buttonLoadingColor = typedArray.getColor(R.styleable.LoadingButton_loadingColor, 0)
        buttonCircleColor = typedArray.getColor(R.styleable.LoadingButton_circleColor, 0)

        buttonState = ButtonState.Completed

        initAnimation()
    }

    private fun initAnimation() {
       valueAnimator.addUpdateListener {
           percentageAnimation = it.animatedValue as Int
           invalidate()
       }
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawButton(canvas)

        displayButtonWidthAnimation(canvas)

        drawButtonText(canvas)

        drawLoadingCircle(canvas)


    }

    private fun drawLoadingCircle(canvas: Canvas?) {
        paint.color = buttonCircleColor
        canvas?.drawArc(widthSize - 200f,60f,widthSize - 100f,150f,0f, percentageAnimation.toFloat(), true, paint)
    }

    private fun drawButtonText(canvas: Canvas?) {
        // text
        paint.color = buttonTextColor
        canvas?.drawText(buttonTextStr, widthSize/2.0f, heightSize/2.0f + 20.0f, paint)
    }

    private fun displayButtonWidthAnimation(canvas: Canvas?) {
        paint.color = buttonLoadingColor
        canvas?.drawRect(0f, 0f, widthSize * percentageAnimation/360f, heightSize.toFloat(), paint)
    }

    private fun drawButton(canvas: Canvas?) {
        paint.color = buttonBackgroundColor
        canvas?.drawRect(0f,0f,widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}