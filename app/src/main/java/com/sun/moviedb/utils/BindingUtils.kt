package com.sun.moviedb.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.sun.moviedb.MovieApplication
import com.sun.moviedb.R
import com.sun.moviedb.data.entity.Movie
import com.sun.moviedb.view.adapter.MovieHomeAdapter

/**
 * Created by nguyenxuanhoi on 2019-08-17.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
object BindingUtils {
    @BindingAdapter("bindTextCategory")
    @JvmStatic
    fun textCategory(textView: TextView, query: String) {
        val context = textView.context
        when (query) {
            CategoryQuery.UP_COMING -> textView.text = context.getString(R.string.title_up_coming)
            CategoryQuery.POPULAR -> textView.text = context.getString(R.string.title_popular)
            CategoryQuery.NOW_PLAYING -> textView.text = context.getString(R.string.title_now_playing)
            CategoryQuery.TOP_RATE -> textView.text = context.getString(R.string.title_top_rate)
        }
    }

    @BindingAdapter("android:url")
    @JvmStatic
    fun bindImageFromUrl(imageView: ImageView, imageUrl: String?) {
        imageUrl?.let {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners( MovieApplication.applicationContext!!.resources.getDimensionPixelSize(R.dimen.dp_8)))
            Glide.with(imageView.context)
                .applyDefaultRequestOptions(requestOptions)
                .load(StringUtils.getImage(it))
                .into(imageView)
        }
    }
    @BindingAdapter("android:text")
    @JvmStatic
    fun bindTextView(textView: TextView,title:String){
            textView.text=title
    }

    @BindingAdapter("movieCategories")
    @JvmStatic
    fun bindCategoryMovies(recycler: RecyclerView, movies: List<Movie>) {
        val adapter = recycler.adapter as? MovieHomeAdapter
        adapter?.submitList(movies)
    }

}
