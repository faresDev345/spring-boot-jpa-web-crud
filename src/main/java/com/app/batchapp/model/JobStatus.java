package com.app.batchapp.model;

public enum JobStatus {
	UNKNOWN("UNKNOWN"),STARTING("STARTING"), STARTED("STARTED"),COMPLETED( "COMPLETED"), FAILED("FAILED");
	 
private final String valeur;

private JobStatus(String valeur) {
  this.valeur = valeur;
}

public String getValeur() {
  return this.valeur;
}
}
