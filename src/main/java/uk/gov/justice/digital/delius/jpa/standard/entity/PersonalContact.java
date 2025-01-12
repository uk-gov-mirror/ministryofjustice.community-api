package uk.gov.justice.digital.delius.jpa.standard.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PERSONAL_CONTACT")
public class PersonalContact {
    @Id@Column(name = "PERSONAL_CONTACT_ID")
    private Long personalContactId;
    @Column(name = "OFFENDER_ID")
    private Long offenderId;
    @Column(name = "RELATIONSHIP")
    private String relationship;
    @Column(name = "START_DATE")
    private LocalDateTime startDate;
    @Column(name = "END_DATE")
    private LocalDateTime endDate;
    @Column(name = "ADDRESS_ID")
    private Long addressId;
    @Column(name = "PARTITION_AREA_ID")
    private Long partitionAreaId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "OTHER_NAMES")
    private String otherNames;
    @Column(name = "SURNAME")
    private String surname;
    @Column(name = "PREVIOUS_SURNAME")
    private String previousSurname;
    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;
    @Column(name = "EMAIL_ADDRESS")
    private String emailAddress;
    @Column(name = "SOFT_DELETED")
    private Long softDeleted;
    @Column(name = "ROW_VERSION")
    private Long rowVersion;
    @Column(name = "GENDER_ID")
    private Long genderId;
    @Column(name = "NOTES")
    private String notes;
    @Column(name = "TITLE_ID")
    private Long titleId;
    @ManyToOne
    @JoinColumn(name = "RELATIONSHIP_TYPE_ID")
    private StandardReference relationshipType;
    @Column(name = "CREATED_BY_USER_ID")
    private Long createdByUserId;
    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createdDatetime;
    @Column(name = "LAST_UPDATED_USER_ID")
    private Long lastUpdatedUserId;
    @Column(name = "LAST_UPDATED_DATETIME")
    private LocalDateTime lastUpdatedDatetime;
    @Column(name = "TRAINING_SESSION_ID")
    private Long trainingSessionId;


}
