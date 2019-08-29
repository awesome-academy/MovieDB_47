package com.sun.moviedb.data.remote

import androidx.lifecycle.LiveData
import com.sun.moviedb.base.BaseResponse
import com.sun.moviedb.data.dto.DetailMovieDTO
import com.sun.moviedb.data.dto.GenresDto
import com.sun.moviedb.data.dto.MovieDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteDataSource {
    @GET("genre/movie/list")
    fun getGenres(): LiveData<BaseResponse<GenresDto>>

    @GET("movie/{type}")
    fun getMoviesByCategory(@Path("type") type: String, @Query("page") page: Int): LiveData<BaseResponse<MovieDto>>

    @GET("discover/movie")
    fun getMoviesByGenre(@Query("with_genres") idGenre: Int, @Query("page") page: Int): Single<MovieDto>

    @GET("movie/{movie_id}?append_to_response=credits,videos")
    fun getMovieDetail(@Path("movie_id") id: Int): Single<DetailMovieDTO>
}
