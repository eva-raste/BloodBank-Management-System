package com.project.BloodBank.Entities;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Recipient extends User {
	
    private String requiredBloodType;
    private String reasonForRequest;
    private String email;	
}