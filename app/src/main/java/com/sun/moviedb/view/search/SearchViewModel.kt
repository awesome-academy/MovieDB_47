package com.sun.moviedb.view.search

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sun.moviedb.data.dto.MovieDto
import com.sun.moviedb.data.remote.RemoteDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by nguyenxuanhoi on 2019-08-25.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class SearchViewModel(
    application: Application,
    val remoteDataSource: RemoteDataSource
) : AndroidViewModel(application) {
    private val _movies = MutableLiveData<MovieDto>()
    val movie
        get() = _movies
    private var _error = MutableLiveData<Throwable>()
    val error
        get() = _error

    @SuppressLint("CheckResult")
    fun searchMovie(inputSearch: String) {
        remoteDataSource.searchMovieByName(inputSearch, FIRST_PAGE)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ _movies.value = it }, { _error.value = it })
    }

    companion object {
        private val FIRST_PAGE = 1
    }
}
