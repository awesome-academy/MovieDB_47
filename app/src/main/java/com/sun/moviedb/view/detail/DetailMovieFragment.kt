package com.sun.moviedb.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.SparseArray
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.sun.moviedb.R
import com.sun.moviedb.base.BaseFragment
import com.sun.moviedb.base.ViewModelBaseFragment
import com.sun.moviedb.databinding.FragmentDetailBinding
import com.sun.moviedb.utils.PlayManager
import com.sun.moviedb.utils.StringUtils
import kotlinx.android.synthetic.main.fragment_detail.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by nguyenxuanhoi on 2019-08-22.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class DetailMovieFragment : ViewModelBaseFragment<DetailMovieViewModel, FragmentDetailBinding>() {
    private val playManager by lazy {
        PlayManager()
    }
    override val viewModel: DetailMovieViewModel by viewModel()
    private val movie by lazy {
        arguments?.let {
            DetailMovieFragmentArgs.fromBundle(it).movie
        }
    }
    override val getContentViewId = R.layout.fragment_detail

    override fun initializeView(savedInstanceState: Bundle?) {
        movie?.let {
            viewDataBinding.item = movie
        }
        viewDataBinding.lifecycleOwner = this
        viewDataBinding.viewModel = viewModel
    }

    override fun initializeComponents() {
        val adapter = ViewpagerFragmentAdapter(context!!, childFragmentManager)
        viewDataBinding.viewPager.adapter = adapter
        viewDataBinding.viewPager.offscreenPageLimit = 2
        viewDataBinding.tabLayout.setupWithViewPager(viewDataBinding.viewPager)
        initAdapter(adapter)
        movie?.let {
            viewModel.initData(it.id)
            viewModel.getMovieDetailDto()
        }
        viewModel.error.observe(this, Observer {
            handleBusinessException(it)
        })
    }

    private fun initAdapter(adapter: ViewpagerFragmentAdapter) {
        val fragments = ArrayList<Fragment>()
        val fragmentTrailer = TrailerFragment.newInstance()
        fragmentTrailer.setOnTrailerClickListener {
            extractYoutubeUrl(StringUtils.getUrlYoutube(it.key))
        }
        fragmentTrailer.setOnFirstTrailerListener {
            extractYoutubeUrl(StringUtils.getUrlYoutube(it))
        }
        fragments.add(fragmentTrailer)
        adapter.submitData(fragments)
    }
    @SuppressLint("StaticFieldLeak")
    private fun extractYoutubeUrl(url: String) {
        val mExtractor = object : YouTubeExtractor(context!!) {
            override fun onExtractionComplete(sparseArray: SparseArray<YtFile>?, videoMeta: VideoMeta) {
                sparseArray?.let {
                    playManager.release()
                    playManager.init(context!!, playerViewTrailer, sparseArray.get(22).url)
                }
            }
        }
        mExtractor.extract(url, true, true)
    }

    override fun onDestroy() {
        playManager.release()
        super.onDestroy()
    }
}
