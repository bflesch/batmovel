package com.example.batmovel;
public class User {
		public String uspNumber;
		public String stoaLogin;
		
		public boolean isEmpty(){
			return (uspNumber == null || stoaLogin == null);
		}
}
