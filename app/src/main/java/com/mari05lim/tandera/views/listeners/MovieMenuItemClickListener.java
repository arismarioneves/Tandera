package com.mari05lim.tandera.views.listeners;

import android.view.MenuItem;
import android.widget.PopupMenu;

import com.mari05lim.tandera.R;
import com.mari05lim.tandera.model.Display;
import com.mari05lim.tandera.model.entities.MovieWrapper;
import com.mari05lim.tandera.model.entities.TrailerWrapper;
import com.mari05lim.tandera.model.util.MoviesCollections;

/**
 * DEV Mari05liM
 */
public class MovieMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    private final MovieWrapper movie;
    private final Display display;

    public MovieMenuItemClickListener(MovieWrapper movie, Display display) {
        this.movie = movie;
        this.display = display;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (display != null) {

            switch (item.getItemId()) {
                case R.id.menu_action_share: {
                    if (movie.getTmdbId() != null) {
                        display.shareMovie(movie.getTmdbId(), movie.getTitle());
                    }
                    return true;
                }
                case R.id.menu_action_trailer: {
                    if (movie != null && !MoviesCollections.isEmpty(movie.getTrailers())) {
                        playTrailer(movie);
                    }
                    return true;
                }
                case R.id.menu_action_web_search: {
                    if (movie.getTitle() != null) {
                        display.performWebSearch(movie.getTitle());
                    }
                    return true;
                }
                default:
                    return false;
            }
        }
        return false;
    }

    public void playTrailer(MovieWrapper movie) {
        if (display != null) {
            for (TrailerWrapper trailer : movie.getTrailers()) {
                if (trailer.getSource().equals(TrailerWrapper.Source.YOUTUBE)) {
                    display.playYoutubeVideo(trailer.getId());
                    return;
                }
            }
        }
    }

}