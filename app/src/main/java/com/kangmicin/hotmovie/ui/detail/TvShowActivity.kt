package com.kangmicin.hotmovie.ui.detail

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kangmicin.hotmovie.R
import com.kangmicin.hotmovie.data.Movie
import com.kangmicin.hotmovie.data.Tv
import com.kangmicin.hotmovie.ui.main.TvsViewModel
import com.kangmicin.hotmovie.ui.main.TvsViewModelFactory
import com.kangmicin.hotmovie.utilities.InjectorUtils
import kotlinx.android.synthetic.main.activity_detail.*

class TvShowActivity : DetailActivity() {
    lateinit var tv: Tv
    lateinit var tvFactory: TvsViewModelFactory
    lateinit var tvModel: TvsViewModel

    companion object {
        const val TV_SHOW_KEY = "TV_SHOW"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tvFactory = InjectorUtils.provideTvViewModelFactory()
        tvModel = ViewModelProviders.of(this, tvFactory).get(TvsViewModel::class.java)

        tv = intent.getParcelableExtra(TV_SHOW_KEY)

        tvModel.getTv(tv.id).observe(this, Observer<Tv> {
            if (it != null) {
                Log.i("ThreadNetwork", "" + tv.length + tv.actors.values)
                initDisplay(it)
                toolbar.postInvalidate()
                toolbar.requestLayout()
            }
            Log.i("ThreadNetwork", "getMovie@complete")
        })

        tvModel.loadTv(tv.id)

        initDisplay(tv)
    }

    private fun initDisplay(tv: Tv) {
        displayTitle(tv.name, tv.release)
        displayHeroPoster(tv.backdrop)
        displayGenres(tv.genre)
        displayMoviePoster(tv.poster)
        displayInfoReleaseDate(tv.release)
        displayTopActor(tv.actors)
        displayInfoDirector(R.string.creator_format, tv.creators.map { it.name })
        displayPlot(tv.plot)
        displayInfoLength(tv.length)
    }
}
