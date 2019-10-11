package com.kangmicin.hotmovie.ui.main

import androidx.lifecycle.ViewModel
import com.kangmicin.hotmovie.repository.TvRepository

class TvViewModel (private val tvShowRepository: TvRepository) : ViewModel() {

    fun getTvs() = tvShowRepository.getTvShows()

    fun loadTvShows() = tvShowRepository.loadServiceTvShow()
}