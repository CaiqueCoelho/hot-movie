package com.kangmicin.hotmovie

import android.graphics.Color
import android.graphics.Outline
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout
import com.kangmicin.hotmovie.model.Movie
import com.kangmicin.hotmovie.model.Person
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.actor_card.view.*
import kotlinx.android.synthetic.main.content_movie.*
import kotlinx.android.synthetic.main.review_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class MovieActivity : AppCompatActivity() {
    lateinit var movie: Movie
    lateinit var collapseListener: AppBarLayout.OnOffsetChangedListener


    companion object {
        const val MOVIE_KEY = "MOVIE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        setSupportActionBar(toolbar)

        movie = intent.getParcelableExtra(MOVIE_KEY)

        movie_plot.text = movie.plot
        movie_length.text = Utils.getTimeFormat(movie.length)
        movie_director.text = createInfoText("Director", movie.director.name)

        supportActionBar?.title = null

        collapseListener = object:AppBarLayout.OnOffsetChangedListener {
            var preview: Int? = null
            var isHide: Boolean = true
            override fun onOffsetChanged(p0: AppBarLayout?, p1: Int) {

                // escape from idle state
                if (preview != p1) {
                    //collapsed
                    if (p1.absoluteValue == p0!!.totalScrollRange) {
                        supportActionBar?.title = movie.title
                        isHide = false
                    }else {
                        if (!isHide) {
                            supportActionBar?.title = null
                            toolbar?.title = null
                            isHide = true
                        }
                    }

                    preview = p1
                }
            }
        }

        app_bar?.addOnOffsetChangedListener(collapseListener)

        displayTitle()
        displayHeroPoster()
        displayGenres()
        displayMoviePoster()
        displayRatings()
        displayReleaseDate()
        displayTopActor()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayMoviePoster() {
        val rImage = Utils.roundedImage(this, movie.poster, R.dimen.corner)
        movie_poster.setImageDrawable(rImage)
    }

    private fun displayGenres() {
        val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        param.gravity = Gravity.START
        param.marginEnd = resources.getDimensionPixelSize(R.dimen.content_spacing)
        movie.genre.forEach {
            movie_genres.addView(makeGenreView(it), param)
        }
    }

    private fun displayRatings() {
        val  params = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        params.weight = 1F
        movie.rating.forEach {
            val view = layoutInflater.inflate(R.layout.review_item, reviews_layout, false)
            view.rating_tag?.text = it.source
            view.rating_amount?.text = it.amount
            reviews_layout.addView(view, params)
        }
    }

    private fun displayHeroPoster() {
        val identity = Utils.getIdentity(this, movie.poster, Utils.ResType.DRAWABLE)

        movie_poster_hero.clipToOutline = true
        movie_poster_hero.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                outline?.setRoundRect(0, -50, view!!.width, view.height, 50f)
            }

        }

        movie_poster_hero.setImageResource(identity)
    }

    private fun displayTopActor() {
        val param = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        param.gravity = Gravity.START
        param.marginEnd = resources.getDimensionPixelSize(R.dimen.content_spacing)

        movie.actors.forEach {
            movie_actors.addView(makeActorView(it.value, it.key), param)
        }
    }

    private fun displayReleaseDate() {
        val releaseFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)  // date in US Language
        val content = releaseFormat.format(movie.release)

        movie_release.text = createInfoText("Release Date", content)
    }

    private fun displayTitle() {
        val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())
        val year = "( ${yearFormat.format(movie.release)} )"
        val spannable = SpannableStringBuilder("${movie.title} $year")

        spannable.setSpan(
            ForegroundColorSpan(Color.parseColor("#CBFFFFFF")),
            movie.title.length,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        movie_title?.text = spannable
    }

    private fun createInfoText(description: String, content: String): SpannableStringBuilder {
        val infoLabel = "$description: "
        val infoText = SpannableStringBuilder("$infoLabel$content")

        infoText.setSpan(
            StyleSpan(Typeface.BOLD),
            infoLabel.length,
            infoText.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        return infoText
    }

    private fun makeGenreView(genre: String): TextView {

        val textView = TextView(this)
        textView.text = genre
        textView.updatePadding(25, 10, 20, 15)
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setTextColor(Color.WHITE)
        textView.background = getDrawable(R.drawable.round_outline_poster)
        return textView
    }

    private fun makeActorView(role: String, actor: Person): View {
        val view = layoutInflater.inflate(R.layout.actor_card, movie_actors, false)

        actor.profileUrl?.let {

            val drawable = Utils.roundedImage(this, it, R.dimen.corner)
            view.movie_actor_picture?.setImageDrawable(drawable)
        }
        view.movie_actor_name?.text = actor.name
        view.movie_actor_role?.text = role

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        app_bar?.removeOnOffsetChangedListener(collapseListener)
    }
}
