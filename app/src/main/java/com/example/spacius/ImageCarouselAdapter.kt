package com.example.spacius

import android.graphics.Matrix
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition

class ImageCarouselAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImageCarouselAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_image, parent, false)
        return ImageViewHolder(view as ImageView)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageRes = images[position]
        val imageView = holder.imageView

        Glide.with(imageView.context)
            .load(imageRes)
            .into(object : CustomViewTarget<ImageView, android.graphics.drawable.Drawable>(imageView) {
                override fun onResourceReady(resource: android.graphics.drawable.Drawable, transition: Transition<in android.graphics.drawable.Drawable>?) {
                    val viewWidth = imageView.width.toFloat()
                    val viewHeight = imageView.height.toFloat()
                    val drawableWidth = resource.intrinsicWidth.toFloat()
                    val drawableHeight = resource.intrinsicHeight.toFloat()

                    val matrix = Matrix()
                    val scale: Float
                    var dy = 0f

                    // Escala la imagen para que llene el ancho
                    scale = viewWidth / drawableWidth
                    dy = viewHeight - (drawableHeight * scale)

                    matrix.setScale(scale, scale)
                    matrix.postTranslate(0f, dy)

                    imageView.imageMatrix = matrix
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadFailed(errorDrawable: android.graphics.drawable.Drawable?) {
                    // Manejar error
                }

                override fun onResourceCleared(placeholder: android.graphics.drawable.Drawable?) {
                    // Limpiar
                }
            })
    }

    override fun getItemCount(): Int = images.size

    class ImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
}
