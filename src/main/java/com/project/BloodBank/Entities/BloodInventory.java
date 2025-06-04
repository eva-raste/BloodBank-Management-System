package com.project.BloodBank.Entities;

import jakarta.persistence.*;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BloodInventory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String bloodType;

    private int quantity;
   
	public BloodInventory(String bloodType, int quantity) {
        this.bloodType = bloodType;
        this.quantity = quantity;
    }
}