package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        Optional<User> optionalUser = userRepository.findById(subscriptionEntryDto.getUserId());
        if(!optionalUser.isPresent()){
            return 0;
        }
        int amountPaid = 0;

        int screenSubscribed = subscriptionEntryDto.getNoOfScreensRequired();

        //Basic : 500 + 200 * noOfScreensSubscribed
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            amountPaid = 500 + (200 * screenSubscribed);
        }
        //Pro : 800 + 250*noOfScreensSubscribed
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
        {
            amountPaid = 800 + (250 * screenSubscribed);
        }
        //ELITE Plan : 1000 + 350*noOfScreensSubscribed
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            amountPaid = 1000 + (350 * screenSubscribed);
        }
        else
            return 0;

        subscription.setTotalAmountPaid(amountPaid);

        //mapping user->subscription
        User user = optionalUser.get();
        user.setSubscription(subscription);

        subscriptionRepository.save(subscription);

        userRepository.save(user);
        return amountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        Optional<User> userOptional = userRepository.findById(userId);
        if(!userOptional.isPresent())
            return 0;

        User user = userOptional.get();

        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE))
        {
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription = user.getSubscription();

        int screenSubscribed = subscription.getNoOfScreensSubscribed();

        int totalAmountBefore = subscription.getTotalAmountPaid();

        int amountDiff=0;
        int updatedAmount=0;

        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO))
        {
            updatedAmount = 1000 + (350 * screenSubscribed);
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(updatedAmount);
        }
        else if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            updatedAmount = 800 + (250 * screenSubscribed);
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(updatedAmount);
        }

        user.setSubscription(subscription);
        userRepository.save(user);
        subscriptionRepository.save(subscription);

        amountDiff = updatedAmount-totalAmountBefore;
        return amountDiff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int count = 0;
        for(Subscription subscription:subscriptionList){
            count += subscription.getTotalAmountPaid();
        }
        return count;
    }

}
