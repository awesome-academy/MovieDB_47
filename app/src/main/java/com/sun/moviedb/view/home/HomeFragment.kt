package com.sun.moviedb.view.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.sun.moviedb.MainActivity
import com.sun.moviedb.R
import com.sun.moviedb.base.BaseResponse
import com.sun.moviedb.base.ViewModelBaseFragment
import com.sun.moviedb.constants.Constants
import com.sun.moviedb.data.dto.CategoryDTO
import com.sun.moviedb.data.entity.Movie
import com.sun.moviedb.databinding.FragmentHomeBinding
import com.sun.moviedb.utils.CategoryQuery
import com.sun.moviedb.view.adapter.GenresAdapter
import com.sun.moviedb.view.adapter.HomeAdapter
import com.sun.moviedb.view.widget.BackDropView
import com.sun.moviedb.view.widget.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.layout_menu.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by nguyenxuanhoi on 2019-08-07.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class HomeFragment : ViewModelBaseFragment<HomeViewModel, FragmentHomeBinding>(), View.OnClickListener {

    override val viewModel: HomeViewModel by viewModel()

    override val getContentViewId = R.layout.fragment_home

    override fun initializeView(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setUpToolBar()
    }

    override fun initializeComponents() {
        initGenres()
        initCategory()
    }

    override fun registerListeners() {
        buttonNowPlaying.setOnClickListener(this)
        buttonTopRate.setOnClickListener(this)

    }

    override fun onClick(v: View) {

    }

    override fun onBackPressed(): Boolean {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END)
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val toggleTheme = menu.findItem(R.id.menu_theme)
        toggleDarkTheme(toggleTheme)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_search -> {
            true
        }
        R.id.menu_filter -> {
            drawer.openDrawer(GravityCompat.END)
            true
        }
        else -> super.onOptionsItemSelected(item)

    }

    private fun toggleDarkTheme(toggleTheme: MenuItem) {
        (toggleTheme.actionView as CheckBox?)?.apply {
            setButtonDrawable(R.drawable.ic_dark_theme)
            isChecked = isDarkTheme()
            jumpDrawablesToCurrentState()
            setOnCheckedChangeListener { _, isChecked ->
                postDelayed({
                    AppCompatDelegate.setDefaultNightMode(
                            if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                            else AppCompatDelegate.MODE_NIGHT_NO)
                    (activity as MainActivity).delegate.applyDayNight()
                }, Constants.TIME_DELAY_CHANGE_MODE)
            }
        }
    }

    private fun isDarkTheme(): Boolean {
        return context!!.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun initCategory() {
        val categories = viewModel.getCategories()
        val adapter = HomeAdapter({ category ->
            val directions = HomeFragmentDirections.actHomeToMovieByCategory(category.queryType)
            findNavController().navigate(directions)
        }, { movie ->
            val directions = HomeFragmentDirections.actHomeToDetail(movie)
            findNavController().navigate(directions)
        })
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(SpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.dp_8)))
            this.adapter = adapter
        }
        adapter.submitList(categories)
        categories.forEach { category ->
            when (category.queryType) {
                CategoryQuery.POPULAR -> {
                    observeMovieByCategory(category, adapter, viewModel.moviesPopular)
                }
                CategoryQuery.UP_COMING -> {
                    observeMovieByCategory(category, adapter, viewModel.moviesUpComming)
                }
            }
        }
    }

    private fun observeMovieByCategory(
            category: CategoryDTO,
            adapter: HomeAdapter, data:
            LiveData<BaseResponse<List<Movie>>>
    ) {
        data.observe(this, Observer {
            handlerError(it)
            it.result?.let { movies ->
                category.movies = movies
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initGenres() {
        val adapterGenres = GenresAdapter {
            drawer.closeDrawer(GravityCompat.END)
            findNavController().navigate(R.id.actHomeToMovieByGenres)

        }
        recyclerGenres.apply {
            this.adapter = adapterGenres
        }
        viewModel.genres.observe(this, Observer {
            handlerError(it)
            it.result?.let { genres ->
                adapterGenres.submitList(genres)
            }
        })
    }

    private fun setUpToolBar() {
        (activity as MainActivity).setSupportActionBar(toolBar)
        toolBar.setNavigationOnClickListener(BackDropView(
                context!!, home, AccelerateDecelerateInterpolator(),
                ContextCompat.getDrawable(context!!, R.drawable.ic_menu),
                ContextCompat.getDrawable(context!!, R.drawable.ic_close_menu))
        )
    }
}
