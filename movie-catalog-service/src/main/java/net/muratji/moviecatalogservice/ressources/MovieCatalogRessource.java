package net.muratji.moviecatalogservice.ressources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import net.muratji.moviecatalogservice.models.CategoryItem;
import net.muratji.moviecatalogservice.models.Movie;
import net.muratji.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogRessource {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFaultBackMethod")
    public List<CategoryItem> getCatalog(@PathVariable String userId) {

        // Get all rated movie IDs
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId,
                UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            // For each movie ID, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

            /*
             * Movie movie = webClientBuilder.build() .get()
             * .uri("http://localhost:8082/movies/" + rating.getMovieId()) .retrieve()
             * .bodyToMono(Movie.class) .block();
             */


            // Put them all together
            return new CategoryItem(movie.getName(), "Transformers Description", rating.getRating());
        }).collect(Collectors.toList());

        // return Collections.singletonList(new CatalogItem("Transformers",
        // "Transformers 1", 5));
    }

    public List<CategoryItem> getFaultBackMethod(@PathVariable String userId) {
        List<CategoryItem> categoryItems = new ArrayList<>();
        categoryItems.add(new CategoryItem("Not Found For User", userId, 1));
        return categoryItems;
    }
}
