package com.project.BloodBank.Entities;

import jakarta.persistence.*;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@PrimaryKeyJoinColumn(name = "id") 
public class Donor extends User {

    private String bloodType;
    private int age;
    private double weight;
    private String gender;
    private double haemoglobinLevel;
    private boolean hasChronicDisease;
    private String email;
    private boolean readyToDonate = false;
   
}
