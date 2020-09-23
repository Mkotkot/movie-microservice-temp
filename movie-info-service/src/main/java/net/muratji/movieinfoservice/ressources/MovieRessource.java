package net.muratji.movieinfoservice.ressources;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.muratji.movieinfoservice.models.Movie;

@RestController
@RequestMapping("/movies")
public class MovieRessource {

	@RequestMapping("/{movieId}")
	public Movie getMovieInfo(@PathVariable String movieId) {
		return new Movie(movieId, "Tranformes");
	}
}
