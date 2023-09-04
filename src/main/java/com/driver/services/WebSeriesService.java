package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeriesOptional = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(webSeriesOptional!=null)
        {
            throw new Exception("Series is already present");
        }
        WebSeries newWebSeries = new WebSeries();
        newWebSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        newWebSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        newWebSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        newWebSeries.setRating(webSeriesEntryDto.getRating());

        //fetching productionHouse
        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();
        //newWebSeries.setProductionHouse(productionHouse);

        //updating production house rating
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();

        //adding web series in the productionHouse webSeries list
        webSeriesList.add(newWebSeries);
        productionHouse.setWebSeriesList(webSeriesList);

        double webSeriesRating = 0;
        int webCount = webSeriesList.size();
        for(WebSeries webSeries : webSeriesList)
        {
            webSeriesRating = webSeriesRating + webSeries.getRating();
        }
        double updatedrating = webSeriesRating/webCount;

        productionHouse.setRatings(updatedrating);

        webSeriesRepository.save(newWebSeries);

        productionHouseRepository.save(productionHouse);

        return 123;
    }

}
