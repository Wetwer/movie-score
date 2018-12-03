package ch.wetwer.moviedbapi.service;

import ch.wetwer.moviedbapi.data.dao.GenreDao;
import ch.wetwer.moviedbapi.data.dao.MovieDao;
import ch.wetwer.moviedbapi.data.dao.TimeDao;
import ch.wetwer.moviedbapi.data.dao.UserDao;
import ch.wetwer.moviedbapi.data.entity.Genre;
import ch.wetwer.moviedbapi.data.entity.Movie;
import ch.wetwer.moviedbapi.data.entity.Serie;
import ch.wetwer.moviedbapi.data.entity.User;
import ch.wetwer.moviedbapi.data.repository.MovieRepository;
import ch.wetwer.moviedbapi.data.repository.SerieRepository;
import ch.wetwer.moviedbapi.model.StartedVideo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wetwer
 * @project movie-db
 */
@Service
public class SearchService {

    private MovieDao movieDao;
    private TimeDao timeDao;
    private GenreDao genreDao;
    private UserDao userDao;
    private MovieRepository movieRepository;
    private SerieRepository serieRepository;

    private DuplicateService duplicateService;

    public SearchService(MovieRepository movieRepository, MovieDao movieDao, TimeDao timeDao, GenreDao genreDao,
                         UserDao userDao, SerieRepository serieRepository, DuplicateService duplicateService) {
        this.movieRepository = movieRepository;
        this.movieDao = movieDao;
        this.timeDao = timeDao;
        this.genreDao = genreDao;
        this.serieRepository = serieRepository;
        this.userDao = userDao;
        this.duplicateService = duplicateService;
    }

    public List<Movie> searchMovies(String searchParam, String orderByParam, String genreParam) {
        List<Movie> movies;
        List<Movie> movies2 = new ArrayList<>();

        switch (orderByParam) {
            case "":
                movies = movieRepository.findMoviesByTitleContainingOrderByPopularityDesc(searchParam);
                break;
            case "title":
                movies = movieRepository.findMoviesByTitleContainingOrderByTitle(searchParam);
                break;
            case "rating":
                movies = movieRepository.findMoviesByTitleContainingOrderByVoteAverageDesc(searchParam);
                break;
            case "year":
                movies = movieRepository.findMoviesByTitleContainingOrderByYearDesc(searchParam);
                break;
            case "recomended":
                movies = movieDao.searchRecomended(searchParam);
                break;
            default:
                movies = movieRepository.findMoviesByTitleContainingOrderByTitle(searchParam);
                break;
        }

        if (genreParam.equals("")) {
            return movies;
        } else {
            for (Movie movie : movies) {
                for (Genre genre : movie.getGenres()) {
                    if (genre.getName().equals(genreParam)) {
                        movies2.add(movie);
                    }
                }
            }
        }
        return movies2;
    }

    public List<Movie> searchMoviesTop(String search, String orderBy) {
        List<Movie> movies;

        switch (orderBy) {
            case "":
                movies = movieRepository.findTop24ByTitleContainingOrderByPopularityDesc(search);
                break;
            case "title":
                movies = movieRepository.findTop24ByTitleContainingOrderByTitle(search);
                break;
            case "rating":
                movies = movieRepository.findTop24ByTitleContainingOrderByVoteAverageDesc(search);
                break;
            case "year":
                movies = movieRepository.findTop24ByTitleContainingOrderByYearDesc(search);
                break;
            default:
                movies = movieRepository.findTop24ByTitleContainingOrderByTitle(search);
                break;
        }
        return movies;
    }

    public List<Serie> searchSerie(String search, String genreParam) {
        List<Serie> series = serieRepository.findSeriesByTitleContainingOrderByPopularityDesc(search);
        List<Serie> series2 = new ArrayList<>();

        if (genreParam.equals("")) {
            return series;
        } else {
            for (Serie serie : series) {
                for (Genre genre : serie.getGenres()) {
                    if (genre.getName().equals(genreParam)) {
                        series2.add(serie);
                    }
                }
            }
            return series2;
        }
    }

    public List<StartedVideo> findStartedVideos(User user) {
        return timeDao.getStartedMovies(user);
    }

    public List<Serie> searchSerieTop(String search) {
        return serieRepository.findTop24ByTitleContainingOrderByPopularityDesc(search);
    }


    public List<User> searchUser(String search) {
        return userDao.search(search);
    }

    public List<String> getGenres(GenreSearchType type) {
        List<Genre> genreList = new ArrayList<>();
        switch (type) {
            case ALL:
                genreList = genreDao.getAll();
                break;
            case MOVIE:
                genreList = genreDao.getForMovies();
                break;
            case SERIE:
                genreList = genreDao.getForSeries();
                break;
        }

        List<String> genres = new ArrayList<>();
        for (Genre genre : genreList) {
            genres.add(genre.getName());
        }
        genres = duplicateService.removeStringDuplicates(genres);
        genres.sort(String::compareToIgnoreCase);
        return genres;
    }

}