package com.example.android.asteroidradar

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.android.asteroidradar.repository.AsteroidApiStatus
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

@BindingAdapter("pictureOfTheDay")
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imageUri = it.toUri().buildUpon().scheme("https").build()
        Picasso.get()
            .load(imageUri)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .placeholder(R.drawable.placeholder_picture_of_day)
            .error(R.drawable.placeholder_picture_of_day)
            .into(imageView)
    }
}

@BindingAdapter("pictureContentDescription")
fun bindPictureContentDescription(imageView: ImageView, title: String?) {
    val context = imageView.context
    if (title != null) {
        imageView.contentDescription = String.format(context.getString(R.string.nasa_picture_of_day_content_description_format), title)
    } else {
        imageView.contentDescription = context.getString(R.string.this_is_nasa_s_picture_of_day_showing_nothing_yet)
    }
}

@BindingAdapter("asteroidApiStatus")
fun bindAsteroidApiStatus(progressBar: ProgressBar, status: AsteroidApiStatus?) {
    progressBar.visibility = when (status) {
        AsteroidApiStatus.LOADING -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("asteroidErrorLoading")
fun bindAsteroidErrorText(textView: TextView, status: AsteroidApiStatus?) {
    textView.visibility = when (status) {
        AsteroidApiStatus.ERROR -> View.VISIBLE
        else -> View.GONE
    }
}

@BindingAdapter("statusIcon")
fun bindAsteroidStatusImage(imageView: ImageView, isHazardous: Boolean) {
    if (isHazardous) {
        imageView.setImageResource(R.drawable.ic_status_potentially_hazardous)
    } else {
        imageView.setImageResource(R.drawable.ic_status_normal)
    }
}

@BindingAdapter("asteroidStatusImage")
fun bindDetailsStatusImage(imageView: ImageView, isHazardous: Boolean) {
    val context = imageView.context
    if (isHazardous) {
        imageView.setImageResource(R.drawable.asteroid_hazardous)
        imageView.contentDescription = context.getString(R.string.potentially_hazardous_asteroid_image)
    } else {
        imageView.setImageResource(R.drawable.asteroid_safe)
        imageView.contentDescription = context.getString(R.string.not_hazardous_asteroid_image)
    }
}

@BindingAdapter("astronomicalUnitText")
fun bindTextViewToAstronomicalUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.astronomical_unit_format), number)
}

@BindingAdapter("kmUnitText")
fun bindTextViewToKmUnit(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_unit_format), number)
}

@BindingAdapter("velocityText")
fun bindTextViewToDisplayVelocity(textView: TextView, number: Double) {
    val context = textView.context
    textView.text = String.format(context.getString(R.string.km_s_unit_format), number)
}
