package com.sun.moviedb.view.detail

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.sun.moviedb.R
import com.sun.moviedb.base.ViewModelBaseFragment
import com.sun.moviedb.data.dto.TrailerDTO
import com.sun.moviedb.databinding.FragmentTrailerBinding
import com.sun.moviedb.view.adapter.TrailerAdapter
import com.sun.moviedb.view.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_trailer.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Created by nguyenxuanhoi on 2019-08-25.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class TrailerFragment : ViewModelBaseFragment<DetailMovieViewModel, FragmentTrailerBinding>() {
    private lateinit var onTrailerClicked: (trailer: TrailerDTO) -> Unit

    fun setOnTrailerClickListener(onButtonClicked: (trailer: TrailerDTO) -> Unit) {
        this.onTrailerClicked = onButtonClicked
    }

    private lateinit var onFirstTrailer: (trailer: String) -> Unit

    fun setOnFirstTrailerListener(onButtonClicked: (trailer: String) -> Unit) {
        this.onFirstTrailer = onButtonClicked
    }

    override val viewModel: DetailMovieViewModel by sharedViewModel(from = { parentFragment!! })

    override val getContentViewId = R.layout.fragment_trailer

    override fun initializeView(savedInstanceState: Bundle?) {
    }

    override fun initializeComponents() {
        val castAdapter = TrailerAdapter {
            if (::onTrailerClicked.isInitialized) onTrailerClicked.invoke(it)
        }
        recyclerTrailer.apply {
            layoutManager = GridLayoutManager(context, 3)
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_4)))
            this.adapter = castAdapter
        }
        viewModel.detailMovie.observe(this, Observer {
            it?.videos?.let { videos ->
                castAdapter.submitList(videos.results)
                if (videos.results.isNotEmpty()) {
                    if (::onFirstTrailer.isInitialized) onFirstTrailer.invoke(videos.results[0].key)
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = TrailerFragment()
    }
}
