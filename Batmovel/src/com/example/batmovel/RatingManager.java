package com.example.batmovel;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class RatingManager {

	ArrayList<Ride> offers;
	ArrayList<Interest> interests;

	private static User userToRate;

	public void downloadJsons (){
		WebClient interestsW = new WebClient("http://uspservices.deusanyjunior.dj/interesseemcarona/1.json");
		WebClient offersW = new WebClient("http://uspservices.deusanyjunior.dj/carona/1.json");
		JSONObject interestsJ = interestsW.getJson();
		JSONObject offersJ = offersW.getJson();
		if (interestsJ == null || offersJ == null) {
			offers = null;
			interests = null;
		}
		else {
			offers = Ride.listFromJsonList(offersJ);
			interests = Interest.listFromJsonList(interestsJ);
		}
	}

	/* can A review B ? How many times ?*/
	//TODO com a ajuda do servidor, fazer com que os usuários possam dar reviews específicas para cada ride
	//TODO com a ajuda do servidor, verificar se a ride realmente foi dada (em que sentido ?)
	public Integer numberInteractions (String nuspA, String nuspB){
		downloadJsons();
		if (interests == null || offers == null)
			return null;
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


	public static String getRatingJson(float rating, User currentuser) {
		JSONObject json;
		try{
			json = new JSONObject();
			json.put("userid", userToRate.uspNumber);
			json.put("userlogin", userToRate.stoaLogin);
			json.put("judgeid", currentuser.uspNumber);
			json.put("judgelogin", currentuser.stoaLogin);
			json.put("stars", (int)rating);
			json.put("message", "rating test");

			JSONObject wrapper = new JSONObject();
			wrapper.put("reviewuser", json.toString());

			return wrapper.toString();
		}
		catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}



	//TODO retornar Users
	public ArrayList<User> whoToTry (String nuspA){
		downloadJsons();
		if (interests == null || offers == null)
			return null;
		ArrayList<String> guys = new ArrayList<String> (); //preciso disso para a unicidade de NUSP
		ArrayList<User> users = new ArrayList<User> ();
		for(int i=0; i<interests.size(); i++){
			Interest interest = interests.get(i);
			int ride_num = interest.ride_id;
			Ride ride = offers.get(ride_num-1);
			String nusp1 = interest.nusp_rider; String login1 = interest.user_rider;
			String nusp2 = ride.n_usp; String login2 = ride.login;
			if (nusp1.equals(nuspA) ) {
				if (!guys.contains(nusp2)) {
					guys.add(nusp2);
					users.add(new User(nusp2,login2));
				}
			}
			if (nusp2.equals(nuspA)){
				if (!guys.contains(nusp1)) {
					guys.add(nusp1);
					users.add(new User(nusp1,login1));
				}
			}
		}
		return users;
	}

	public ArrayList<User> pendingReviews (User userA){
		ArrayList<User> candidates = whoToTry(userA.uspNumber);
		if (candidates == null)
			return null;
		ArrayList<User> toReview = new ArrayList<User>();
		for (int i=0; i<candidates.size();i++) {
			String nuspB = candidates.get(i).uspNumber;
			String nuspA = userA.uspNumber;
			Integer reviews = numberOfReviews(nuspA,nuspB);
			Integer interactions = numberInteractions(nuspA,nuspB);
			if (reviews == null || interactions == null)
				return null;
			if (interactions > reviews) {
				toReview.add(candidates.get(i));
			}
		}
		return toReview;
	}

	/*how many times has A reviewed B ?*/
	public Integer numberOfReviews(String nuspA, String nuspB) {
		String url = "http://uspservices.deusanyjunior.dj/avaliacaodousuario/"+nuspB+".json";
		//for (x in evaluations)
		//    if (a = x.judge && b = x.judged)
		//        cont++
		WebClient reviewsOfB_W = new WebClient(url);
		JSONObject reviewsOfB_J = reviewsOfB_W.getJson();
		Integer cont = 0;
		if (reviewsOfB_J == null)
			return null;
		ArrayList<Review> review_list = Review.listFromJsonList(reviewsOfB_J);
		for(int i=0; i<review_list.size(); i++) {
			Review review = review_list.get(i);
			if (nuspA.equals(review.judge_nusp) && nuspB.equals(review.other_nusp)){
				cont++;
			}
		}
		return cont;
	}

	protected static void prepareToRate(User u){
		userToRate = u;
	}
}
