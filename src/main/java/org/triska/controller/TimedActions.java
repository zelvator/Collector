package org.triska.controller;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;

public class TimedActions {
	
	@Scheduled(fixedDelay=5000)
	public void repeated(){
	}
	@Scheduled(cron="0 0 0 * * ?")
	public void onSchedule(){
//		System.out.println("uz je 5 vterin?");
	}
	  @PostConstruct
	public void onStartup(){
//		System.out.println("uz je 5 vterin?");
	}
}
