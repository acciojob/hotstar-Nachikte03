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
        Optional<ProductionHouse> optionalProductionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId());
        if(optionalProductionHouse.isPresent()){
            return 0;
        }
        ProductionHouse productionHouse = optionalProductionHouse.get();
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        for(WebSeries webSeries:webSeriesList){
            if(webSeries.getSeriesName().equals(webSeriesEntryDto.getSeriesName())){
                throw new Exception("Series is already present");
            }
        }
        WebSeries webSeries = new WebSeries();
        webSeries.setSeriesName(webSeries.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setProductionHouse(productionHouse);
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());


        productionHouse.setRatings(productionHouse.getRatings()+webSeriesEntryDto.getRating());
        productionHouseRepository.save(productionHouse);
        webSeriesRepository.save(webSeries);
        return webSeries.getId();
    }

}
