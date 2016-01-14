package com.skyworld.service.dsf;


public class Customer extends User {

	private Question currentQuest;
	

	public Customer() {
		super();
		super.setUserType(UserType.CUSTOMER);
	}

	public Customer(User u) {
		super(u);
		super.setUserType(UserType.CUSTOMER);
	}

	public Question getCurrentQuest() {
		return currentQuest;
	}

	public void setCurrentQuest(Question currentQuest) {
		this.currentQuest = currentQuest;
	}
	
	
}
