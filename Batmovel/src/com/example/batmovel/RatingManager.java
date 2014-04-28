package com.example.batmovel;

import java.util.ArrayList;

import org.json.JSONObject;

public class RatingManager {
	    
	    JSONObject interestsJ;
	    JSONObject offersJ;
	    ArrayList<Ride> offers;
	    ArrayList<Interest> interests;

		public void downloadJsons (){
			WebClient interestsW = new WebClient("http://uspservices.deusanyjunior.dj/interesseemcarona/1.json");
			WebClient offersW = new WebClient("http://uspservices.deusanyjunior.dj/carona/1.json");
			interestsJ = interestsW.getJson();
			offersJ = offersW.getJson();
			if (interestsJ == null || offersJ == null) {
				return;
			}
			offers = Ride.listFromJsonList(offersJ);
			interests = Interest.listFromJsonList(interestsJ);
		}
		
		/* can A review B ? How many times ?*/
		//TODO com a ajuda do servidor, fazer com que os usuários possam dar reviews específicas para cada ride
		//TODO com a ajuda do servidor, verificar se a ride realmente foi dada (em que sentido ?)
		public int canReview (String nuspA, String nuspB){
			downloadJsons();
			int num_reviews = 0;
			for(int i=0; i<interests.size(); i++){
                   Interest interest = interests.get(i);
                   int ride_num = interest.ride_id;
                   Ride ride = offers.get(ride_num-1);
                   String nusp1 = interest.nusp_rider;
                   String nusp2 = ride.n_usp;
                   if (nusp1.equals(nuspA) && nusp2.equals(nuspB)) {
                	   num_reviews++;
                	   System.err.println("ride ="+ride.id);
                	   System.err.println("interest ="+interest.interest_id);
                   }
                   if (nusp2.equals(nuspA) && nusp1.equals(nuspB)){
                	   num_reviews++;  
            	       System.err.println("ride ="+ride.id);
            	       System.err.println("interest ="+interest.interest_id);
                   }
			}
			return num_reviews;
		}
}
