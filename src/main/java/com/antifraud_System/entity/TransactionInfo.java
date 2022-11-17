package com.antifraud_System.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.hibernate.validator.constraints.LuhnCheck;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Entity
@Data
public class TransactionInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("transactionId")
    private long transactionId;

    @Positive
    private long amount;

    @NotBlank(message = "IP address cannot be blank")
    @Pattern(regexp = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")
    private String ip;

    @NotBlank(message = "card number cannot be blank")
    @Pattern(regexp = "\\d{16}")
    @LuhnCheck
    private String number;

    @NotBlank(message = "region cannot be blank")
    @Pattern(regexp = "EAP|ECA|HIC|LAC|MENA|SA|SSA")
    private String region;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Status result;
    @JsonIgnore
    private Status feedback;

    @JsonProperty("feedback")
    public String getFeedbackString() {
        return feedback == null ? "" : feedback.name();
    }

}
