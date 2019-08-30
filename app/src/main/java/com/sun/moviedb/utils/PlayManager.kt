package com.sun.moviedb.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.sun.moviedb.R
/**
 * Created by nguyenxuanhoi on 2019-08-26.
 * @author nguyen.xuan.hoi@sun-asterisk.com
 */
class PlayManager {
    private lateinit var player: SimpleExoPlayer
    private var contentPosition: Long = 0
    fun init(context: Context, playerView: PlayerView, contentUrl: String) {
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.getString(R.string.app_name))
        )
        playerView.player = player

        val videoSource = buildMediaSource(context,Uri.parse(contentUrl), dataSourceFactory)
        player.seekTo(contentPosition)
        player.prepare(videoSource)
        player.playWhenReady = true

    }

    private fun buildMediaSource(
        context: Context,
        uri: Uri,
        dataSourceFactory: DataSource.Factory
    ): MediaSource {
        when (val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> {
                val dataSourceWithBandwidthMeter = DefaultDataSourceFactory(
                    context,
                    Util.getUserAgent(context, context.getString(R.string.app_name)),
                    DefaultBandwidthMeter()
                )
                val mediaSource = DashMediaSource.Factory(
                    DefaultDashChunkSource.Factory(dataSourceWithBandwidthMeter),
                    dataSourceFactory
                ).createMediaSource(uri)
                return mediaSource
            }
            C.TYPE_HLS -> return HlsMediaSource(uri, dataSourceFactory, null, null)
            C.TYPE_OTHER -> {
                return ExtractorMediaSource(
                    uri,
                    dataSourceFactory,
                    DefaultExtractorsFactory(),
                    null,
                    null
                )
            }
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    fun reset() {
        if (::player.isInitialized) {
            contentPosition = player.contentPosition
            player.release()
        }
    }

    fun release() {
        if (::player.isInitialized) {
            player.release()
        }
    }
}
