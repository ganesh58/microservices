package com.example.moviecatalogservice.resource;

import com.example.moviecatalogservice.data.CatalogItem;
import com.example.moviecatalogservice.data.Movie;

import com.example.moviecatalogservice.data.UserRating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;





import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogresource {
    @Autowired
    private RestTemplate restTemplate;



    @RequestMapping("/{userId}")
    @HystrixCommand(fallbackMethod="getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);
        return ratings.getUserRating().stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
        })
                .collect(Collectors.toList());
    }


//    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId) {
//        return Arrays.asList(new CatalogItem("No movie", "", 0));
//    }


}








